package com.houvven.guise.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.houvven.guise.hook.hooker.NetworkHooker
import com.houvven.guise.hook.hooker.PackageHooker
import com.houvven.guise.hook.hooker.PropertiesHooker
import com.houvven.guise.hook.hooker.ResourceConfigurationHooker
import com.houvven.guise.hook.hooker.SettingsSecureHooker
import com.houvven.guise.hook.hooker.TimezoneHooker
import com.houvven.guise.hook.hooker.WifiHooker
import com.houvven.guise.hook.hooker.location.CellHooker
import com.houvven.guise.hook.hooker.location.LocationHooker
import com.houvven.guise.hook.store.impl.SharedPreferenceModuleStore
import android.util.Log
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent


import dalvik.system.BaseDexClassLoader

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import com.houvven.guise.hook.dex.DexHelper
import de.robv.android.xposed.callbacks.XC_LoadPackage
@InjectYukiHookWithXposed(
    modulePackageName = "com.houvven.guise",
    isUsingXposedModuleStatus = true
)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        isDebug = false
        debugLog {
            tag = "GuiseHook"
        }
    }
    val TAG = "WeChatPad";

    override fun onHook() = encase {
        Log.e(TAG, "end  onHook")
        loadAppHooker()
        loadFrameworkHooker()
    }
    override fun onXposedEvent()
    {
        // Listen to the loading events of the native Xposed API
//        YukiXposedEvent.events {
//            onInitZygote {
//                // The it object is [StartupParam]
//            }
//            onHandleLoadPackage {
//                // The it object is [LoadPackageParam]
//            }
//            onHandleInitPackageResources {
//                // The it object is [InitPackageResourcesParam]
//            }
//        }
        YukiXposedEvent.onHandleLoadPackage{lpparam: XC_LoadPackage.LoadPackageParam ->
            run {
                test1(lpparam);
                if (lpparam.packageName == "android" && lpparam.processName == "android") {
//                    if (Build.VERSION.SDK_INT == VANILLA_ICE_CREAM) {
//                        com.suqi8.oshin.hook.android.corepatch.CorePatchForV()
//                            .handleLoadPackage(lpparam)
//                    }
                }
            }

        }
    }
    private fun PackageParam.loadAppHooker() {
        val store = SharedPreferenceModuleStore.Hooked(packageParam = this)
        val profiles = store.get(mainProcessName)
        val blackList = listOf("android", "com.android.phone", "com.houvven.guise")
        if (packageName in blackList) {
            return
        }
        if (!profiles.isAvailable) {
            YLog.info("No profiles for $packageName")
            return
        }
        loadApp(
            isExcludeSelf = true,
            *listOf(
                ::PackageHooker,
                ::ResourceConfigurationHooker, // 资源配置
                ::LocationHooker,  // 位置?
                ::CellHooker,
                ::SettingsSecureHooker,
                ::TimezoneHooker,
                ::NetworkHooker,// 网络
                ::WifiHooker// wifi
            ).map { it.invoke(profiles) }
                .plus(PropertiesHooker(profiles.properties))
                .toTypedArray()
        )
    }

    private fun PackageParam.loadFrameworkHooker() {
//        loadSystem {
//            loadHooker(SysLocationHooker())
//        }
    }
    private fun test1(lpparam: LoadPackageParam) {

        val findClassIfExists = XposedHelpers.findClassIfExists(
            "com.tencent.tinker.loader.app.TinkerApplication",
            lpparam.classLoader
        )
        if (findClassIfExists != null) {
            try {
                XposedHelpers.findAndHookMethod(
                    findClassIfExists,
                    "getTinkerFlags",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            try {
                                param.result = 0
                            } catch (th: Throwable) {
                                val member = param.method
                                Log.e(TAG, "Error occurred calling hooker on $member")
                            }
                        }
                    })
            } catch (th: Throwable) {
                Log.e(TAG, "$th")
            }
        } else {
            return;
        }
        val baseDexClassLoader: BaseDexClassLoader?
        var classLoader = lpparam.classLoader
        while (true) {
            if (classLoader !is BaseDexClassLoader) {
                if (classLoader.parent == null) {
                    baseDexClassLoader = null
                    break
                } else {
                    classLoader = classLoader.parent
                }
            } else {
                baseDexClassLoader = classLoader
                break
            }
        }
        if (baseDexClassLoader != null) {
            val dexHelper = DexHelper(baseDexClassLoader)
            val findMethodUsingString = dexHelper.findMethodUsingString(
                "Lenovo TB-9707F",
                true,
                -1L,
                (-1).toShort(),
                null,
                -1L,
                null,
                null,
                null,
                true
            )
            val methodIdx = if (findMethodUsingString.isEmpty()) null else findMethodUsingString[0]
            if (methodIdx != null) {
                val decodeMethodIndex = dexHelper.decodeMethodIndex(methodIdx)
                XposedBridge.hookMethod(decodeMethodIndex, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = true
                    }
                })
            }
        }
    }
}
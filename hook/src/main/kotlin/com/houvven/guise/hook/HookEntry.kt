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

    override fun onHook() = encase {
        loadAppHooker()
        loadFrameworkHooker()
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

}
package com.houvven.guise.hook.hooker

import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.util.type.WifiManagerClass

internal class WifiHooker(private val profiles: HookProfiles) : YukiBaseHooker() {

    override fun onHook() {
        hookWifiState()
        hookConnectionInfo()
    }

    private fun hookConnectionInfo() {
        val (wifiSsid, wifiBssid, wifiMac) = profiles.run {
            listOf(wifiSsid, wifiBssid, wifiMac)
            //if (disableWifiLocation) listOf(UNKNOWN_SSID, DEFAULT_MAC_ADDRESS, DEFAULT_MAC_ADDRESS)
            //else listOf(wifiSsid, wifiBssid, wifiMac)
        }

        // 已连接的wifi 名称
        wifiSsid?.let {
            classOf<WifiInfo>().method { name = "getSSID" }.hook().replaceTo("\"$it\"")
        }

        // hook 获取当前已经连接的wifi 信息  自身wifi 硬件的mac 和WiFi的mac地址
        WifiManagerClass.method {
            name = "getConnectionInfo"
        }.hook().after {
            result?.current {
                wifiBssid?.let { field { name = "mBSSID" }.set(it) }
                wifiMac?.let { field { name = "mMacAddress" }.set(it) }

            }
        }
    }

    private fun hookWifiState() {
        // 忽悠软件我开启了wifi 使用的wifi 网络
        if (profiles.networkType == NetworkCapabilities.TRANSPORT_WIFI) {
            WifiManagerClass.method {
                name = "getWifiState"
            }.hook().replaceTo(WifiManager.WIFI_STATE_ENABLED)
        }
    }

    companion object {
        // 默认值
        const val UNKNOWN_SSID = "<unknown ssid>"
        const val DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00"
    }
}
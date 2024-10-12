package com.houvven.guise.data.repository.profile

import android.annotation.SuppressLint
import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import com.houvven.guise.R
import com.houvven.guise.data.domain.ProfileSuggest
import com.houvven.guise.hook.util.type.TelephonyManagerClass
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Field

private typealias LocalRepo<T> = ProfilesSuggestRepo.Local<T>

object MobileNetworkTypeRepo : LocalRepo<Int> {

    override fun get(): List<ProfileSuggest<out Int>> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("Landroid/telephony/TelephonyManager;")
        }
        val types = getNetworkTypes()
        return types.map { type ->
            val name = getNetworkTypeName(type)
            ProfileSuggest(name, type)
        }
    }

    @SuppressLint("PrivateApi")
    @Suppress("UNCHECKED_CAST")
    private fun getNetworkTypes(): IntArray {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val fields = HiddenApiBypass.getStaticFields(TelephonyManagerClass) as List<Field>
            fields.find { it.name == "NETWORK_TYPES" }?.run {
                isAccessible = true
                get(null) as IntArray?
            } ?: intArrayOf()
        } else {
            val bb = "NETWORK_TYPES"; // api27 禁止调用getDeclaredField
            
            if(bb.equals(bb)){
                // Reflective access to NETWORK_TYPES is forbidden when targeting API 27 and above More... (Ctrl+F1)
                //Inspection info:Usage of restricted non-SDK interface is forbidden for this targetSDK. Accessing non-SDK methods or fields through reflection has a high likelihood to break your app between versions, and is being restricted to facilitate future app compatibility.  Issue id: BlockedPrivateApi
                // TelephonyManagerClass.getDeclaredField("NETWORK_TYPES").get(null) as IntArray
                TelephonyManagerClass.getDeclaredField(bb).get(null) as IntArray
            }else{


                 intArrayOf()
            }




        }
    }

    @SuppressLint("PrivateApi")
    private fun getNetworkTypeName(type: Int): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.invoke(
                /* clazz = */         TelephonyManagerClass,
                /* thiz = */          null,
                /* methodName = */    "getNetworkTypeName",
                /* ...args = */       type
            ) as String
        } else {
            val method = TelephonyManagerClass.getDeclaredMethod("getNetworkTypeName")
            method.invoke(null, type) as String
        }
    }
}

class NetworkType(context: Context) : LocalRepo<Int> {

    private val networkTypes by lazy {
        ProfileSuggest.create(
            context,
            R.string.network_type_none to -1,
            R.string.network_type_cellular to NetworkCapabilities.TRANSPORT_CELLULAR,
            R.string.network_type_wifi to NetworkCapabilities.TRANSPORT_WIFI,
            R.string.network_type_bluetooth to NetworkCapabilities.TRANSPORT_BLUETOOTH,
            R.string.network_type_vpn to NetworkCapabilities.TRANSPORT_VPN,
            R.string.network_type_ethernet to NetworkCapabilities.TRANSPORT_ETHERNET
        ).toMutableList().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(ProfileSuggest("USB", NetworkCapabilities.TRANSPORT_USB))
            }
        }
    }

    override fun get(): List<ProfileSuggest<out Int>> {
        return networkTypes
    }
}
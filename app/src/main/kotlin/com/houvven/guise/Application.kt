package com.houvven.guise

import android.util.Log
import com.houvven.guise.client.LServiceBridgeClient
import com.houvven.guise.hook.ModuleStatus
import com.houvven.guise.injection.commonModule
import com.houvven.guise.injection.profileSuggestModule
import com.houvven.guise.injection.storeModule
import com.houvven.guise.injection.viewModelModule
import com.houvven.guise.util.MY_USER_ID
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


typealias AndroidApplication = android.app.Application

class Application : AndroidApplication() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "uid: $MY_USER_ID")
        Log.d(TAG, "module_active: ${ModuleStatus.isModuleActive}")
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                commonModule,
                storeModule,
                viewModelModule,
                profileSuggestModule
            )
        }

        if (ModuleStatus.isLSPosedExecutor) LServiceBridgeClient.start(this)
    }

    companion object {
        const val TAG = "GuiseApplication"
    }
}
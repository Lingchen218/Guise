package com.houvven.guise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.houvven.guise.ui.ApplicationObj
import com.houvven.guise.ui.GuiseApp
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application
        // 如果是自定义 Application 类
        if (app is Application) {
            var androidApp = app as Application
            // 调用自定义方法或属性
            // androidApp
            ApplicationObj = androidApp
        }
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                GuiseApp(activityMain = this)
            }
        }
    }
}
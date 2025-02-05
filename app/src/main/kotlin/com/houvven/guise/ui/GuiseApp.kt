package com.houvven.guise.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.houvven.guise.R
import com.houvven.guise.client.LServiceBridgeClient
import com.houvven.guise.ui.theme.AppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import com.houvven.guise.Application
import com.houvven.guise.MainActivity


val LocalNavHostController: ProvidableCompositionLocal<NavHostController> =
    staticCompositionLocalOf { error("Not provided") }

val LocalSnackBarHostState: ProvidableCompositionLocal<SnackbarHostState> =
    staticCompositionLocalOf { error("Not provided") }
var ApplicationObj: Application? = null
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GuiseApp(
    navController: NavHostController = rememberNavController(),
    hostState: SnackbarHostState = remember { SnackbarHostState() },
    activityMain: MainActivity? = null
) {
    AppTheme {
        CompositionLocalProvider(
            LocalNavHostController provides navController,
            LocalSnackBarHostState provides hostState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = {
                    SnackbarHost(hostState = LocalSnackBarHostState.current) {
                        Snackbar(snackbarData = it)
                    }
                }
            ) {
                DestinationsNavHost(
                    navController = LocalNavHostController.current,
                    navGraph = NavGraphs.root
                )

                LServiceErrorStatusDia(activityMain)
            }
        }
    }
}


@Composable
private fun LServiceErrorStatusDia(activityMain: MainActivity? = null) {
    val lServiceBridgeStatus by LServiceBridgeClient.statusFlow.collectAsStateWithLifecycle()
    val error = lServiceBridgeStatus as? LServiceBridgeClient.Status.Error
    var iSee by remember { mutableStateOf(false) }
    val visible = error != null && !iSee
    var text by remember { mutableStateOf("su") }
    if (visible) AlertDialog(
        onDismissRequest = { iSee = true },
        confirmButton = {},
        title = { Text(text = stringResource(id = R.string.lservice_error_title)) },
        text = {
            Column {
                Text(text = stringResource(id = error!!.messageResId))
                Text(text = stringResource(id = R.string.lservice_description))
                Text("请检测root权限是否给予")
                // 分割线
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), // 上下间距
                    thickness = 1.dp,  // 分割线厚度
                    color = Color.Gray // 分割线颜色
                )
                // 输入框和按钮的水平布局
                Row(
                    verticalAlignment = Alignment.CenterVertically, // 垂直居中对齐
                    horizontalArrangement = Arrangement.SpaceBetween, // 水平分布
                    modifier = Modifier.fillMaxWidth() // 占满宽度
                ) {
                    // 输入框
                    TextField(
                        value = text, // 绑定输入框内容
                        onValueChange = { newText ->
                            text = newText // 更新输入框内容
                        },
                        label = { Text("请输入su路径") }, // 输入框的标签
                        modifier = Modifier.weight(1f) // 输入框占满剩余空间

                    )
                    Button(onClick = {
                        // 点击按钮时获取输入框的内容
                        val inputText = text
                        var con =  ApplicationObj as Context
                        val sharedPreferences = con.getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("username", inputText)
                        editor.putBoolean("notifications_enabled", true)
                        editor.apply()
                        activityMain?.finish() // 关闭当前 Activity
                        activityMain?.finishAffinity() // 关闭当前任务栈中的所有 Activity
                        // System.exit(0) // 终止应用程序进程

                    }) {
                        Text("确认")
                    }
                }
            }
        },
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.errorContainer
    )

    LaunchedEffect(error) {
        if (error != null && iSee) {
            iSee = false
        }
    }
}
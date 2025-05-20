package com.bianca.clock.ui.normal
import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * 通用權限處理元件
 */
@Composable
fun PermissionHandler(
    permissions: List<String>,
    onAllGranted: () -> Unit = {},
    onDenied: (List<String>) -> Unit = {}
) {
    val context = LocalContext.current
    var grantedPermissions by remember { mutableStateOf(checkGrantedPermissions(context, permissions)) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        grantedPermissions = result.filterValues { it }.keys.toList()
        val denied = permissions - grantedPermissions.toSet()
        if (denied.isEmpty()) onAllGranted() else onDenied(denied)
    }

    LaunchedEffect(Unit) {
        val notGranted = permissions - grantedPermissions.toSet()
        if (notGranted.isNotEmpty()) {
            launcher.launch(notGranted.toTypedArray())
        } else {
            onAllGranted()
        }
    }
}

fun checkGrantedPermissions(context: Context, permissions: List<String>): List<String> {
    return permissions.filter { permission ->
        when (permission) {
            Manifest.permission.POST_NOTIFICATIONS -> {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
            else -> ContextCompat.checkSelfPermission(
                context,
                permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = android.net.Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Composable
fun CameraPermissionRationaleDialog(onDismiss: () -> Unit = {}) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("需要相機權限") },
        text = { Text("我們需要相機權限來提供完整功能，請至設定中開啟權限。") },
        confirmButton = {
            TextButton(onClick = {
                openAppSettings(context)
                onDismiss()
            }) {
                Text("前往設定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

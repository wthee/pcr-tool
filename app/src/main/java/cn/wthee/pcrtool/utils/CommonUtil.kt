package cn.wthee.pcrtool.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import cn.wthee.pcrtool.MyApplication

/**
 * 权限校验
 */
fun hasPermissions(context: Context, permissions: Array<String>) = permissions.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * 添加 [str] 到系统剪切板
 */
fun add(str: String, tipText: String = "内容已复制~") {
    val clipboardManager =
        MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val mClipData = ClipData.newPlainText("OcrText", str)
    clipboardManager.setPrimaryClip(mClipData)
    ToastUtil.short(tipText)
}

/**
 * 在浏览器中打开 [url]
 */
fun openWebView(context: Context, url: String, title: String = "请选择浏览器") {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.data = Uri.parse(url)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(Intent.createChooser(intent, title))
}
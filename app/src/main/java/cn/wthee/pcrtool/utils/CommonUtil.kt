package cn.wthee.pcrtool.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import cn.wthee.pcrtool.MyApplication
import java.util.*

/**
 * 权限校验
 */
fun hasPermissions(context: Context, permissions: Array<String>) = permissions.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * 添加 [str] 到系统剪切板
 */
fun addToClip(str: String, tipText: String = "内容已复制~") {
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


/**
 *  把 - 拼接的字符串，转化为数组
 */
fun String.intArrayList(): List<Int> {
    val list = arrayListOf<Int>()
    val ids = this.split("-")
    ids.forEachIndexed { _, id ->
        if (id != "") {
            list.add(id.toInt())
        }
    }
    return list
}

fun List<Int>.fillPlaceholder(): ArrayList<Int> {
    val list = arrayListOf<Int>()
    list.addAll(this)
    if (this.size % 6 != 0) {
        for (i in 0 until 6 - this.size % 6) {
            list.add(0)
        }
    }
    return list
}

/**
 * 去除空格等无用字符
 */
fun String.deleteSpace() = this.replace("\\s".toRegex(), "")

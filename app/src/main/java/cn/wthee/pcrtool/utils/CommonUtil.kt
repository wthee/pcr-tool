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
 * 添加文本 [str] 系统剪切板
 *
 * @param str 文本内容
 * @param tipText 复制提示
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
 *
 * @param url 链接
 * @param title 标题
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

/**
 * 去除空格等无用字符
 */
fun String.deleteSpace() = this.replace("\\s".toRegex(), "")

/**
 * [Double] 转 [Int]，四舍五入
 */
val Double.int: Int
    get() {
        return (this + 0.5).toInt()
    }
package cn.wthee.pcrtool.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * 浏览器打开
 */
object BrowserUtil {

    /**
     * 在浏览器中打开 [url]
     */
    fun OpenWebView(context: Context, url: String, title: String = "请选择浏览器") {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, title))
    }
}
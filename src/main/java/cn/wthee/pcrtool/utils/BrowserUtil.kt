package cn.wthee.pcrtool.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri


object BrowserUtil {

    fun open(context: Context, url: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, "请选择浏览器"))
    }
}
package cn.wthee.pcrtool.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import cn.wthee.pcrtool.MyApplication

object ClipboardUtli {

    fun add(str: String) {
        val clipboardManager =
            MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("OcrText", str)
        clipboardManager.setPrimaryClip(mClipData)
        ToastUtil.short("已复制链接~")
    }
}
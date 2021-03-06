package cn.wthee.pcrtool.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import cn.wthee.pcrtool.MyApplication

/**
 * 剪切板
 */
object ClipboardUtil {

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
}
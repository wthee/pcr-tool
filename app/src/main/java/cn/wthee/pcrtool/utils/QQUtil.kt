package cn.wthee.pcrtool.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import cn.wthee.pcrtool.R


/**
 * 发起添加群流程
 */
fun joinQQGroup(context: Context) {
    val intent = Intent()
    intent.data =
        Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3DZd7PrbHQ2vKCf2NQsoGWuqHrl54MJErV")
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        //复制群号
        val qqGroup = "775966246"
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("OcrText", qqGroup)
        clipboardManager.setPrimaryClip(mClipData)
        ToastUtil.short(getString(R.string.copy_qq_group, qqGroup))
    }
}
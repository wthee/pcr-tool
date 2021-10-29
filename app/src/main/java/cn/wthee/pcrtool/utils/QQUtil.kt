package cn.wthee.pcrtool.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri


/****************
 *
 * 发起添加群流程。群号：214141356 的 key 为：
 * 调用 joinQQGroup(F5P739Sbo-Y5Rg90oK4ZMoDaV2tMYf0h) 即可发起手Q客户端申请加群 PCR Tool 反馈交流(214141356)
 *
 * @return 返回true表示呼起手Q成功，返回false表示呼起失败
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
        ToastUtil.short("已复制QQ群号：$qqGroup")
    }
}
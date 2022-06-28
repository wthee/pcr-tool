package cn.wthee.pcrtool.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.ceil
import kotlin.math.floor


/**
 * 权限校验
 */
private fun hasPermissions(context: Context, permissions: Array<String>) = permissions.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * 存储权限
 */
fun checkPermissions(
    context: Context,
    permissions: Array<String>,
    sdkCheck: Boolean = true,
    action: () -> Unit
) {

    if (!sdkCheck) {
        //不校验sdk
        if (!hasPermissions(context, permissions)) {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissions,
                1
            )
        } else {
            action()
        }
    } else {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !hasPermissions(
                context,
                permissions
            )
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissions,
                1
            )
        } else {
            action()
        }
    }

}

/**
 *  把 - 拼接的字符串，转化为数组
 */
val String.intArrayList: List<Int>
    get() {
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
val String.deleteSpace: String
    get() {
        return this.replace("\\s".toRegex(), "").replace("\\n", "").replace("\n", "")
    }

/**
 * [Double] 转 [Int]，四舍五入
 */
val Double.int: Int
    get() {
        return if (this * 10 % 10 > 1) ceil(this).toInt() else floor(this).toInt()
    }

/**
 * 复制
 */
fun copyText(context: Context, text: String) {
    //复制群号
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val mClipData = ClipData.newPlainText("OcrText", text)
    clipboardManager.setPrimaryClip(mClipData)
    ToastUtil.short("已复制~")
}
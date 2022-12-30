package cn.wthee.pcrtool.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
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
 *  把 - 拼接的字符串，转化为 Int 数组
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

val String.stringArrayList: List<String>
    get() = this.split("-").filter { it != "" }


/**
 * 去除空格等无用字符
 */
val String.deleteSpace: String
    get() {
        return this.replace("\\s".toRegex(), "").replace("\\n", "").replace("\n", "")
    }

/**
 * [Double] 转 [Int]，小数非0进位
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
    ToastUtil.short(getString(R.string.copy_success))
}

/**
 * 服务器版本名称
 */
fun getRegionName(region: Int) = getString(
    when (region) {
        2 -> R.string.db_cn
        3 -> R.string.db_tw
        4 -> R.string.db_jp
        else -> R.string.all
    }
)

/**
 * 获取文本
 */
fun getString(resId: Int, vararg formatArgs: Any) =
    MyApplication.context.getString(resId, *formatArgs)

/**
 * 格式化文本
 * 999 -> ?
 */
val String.fixedStr: String
    get() = if (this == "999") Constants.UNKNOWN else this

/**
 * Rank 格式化
 */
fun getFormatText(rank: Int, preStr: String = Constants.RANK_UPPER): String {
    val text = when (rank) {
        in 0..9 -> "  $rank"
        else -> "$rank"

    }
    return "$preStr $text"
}

/**
 * 阶段格式化
 */
@Composable
fun getZhNumberText(section: Int): String {
    return when (section) {
        1 -> stringResource(R.string.no1)
        2 -> stringResource(R.string.no2)
        3 -> stringResource(R.string.no3)
        4 -> stringResource(R.string.no4)
        5 -> stringResource(R.string.no5)
        6 -> stringResource(R.string.no6)
        7 -> stringResource(R.string.no7)
        else -> section.toString()
    }
}

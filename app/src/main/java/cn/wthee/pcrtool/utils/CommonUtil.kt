package cn.wthee.pcrtool.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
fun checkPermissions(context: Context, action: () -> Unit) {
    //权限
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    //权限校验
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !hasPermissions(context, permissions)) {
        ActivityCompat.requestPermissions(
            context as Activity,
            permissions,
            1
        )
    } else {
        action.invoke()
    }
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
        return this.replace("\\s".toRegex(), "")
    }

/**
 * [Double] 转 [Int]，四舍五入
 */
val Double.int: Int
    get() {
        return if (this * 10 % 10 > 1) ceil(this).toInt() else floor(this).toInt()
    }

val Double.format: String
    get() {
        return String.format("%.1f", this)
    }
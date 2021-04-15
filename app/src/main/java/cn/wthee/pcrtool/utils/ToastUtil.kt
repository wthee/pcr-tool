package cn.wthee.pcrtool.utils

import android.widget.Toast
import cn.wthee.pcrtool.MyApplication

/**
 * Toast 工具
 */
object ToastUtil {

    fun short(message: String?) {
        Toast.makeText(MyApplication.context, message, Toast.LENGTH_SHORT).show()
    }

    fun long(message: String?) {
        Toast.makeText(MyApplication.context, message, Toast.LENGTH_LONG).show()
    }

}
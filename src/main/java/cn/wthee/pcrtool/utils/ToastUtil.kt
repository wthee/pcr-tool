package cn.wthee.pcrtool.utils

import android.widget.Toast
import cn.wthee.pcrtool.MyApplication

object ToastUtil {

    fun short(message: String?) {
        Toast.makeText(MyApplication.context, message, Toast.LENGTH_SHORT).show()
    }

    fun long(message: String?) {
        Toast.makeText(MyApplication.context, message, Toast.LENGTH_LONG).show()
    }

}
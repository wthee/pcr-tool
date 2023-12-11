package cn.wthee.pcrtool.utils

import android.os.Looper
import android.widget.Toast
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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

    fun launchShort(message: String?) {
        MainScope().launch(Dispatchers.IO) {
            Looper.prepare()
            short(message)
            Looper.loop()
        }
    }
}
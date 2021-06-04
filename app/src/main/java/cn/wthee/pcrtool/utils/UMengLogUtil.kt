package cn.wthee.pcrtool.utils

import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object UMengLogUtil {

    fun upload(e: Exception, msg: String) {
        MainScope().launch {
            UMCrash.generateCustomLog(e, msg)
        }
    }
}
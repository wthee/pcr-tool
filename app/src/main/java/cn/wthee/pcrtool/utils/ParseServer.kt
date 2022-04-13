package cn.wthee.pcrtool.utils

import android.util.Log
import cn.wthee.pcrtool.BuildConfig
import com.parse.ParseObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object ParseServer {

    fun upload(e: Exception, msg: String) {
        MainScope().launch {
            if (BuildConfig.DEBUG) {
                Log.e("DEBUG", msg + e.message)
            }
            ParseObject("AppLog").apply {
                put("type", msg)
                put("log", e.toString())
                saveInBackground()
            }
        }
    }
}
package cn.wthee.pcrtool.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceUtil {

    fun getMain(): SharedPreferences =
        ActivityUtil.instance.currentActivity!!.getSharedPreferences("main", Context.MODE_PRIVATE)
}
package cn.wthee.pcrtool

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //获取Context
        context = applicationContext
    }

    companion object {
        private lateinit var context: Context
        fun getContext() = context
    }
}
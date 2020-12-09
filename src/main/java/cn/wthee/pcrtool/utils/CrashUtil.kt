package cn.wthee.pcrtool.utils

import android.os.Handler
import android.os.Looper
import android.view.Gravity

/**
 * 异常捕获
 */
class CrashUtil private constructor() {

    private fun setCrashHandler() {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    showDialog(e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            showDialog(e)
        }
    }

    private fun showDialog(e: Throwable) {
        var error = e.message ?: return
        if (error.contains("wthee.xyz"))
            error = "无法访问服务器，请稍后重试~"
        if (!error.contains("Remote key")) {
            val builder = android.app.AlertDialog.Builder(ActivityUtil.instance.currentActivity)
            val dialog = builder.create()
            builder.setTitle("错误日志:")
            builder.setMessage(error)
            dialog.window?.attributes?.gravity = Gravity.BOTTOM
            builder.setPositiveButton(
                "复制异常信息"
            ) { _, which ->
                ClipboardUtli.add(error + "\n" + e.stackTraceToString())
            }
            builder.setNegativeButton(
                "关闭"
            ) { dl, which ->
                dl.dismiss()
            }
            builder.setCancelable(false)
            builder.show()
        }
    }

    companion object {
        private var mInstance: CrashUtil? = null
        private val instance: CrashUtil?
            get() {
                if (mInstance == null) {
                    synchronized(CrashUtil::class.java) {
                        if (mInstance == null) {
                            mInstance = CrashUtil()
                        }
                    }
                }
                return mInstance
            }

        fun init() {
            instance!!.setCrashHandler()
        }
    }
}
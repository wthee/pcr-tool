package cn.wthee.pcrtool.utils


import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

/**
 * 全局获取 activity
 */
class ActivityUtil private constructor() {

    private var sCurrentActivityWeakRef: WeakReference<AppCompatActivity>? = null

    var currentActivity: AppCompatActivity?
        get() {
            var currentActivity: AppCompatActivity? = null
            if (sCurrentActivityWeakRef != null) {
                currentActivity = sCurrentActivityWeakRef!!.get()
            }
            return currentActivity
        }
        set(activity) {
            sCurrentActivityWeakRef = WeakReference<AppCompatActivity>(activity)
        }


    companion object {

        val instance = ActivityUtil()
    }


}
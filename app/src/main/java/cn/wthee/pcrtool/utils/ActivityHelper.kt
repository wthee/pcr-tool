package cn.wthee.pcrtool.utils


import androidx.activity.ComponentActivity
import java.lang.ref.WeakReference

/**
 * 全局获取 activity
 */
class ActivityHelper private constructor() {

    private var sCurrentActivityWeakRef: WeakReference<ComponentActivity>? = null

    var currentActivity: ComponentActivity?
        get() {
            var currentActivity: ComponentActivity? = null
            if (sCurrentActivityWeakRef != null) {
                currentActivity = sCurrentActivityWeakRef!!.get()
            }
            return currentActivity
        }
        set(activity) {
            sCurrentActivityWeakRef = WeakReference<ComponentActivity>(activity)
        }


    companion object {

        val instance = ActivityHelper()
    }


}
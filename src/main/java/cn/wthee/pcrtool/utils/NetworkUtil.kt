package cn.wthee.pcrtool.utils

import java.io.IOException


/**
 * 网络状态校验
 */
object NetworkUtil {

    /**
     * 网络是否正常
     */
    fun isEnable(): Boolean {
        try {
            val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 100 wthee.xyz")
            val status = process.waitFor()
            if (status == 0)
                return true
            return false
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }
}

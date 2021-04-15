package cn.wthee.pcrtool.utils

import android.util.Log
import cn.wthee.pcrtool.data.network.MyAPIRepository

/**
 * 应用更新
 */
object AppUpdateUtil {

    /**
     * 校验版本
     */
    suspend fun init(): Boolean {
        try {
            val version = MyAPIRepository.getInstance().getAppUpdateNotice()
            if (version.status == 0) {
                if (version.data != null && version.data == true) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message ?: "")
        }
        return false
    }

}
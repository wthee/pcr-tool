package cn.wthee.pcrtool.utils

import android.app.Activity
import android.util.Log
import androidx.navigation.findNavController
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.utils.ResourcesUtil.getString

/**
 * 应用更新
 */
object AppUpdateUtil {

    /**
     * 校验版本
     */
    suspend fun init(activity: Activity, showToast: Boolean = false) {
        try {
            if (NetworkUtil.isEnable()) {
                val version = MyAPIRepository.getInstance().getAppUpdateNotice()
                if (version.status == 0) {
                    if (version.data != null && version.data == true) {
                        //有新版本发布，弹窗
                        if (showToast) {
                            activity.findNavController(R.id.nav_host_fragment)
                                .navigate(R.id.action_global_noticeListFragment)
                        } else {
                            MainActivity.fabNotice.show()
                        }
                    } else if (showToast) {
                        ToastUtil.short("应用已是最新版本~")
                    }
                }
            } else {
                ToastUtil.short(getString(R.string.network_error))
            }
        } catch (e: Exception) {
            MainActivity.fabNotice.hide()
            Log.e(Constants.LOG_TAG, e.message ?: "")
        }

    }

}
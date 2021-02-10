package cn.wthee.pcrtool.utils

import android.util.Log
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.utils.ResourcesUtil.getString
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 应用更新
 */
object AppUpdateUtil {

    /**
     * 校验版本
     */
    suspend fun init() {
        try {
            if (NetworkUtil.isEnable()) {
                val version = MyAPIRepository.getInstance().getAppUpdateNotice()
                if (version.status == 0) {
                    if (version.data != null && version.data == true) {
                        MainScope().launch {
                            MainActivity.fabNotice.show()
                        }
                    }
                }
            } else {
                ToastUtil.short(getString(R.string.network_error))
            }
        } catch (e: Exception) {
            MainScope().launch {
                MainActivity.fabNotice.hide()
            }
            Log.e(Constants.LOG_TAG, e.message ?: "")
        }

    }

}
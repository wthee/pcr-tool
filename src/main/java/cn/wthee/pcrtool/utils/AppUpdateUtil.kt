package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.service.MyAPIService
import cn.wthee.pcrtool.utils.ResourcesUtil.getString

/**
 * 应用更新
 */
object AppUpdateUtil {

    /**
     * 校验版本
     */
    suspend fun init(showToast: Boolean = false) {
        val service = ApiUtil.create(
            MyAPIService::class.java,
            Constants.API_URL
        )
        try {
            if (NetworkUtil.isEnable()) {
                val version = service.toUpdate()
                if (version.message == "success") {
                    if (version.data == true) {
                        //有新版本发布，弹窗
                        MainActivity.fabNotice.show()
                    } else if (showToast) {
                        ToastUtil.short("应用已是最新版本~")
                    }
                }
            } else {
                ToastUtil.short(getString(R.string.network_error))
            }
        } catch (e: Exception) {

        }

    }

}
package cn.wthee.pcrtool.utils

import android.annotation.SuppressLint
import com.tbruyelle.rxpermissions2.RxPermissions

object PermissionsUtil {
    //权限申请
    @SuppressLint("CheckResult")
    fun request(afterRequest: AfterRequest, vararg permissions: String) {
        val rxPermissions = RxPermissions(ActivityUtil.instance.currentActivity!!)
        rxPermissions
            .request(*permissions)
            .subscribe { isDisposed ->
                if (isDisposed) {
                    afterRequest.allow()
                } else {
                    afterRequest.deny()
                }
            }
    }

    //授权回调
    interface AfterRequest {
        fun allow()
        fun deny()
    }
}
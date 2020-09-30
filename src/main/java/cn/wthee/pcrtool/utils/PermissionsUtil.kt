package cn.wthee.pcrtool.utils

object PermissionsUtil {

    fun requestPermission(onPermission: OnPermission, vararg permissions: String){

    }

    interface OnPermission{
        fun allow()
        fun deny()
    }

}
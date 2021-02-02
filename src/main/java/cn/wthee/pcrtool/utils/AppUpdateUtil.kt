package cn.wthee.pcrtool.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.service.MyAPIService
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import cn.wthee.pcrtool.utils.ResourcesUtil.getString

/**
 * 应用更新
 */
object AppUpdateUtil {

    /**
     * 校验版本
     */
    suspend fun init(context: Context, inflater: LayoutInflater, showToast: Boolean = false) {
        val service = ApiUtil.create(
            MyAPIService::class.java,
            Constants.API_URL
        )
        try {
            if (NetworkUtil.isEnable()) {
                val version = service.getAppVersion()
                if (version.message == "success") {
                    if (BuildConfig.VERSION_CODE < version.data!!.versionCode) {
                        //有新版本发布，弹窗
                        DialogUtil.create(
                            context,
                            LayoutWarnDialogBinding.inflate(inflater),
                            "版本更新：${BuildConfig.VERSION_NAME} > ${version.data!!.versionName} ",
                            version.data!!.content,
                            "暂不更新",
                            "前往下载",
                            object : DialogListener {
                                override fun onCancel(dialog: AlertDialog) {
                                    dialog.dismiss()
                                }

                                override fun onConfirm(dialog: AlertDialog) {
                                    BrowserUtil.open(context, version.data!!.url)
                                }
                            }).show()

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
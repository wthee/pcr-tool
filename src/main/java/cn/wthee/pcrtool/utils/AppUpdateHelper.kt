package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import cn.wthee.pcrtool.data.service.MyAPIService
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object AppUpdateHelper {

    fun init(context: Context, inflater: LayoutInflater, showToast: Boolean = false) {
        try {
            //本地版本
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
            val localVersion = if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
                info.longVersionCode
            } else {
                info.versionCode.toLong()
            }

            val service = ApiHelper.create(
                MyAPIService::class.java,
                Constants.API_URL
            )
            MainScope().launch {
                val version = service.getAppVersion()
                if (localVersion < version.versionCode) {
                    //有新版本发布，弹窗
                    DialogUtil.create(context,
                        LayoutWarnDialogBinding.inflate(inflater),
                        "版本更新：${info.versionName} > ${version.versionName} ",
                        version.content,
                        "暂不更新",
                        "前往下载",
                        object : DialogListener {
                            override fun onCancel(dialog: AlertDialog) {
                                dialog.dismiss()
                            }

                            override fun onConfirm(dialog: AlertDialog) {
                                BrowserUtil.open(context, version.url)
                            }
                        }).show()

                } else if (showToast) {
                    ToastUtil.short("应用已是最新版本~")
                }
            }
        } catch (e: Exception) {

        }


    }

}
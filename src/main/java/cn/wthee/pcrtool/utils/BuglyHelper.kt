package cn.wthee.pcrtool.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.TextView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.UpgradeInfo
import com.tencent.bugly.beta.ui.UILifecycleListener
import java.text.SimpleDateFormat

object BuglyHelper {

    fun init(applicationContext: Context) {
        Beta.upgradeDialogLayoutId = R.layout.layout_upgrade_dialog
        //自动检查应用更新
        Beta.autoCheckUpgrade = MainActivity.spSetting.getBoolean("auto_update_app", true)
        Beta.smallIconId = R.drawable.ic_logo
        Beta.largeIconId = R.mipmap.ic_launcher
        Beta.upgradeDialogLifecycleListener = object : UILifecycleListener<UpgradeInfo?> {
            @SuppressLint("RestrictedApi")
            override fun onCreate(context: Context?, view: View, upgradeInfo: UpgradeInfo?) {
                Log.d("upgrade", "onCreate")
                if (upgradeInfo != null) {
                    //添加徽标
                    val info = view.findViewWithTag<View>("info") as TextView
                    val labelFeature = view.findViewWithTag<View>("label_feature") as TextView
                    //获取更新信息
                    val versionName = upgradeInfo.versionName.toString()
                    val fileSize = String.format("%.2f", upgradeInfo.fileSize / 1024f / 1024f)
                    val infoText = "${MainActivity.nowVersionName} > $versionName   ${fileSize}M"
                    val spaned = SpannableStringBuilder(infoText)
                    info.text = spaned
                    labelFeature.text =
                        SimpleDateFormat("yyyy-MM-dd").format(upgradeInfo.publishTime)
                }
            }

            override fun onStart(context: Context?, view: View?, upgradeInfo: UpgradeInfo?) {
                Log.d("upgrade", "onStart")
            }

            override fun onResume(context: Context?, view: View?, upgradeInfo: UpgradeInfo?) {
                Log.d("upgrade", "onResume")
            }

            override fun onPause(context: Context?, view: View?, upgradeInfo: UpgradeInfo?) {
                Log.d("upgrade", "onPause")
            }

            override fun onStop(context: Context?, view: View?, upgradeInfo: UpgradeInfo?) {
                Log.d("upgrade", "onStop")
            }

            override fun onDestroy(context: Context?, view: View?, upgradeInfo: UpgradeInfo?) {
                Log.d("upgrade", "onDestory")
            }
        }
        //初始化
        Beta.initDelay = 5000
        Bugly.init(applicationContext, PrivateData.APP_ID, false)
    }
}
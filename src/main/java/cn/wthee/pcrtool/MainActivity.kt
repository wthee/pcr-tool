package cn.wthee.pcrtool

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkManager
import cn.wthee.pcrtool.update.DatabaseUpdateHelper
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.PrivateData
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.UpgradeInfo
import com.tencent.bugly.beta.ui.UILifecycleListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        var currentCharaPosition: Int = 0
        var currentEquipPosition: Int = 0
        var currentMainPage: Int = 0
        var databaseVersion: String? = Constants.DATABASE_VERSION
        lateinit var sp: SharedPreferences
        var sortType = Constants.SORT_TYPE
        var sortAsc = Constants.SORT_ASC
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //本地储存
        sp = getSharedPreferences("main", Context.MODE_PRIVATE)
        databaseVersion = sp.getString(Constants.SP_DATABASE_VERSION, Constants.DATABASE_VERSION)
        sortType = sp.getInt(Constants.SP_SORT_TYPE, Constants.SORT_TYPE)
        sortAsc = sp.getBoolean(Constants.SP_SORT_ASC, Constants.SORT_ASC)
        //检查更新
        WorkManager.getInstance(this).cancelAllWork()
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseUpdateHelper().checkDBVersion(this@MainActivity)
        }
        //绑定
        ActivityUtil.instance.currentActivity = this

        // Bugly 设置
        Beta.upgradeDialogLayoutId = R.layout.layout_upgrade_dialog
        Beta.autoCheckUpgrade = false
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
                    val oldVersionName = packageManager.getPackageInfo(
                        packageName,
                        0
                    ).versionName
                    val versionName = upgradeInfo.versionName.toString()
                    val fileSize = String.format("%.2f", upgradeInfo.fileSize / 1024f / 1024f)
                    val infoText = "$oldVersionName > $versionName   ${fileSize}M"
                    val spaned = SpannableStringBuilder(infoText)
                    spaned.setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.colorPrimary, null)),
                        infoText.indexOfFirst { it == '>' } + 1,
                        infoText.indexOfLast { it == ' ' },
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
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
        Bugly.init(applicationContext, PrivateData.APP_ID, false)
    }

}

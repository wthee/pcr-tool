package cn.wthee.pcrtool

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
import cn.wthee.pcrtool.databinding.ActivityMainBinding
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.BuglyHelper
import cn.wthee.pcrtool.utils.Constants
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        var currentCharaPosition: Int = 0
        var currentEquipPosition: Int = 0
        var currentMainPage: Int = 0
        var databaseVersion: String? = Constants.DATABASE_VERSION
        var nowVersionName = "0.0.0"
        lateinit var sp: SharedPreferences
        lateinit var spSetting: SharedPreferences
        var sortType = Constants.SORT_TYPE
        var sortAsc = Constants.SORT_ASC
        var canBack = true

        //fab 默认隐藏
        lateinit var fab: ExtendedFloatingActionButton

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //取消其它任务
        WorkManager.getInstance(this).cancelAllWork()
        //获取版本名
        nowVersionName = packageManager.getPackageInfo(
            packageName,
            0
        ).versionName
        //本地储存
        sp = getSharedPreferences("main", Context.MODE_PRIVATE)
        databaseVersion = sp.getString(Constants.SP_DATABASE_VERSION, Constants.DATABASE_VERSION)
        sortType = sp.getInt(Constants.SP_SORT_TYPE, Constants.SORT_TYPE)
        sortAsc = sp.getBoolean(Constants.SP_SORT_ASC, Constants.SORT_ASC)
        //设置信息
        spSetting = PreferenceManager.getDefaultSharedPreferences(this)
        //检查数据库更新
        val autoUpdateDb = spSetting.getBoolean("auto_update_db", true)
        if (autoUpdateDb) {
            CoroutineScope(Dispatchers.Main).launch {
                DatabaseUpdateHelper().checkDBVersion()
            }
        }
        //悬浮按钮状态
        fab = binding.fab
        val fabExtend = spSetting.getBoolean("fab_status", false)
        if (fabExtend) fab.extend() else fab.shrink()
        //绑定活动
        ActivityUtil.instance.currentActivity = this
        // Bugly 初始设置
        BuglyHelper.init(this)

    }
    //动画执行完之前，禁止直接返回
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (!canBack && event.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }
}

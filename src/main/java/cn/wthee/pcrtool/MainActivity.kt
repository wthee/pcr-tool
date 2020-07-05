package cn.wthee.pcrtool

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.work.WorkManager
import cn.wthee.pcrtool.database.UpdateHelper
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.Constants
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
        lateinit var sp: SharedPreferences
        var sortType = Constants.SORT_TYPE
        var sortAsc = Constants.SORT_ASC
    }

    private lateinit var appBarConfiguration: AppBarConfiguration


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
            UpdateHelper().checkDBVersion(this@MainActivity)
        }
        //绑定
        ActivityUtil.instance.currentActivity = this
    }

}

package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * 设置
 *
 * 页面布局 [R.xml.root_preferences]
 *
 * ViewModels [CharacterViewModel]
 */
class MainSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        lateinit var titleDatabase: Preference
    }


    override fun onResume() {
        super.onResume()
        //添加返回fab
        FabHelper.addBackFab()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //获取控件
        titleDatabase = findPreference("title_database")!!
        val forceUpdateDb = findPreference<Preference>("force_update_db")
        val cleanDatabase = findPreference<Preference>("clean_database")
        val appUpdate = findPreference<Preference>("force_update_app")
        val shareApp = findPreference<Preference>("share_app")
        val changeDbType = findPreference<ListPreference>("change_database")
        val switchPvpRegion = findPreference<SwitchPreference>("pvp_region")
        changeDbType?.title =
            "版本：" + if (changeDbType?.value == "1") getString(R.string.db_cn) else getString(R.string.db_jp)
        switchPvpRegion?.isVisible = changeDbType?.value != "1"
        cleanDatabase?.summary = FileUtil.getOldDatabaseSize()
        //数据版本
        MainScope().launch {
            DataStoreUtil.get(
                if (changeDbType?.value == "1") Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP
            ).collect { str ->
                titleDatabase.title = getString(R.string.data) + if (str != null) {
                    str.split("/")[0]
                } else {
                    ""
                }

            }
        }
        //强制更新数据库
        forceUpdateDb?.setOnPreferenceClickListener {
            DialogUtil.create(
                requireContext(),
                LayoutWarnDialogBinding.inflate(layoutInflater),
                getString(R.string.redownload_db),
                getString(R.string.to_download),
                "取消",
                "下载数据",
                object : DialogListener {
                    override fun onCancel(dialog: AlertDialog) {
                        dialog.dismiss()
                    }

                    override fun onConfirm(dialog: AlertDialog) {
                        lifecycleScope.launch {
                            DatabaseUpdater.checkDBVersion(0, force = true)
                            dialog.dismiss()
                        }
                    }
                }).show()
            return@setOnPreferenceClickListener true
        }
        //切换数据库版本
        changeDbType?.setOnPreferenceChangeListener { _, newValue ->
            if (changeDbType.value != newValue as String) {
                if (newValue == "1") {
                    changeDbType.title = "版本：" + getString(R.string.db_cn)
                    switchPvpRegion?.isVisible = false
                } else {
                    changeDbType.title = "版本：" + getString(R.string.db_jp)
                    switchPvpRegion?.isVisible = true
                }
                MainScope().launch {
                    delay(800L)
                    DatabaseUpdater.checkDBVersion(1)
                }
            }
            return@setOnPreferenceChangeListener true
        }
        //历史数据库文件
        cleanDatabase?.setOnPreferenceClickListener {
            FileUtil.deleteOldDatabase()
            it.summary = FileUtil.getOldDatabaseSize()
            return@setOnPreferenceClickListener true
        }

        //应用更新
        appUpdate?.summary = MainActivity.nowVersionName
        appUpdate?.setOnPreferenceClickListener {
            //应用版本校验
            lifecycleScope.launch {
                ToastUtil.short("应用版本检测中...")
                AppUpdateUtil.init(requireContext(), layoutInflater, true)
            }
            return@setOnPreferenceClickListener true
        }


        //egg
        val eggs = findPreference<PreferenceCategory>("egg")
        val eggKL = findPreference<Preference>("kl")
        //是否显示
        eggs?.isVisible =
            CharacterListFragment.characterFilterParams.starIds.contains(107801)
        eggKL?.setOnPreferenceClickListener {
            ToastUtil.short("不要碰我了~烦死啦")
            return@setOnPreferenceClickListener true
        }

        //分享应用
        shareApp?.setOnPreferenceClickListener {
            ShareIntentUtil.text("PCR Tool 下载地址: ${getString(R.string.app_download_url)}")
            return@setOnPreferenceClickListener true
        }
    }


}
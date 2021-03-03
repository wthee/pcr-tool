package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.preference.*
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.FileUtil.convertFileSize
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
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

    private lateinit var switchPvpRegion: SwitchPreference
    private lateinit var changeDbType: ListPreference

    override fun onResume() {
        super.onResume()
        //添加返回fab
        FabHelper.addBackFab()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //获取控件
        titleDatabase = findPreference("title_database")!!
        val titleApp = findPreference<Preference>("title_app")
        val forceUpdateDb = findPreference<Preference>("force_update_db")
        val cleanDatabase = findPreference<Preference>("clean_database")
        val appUpdate = findPreference<Preference>("force_update_app")
        val shareApp = findPreference<Preference>("share_app")
        changeDbType = findPreference("change_database")!!
        switchPvpRegion = findPreference("pvp_region")!!
        setDbSummary(changeDbType.value)
        //切换竞技场查询
        switchPvpRegion.isVisible = changeDbType.value != "1"
        //历史数据大小
        FileUtil.getOldDatabaseSize().let {
            if (it > 0) {
                cleanDatabase?.isVisible = true
                cleanDatabase?.summary = FileUtil.getOldDatabaseSize().convertFileSize()
            }
        }
        //数据版本
        lifecycleScope.launch {
            DataStoreUtil.get(
                if (changeDbType.value == "1") Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP
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
                            dialog.dismiss()
                            DatabaseUpdater.checkDBVersion(0, force = true)
                        }
                    }
                }).show()
            return@setOnPreferenceClickListener true
        }
        //切换数据库版本
        changeDbType.setOnPreferenceChangeListener { _, newValue ->
            if (changeDbType.value != newValue as String) {
                setDbSummary(newValue)
                lifecycleScope.launch {
                    delay(800L)
                    DatabaseUpdater.checkDBVersion(1)
                }
            }
            return@setOnPreferenceChangeListener true
        }
        //历史数据库文件
        cleanDatabase?.setOnPreferenceClickListener {
            FileUtil.deleteOldDatabase()
            it.summary = FileUtil.getOldDatabaseSize().convertFileSize()
            it.isVisible = false
            return@setOnPreferenceClickListener true
        }

        //应用更新
        titleApp?.title = getString(R.string.app_version) + BuildConfig.VERSION_NAME
        appUpdate?.setOnPreferenceClickListener {
            //应用版本校验
            lifecycleScope.launch {
                requireActivity().findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_global_noticeListFragment)
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

    /**
     * 设置切换版本摘要
     */
    private fun setDbSummary(newValue: String) {
        changeDbType.summary = getString(R.string.now_db_type) + if (newValue == "1") {
            switchPvpRegion.isVisible = false
            getString(R.string.db_cn)
        } else {
            switchPvpRegion.isVisible = true
            getString(R.string.db_jp)
        } + if (MyApplication.backupMode) {
            getString(R.string.backup_mode)
        } else {
            ""
        }
    }
}
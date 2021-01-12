package cn.wthee.pcrtool.ui.setting

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.*
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.ui.home.CharacterListFragment.Companion.sortAsc
import cn.wthee.pcrtool.ui.home.CharacterListFragment.Companion.sortType
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 设置
 */
class MainSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        lateinit var titleDatabase: Preference
    }

    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onResume() {
        super.onResume()
        //添加返回fab
        FabHelper.addBackFab()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val sp = requireActivity().getSharedPreferences("main", Context.MODE_PRIVATE)
        //获取控件
        titleDatabase = findPreference("title_database")!!
        val forceUpdateDb = findPreference<Preference>("force_update_db")
        val appUpdate = findPreference<Preference>("force_update_app")
        val shareApp = findPreference<Preference>("share_app")
        val changeDbType = findPreference<ListPreference>("change_database")
        val switchPvpRegion = findPreference<SwitchPreference>("pvp_region")
        changeDbType?.title =
            "游戏版本 - " + if (changeDbType?.value == "1") getString(R.string.db_cn) else getString(R.string.db_jp)
        switchPvpRegion?.isVisible = changeDbType?.value != "1"
        //数据版本
        titleDatabase.title = getString(R.string.data) + sp.getString(
            if (changeDbType?.value == "1") Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
            "0"
        )
        appUpdate?.summary = MainActivity.nowVersionName
        appUpdate?.setOnPreferenceClickListener {
            //应用版本校验
            ToastUtil.short("应用版本检测中...")
            AppUpdateHelper.init(requireContext(), layoutInflater, true)
            return@setOnPreferenceClickListener true
        }
        //设置监听
        //强制更新数据库
        forceUpdateDb?.setOnPreferenceClickListener {
            DatabaseUpdater.checkDBVersion(0, force = true)
            return@setOnPreferenceClickListener true
        }
        //切换数据库版本
        changeDbType?.setOnPreferenceChangeListener { _, newValue ->
            if (changeDbType.value != newValue as String) {
                if (newValue == "1") {
                    changeDbType.title = "游戏版本 - " + getString(R.string.db_cn)
                    switchPvpRegion?.isVisible = false
                } else {
                    changeDbType.title = "游戏版本 - " + getString(R.string.db_jp)
                    switchPvpRegion?.isVisible = true
                }
                MainScope().launch {
                    delay(800L)
                    DatabaseUpdater.checkDBVersion(1)
                }
            }
            return@setOnPreferenceChangeListener true
        }
        //egg
        val eggs = findPreference<PreferenceCategory>("egg")
        val eggKL = findPreference<Preference>("kl")
        //是否显示
        eggs?.isVisible = CharacterListFragment.characterFilterParams.starIds.contains(106001)
                || CharacterListFragment.characterFilterParams.starIds.contains(107801)
                || CharacterListFragment.characterFilterParams.starIds.contains(112001)
        var count = sp.getInt("click_kl", 0)
        eggKL?.setOnPreferenceClickListener {
            count++
            if (count > 3) {
                //隐藏
                eggs?.isVisible = false
                CharacterListFragment.characterFilterParams
                    .remove(107801)
                CharacterListFragment.characterFilterParams.initData()
                sharedCharacterViewModel.getCharacters(
                    sortType,
                    sortAsc,
                    ""
                )
            } else {
                val text = when (count) {
                    1 -> "不要碰我了~烦死啦"
                    2 -> "我都说过了！烦死啦~烦死啦~"
                    3 -> "凯露我啊~真的生气了！！！"
                    else -> ""
                }
                ToastUtil.short(text)
            }

            return@setOnPreferenceClickListener true
        }

        //分享应用
        shareApp?.setOnPreferenceClickListener {
            ShareIntentUtil.text("PCR Tool 下载地址: ${getString(R.string.app_download_url)}")
            return@setOnPreferenceClickListener true
        }
    }


}
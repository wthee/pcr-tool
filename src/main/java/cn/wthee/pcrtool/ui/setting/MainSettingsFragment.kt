package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.preference.*
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.ui.main.EquipmentViewModel
import cn.wthee.pcrtool.utils.*
import coil.Coil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        lateinit var forceUpdateDb: Preference
    }

    private val viewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //添加返回fab
        FabHelper.addBackFab()
        //获取控件
        val isList = findPreference<SwitchPreferenceCompat>("equip_is_list")
        forceUpdateDb = findPreference<Preference>("force_update_db")!!
        val appUpdate = findPreference<Preference>("force_update_app")
        val cleanData = findPreference<Preference>("clean_data")
        val changeDbType = findPreference<ListPreference>("change_database")
        changeDbType?.title =
            "游戏版本 - " + if (changeDbType?.value == "1") getString(R.string.db_cn) else getString(R.string.db_jp)
        //摘要替换
        forceUpdateDb.summary = MainActivity.sp.getString(
            if (changeDbType?.value == "1") Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
            "0"
        )
        appUpdate?.summary = MainActivity.nowVersionName
        cleanData?.title =
            cleanData?.title.toString() + "  " + CacheUtil.getTotalCacheSize(requireContext())
        //设置监听
        //装备列表界面
        isList?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            viewModel.isList.postValue(value)
            return@setOnPreferenceChangeListener true
        }
        //强制更新数据库
        forceUpdateDb.setOnPreferenceClickListener {
            DatabaseUpdateHelper.checkDBVersion(0)
            return@setOnPreferenceClickListener true
        }
        //切换数据库版本
        changeDbType?.setOnPreferenceChangeListener { _, newValue ->
            changeDbType.title =
                "游戏版本 - " + if (newValue as String == "1") getString(R.string.db_cn) else getString(
                    R.string.db_jp
                )
            MainScope().launch {
                delay(800L)
                DatabaseUpdateHelper.checkDBVersion(1)
            }
            return@setOnPreferenceChangeListener true
        }
        //清除图片缓存
        cleanData?.setOnPreferenceClickListener {
            CacheUtil.clearAllCache(requireContext())
            cleanData.title =
                cleanData.title.toString().split(" ")[0] + "  " + CacheUtil.getTotalCacheSize(
                    requireContext()
                )
            Coil.imageLoader(MyApplication.context).memoryCache.clear()
            ToastUtil.short("图片缓存已清理")
            return@setOnPreferenceClickListener true
        }
        //egg
        val eggs = findPreference<PreferenceCategory>("egg")
        val eggKL = findPreference<Preference>("kl")
        eggs?.isVisible = MainActivity.sp.getBoolean("106001", false)
                || MainActivity.sp.getBoolean("107801", false)
        var count = MainActivity.sp.getInt("click_kl", 0)
        eggKL?.setOnPreferenceClickListener {
            count++
            val text = when (count) {
                1 -> "不要碰我了~烦死啦"
                2 -> "我都说过了！烦死啦~烦死啦~"
                3 -> "凯露我啊~真的生气了！！！"
                else -> ""
            }
            ToastUtil.short(text)
            if (count == 3) {
                eggs?.isVisible = false
                MainActivity.sp.edit {
                    putBoolean("106001", false)
                    putBoolean("107801", false)
                }
                CharacterListFragment.characterfilterParams.initData()
                sharedCharacterViewModel.getCharacters(
                    MainActivity.sortType,
                    MainActivity.sortAsc,
                    ""
                )
            }

            return@setOnPreferenceClickListener true
        }
    }


}
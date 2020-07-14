package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
import cn.wthee.pcrtool.ui.main.EquipmentListFragment
import cn.wthee.pcrtool.ui.main.MainPagerFragment
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import com.tencent.bugly.beta.Beta


class MainSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //添加返回fab
        FabHelper.addBackFab(this)
        //获取控件
        val isList = findPreference<SwitchPreferenceCompat>("equip_is_list")
        val fabStatus = findPreference<SwitchPreferenceCompat>("fab_status")
        val forceUpdateDb = findPreference<Preference>("force_update_db")
        val appUpdate = findPreference<Preference>("force_update_app")
        val cleanData = findPreference<Preference>("clean_data")
        //摘要替换
        forceUpdateDb?.summary = MainActivity.databaseVersion
        appUpdate?.summary = MainActivity.nowVersionName
        //设置监听
        isList?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            EquipmentListFragment.viewModel.isList.postValue(value)
            return@setOnPreferenceChangeListener true
        }
        fabStatus?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            if (value) {
                MainActivity.fab.extend()
                MainPagerFragment.fabSetting.extend()
                MainPagerFragment.fabLove.extend()
                MainPagerFragment.fabSearch.extend()
                MainPagerFragment.fabFilter.extend()
                MainPagerFragment.fabSort.extend()
            } else {
                MainActivity.fab.shrink()
                MainPagerFragment.fabSetting.shrink()
                MainPagerFragment.fabLove.shrink()
                MainPagerFragment.fabSearch.shrink()
                MainPagerFragment.fabFilter.shrink()
                MainPagerFragment.fabSort.shrink()
            }
            return@setOnPreferenceChangeListener true
        }
        forceUpdateDb?.setOnPreferenceClickListener {
            DatabaseUpdateHelper().checkDBVersion()
            return@setOnPreferenceClickListener true
        }
        appUpdate?.setOnPreferenceClickListener {
            Beta.checkUpgrade(true, false)
            return@setOnPreferenceClickListener true
        }
        cleanData?.setOnPreferenceClickListener {
            ToastUtil.short("TODO CLEAN")
            return@setOnPreferenceClickListener true
        }
    }


    override fun onResume() {
        super.onResume()
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener(View.OnKeyListener { view, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                FabHelper.goBack(this)
                return@OnKeyListener true
            }
            false
        })

    }


}
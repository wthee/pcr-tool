package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
import cn.wthee.pcrtool.ui.main.EquipmentViewModel
import cn.wthee.pcrtool.utils.*
import coil.Coil
import com.tencent.bugly.beta.Beta


class MainSettingsFragment : PreferenceFragmentCompat() {

    private val viewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //添加返回fab
        FabHelper.addBackFab()
        //获取控件
        val isList = findPreference<SwitchPreferenceCompat>("equip_is_list")
        val forceUpdateDb = findPreference<Preference>("force_update_db")
        val autoUpdateDb = findPreference<Preference>("auto_update_db")
        val appUpdate = findPreference<Preference>("force_update_app")
        val cleanData = findPreference<Preference>("clean_data")
        val notToast = findPreference<Preference>("not_toast")
        notToast?.isEnabled = MainActivity.spSetting.getBoolean("auto_update_db", true)
        //摘要替换
        forceUpdateDb?.summary = MainActivity.sp.getString(Constants.SP_DATABASE_VERSION, "0")
        appUpdate?.summary = MainActivity.nowVersionName
        cleanData?.title =
            cleanData?.title.toString() + "  " + CacheUtil.getTotalCacheSize(requireContext())
        //设置监听
        isList?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            viewModel.isList.postValue(value)
            return@setOnPreferenceChangeListener true
        }
        autoUpdateDb?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            notToast?.isEnabled = value
            return@setOnPreferenceChangeListener true
        }
        notToast?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            MainActivity.notToast = value
            return@setOnPreferenceChangeListener true
        }
        forceUpdateDb?.setOnPreferenceClickListener {
            DatabaseUpdateHelper().checkDBVersion(false)
            return@setOnPreferenceClickListener true
        }
        appUpdate?.setOnPreferenceClickListener {
            Beta.checkUpgrade(true, false)
            return@setOnPreferenceClickListener true
        }
        cleanData?.setOnPreferenceClickListener {
            CacheUtil.clearAllCache(requireContext())
            cleanData.title =
                cleanData.title.toString().split(" ")[0] + "  " + CacheUtil.getTotalCacheSize(
                    requireContext()
                )
            Coil.imageLoader(MyApplication.getContext()).memoryCache.clear()
            ToastUtil.short("图片缓存已清理")
            return@setOnPreferenceClickListener true
        }
    }


}
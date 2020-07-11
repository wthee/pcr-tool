package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
import cn.wthee.pcrtool.utils.ToastUtil
import com.tencent.bugly.beta.Beta
import javax.inject.Singleton

@Singleton
class MainSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //添加返回按钮
        MainActivity.fab.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_back,
                null
            )
        )
        MainActivity.fab.apply {
            setOnClickListener {
                goBack()
            }
        }

        //获取控件
        val forcepdateUDb = findPreference<Preference>("force_update_db")
        val appUpdate = findPreference<Preference>("force_update_app")
        val cleanData = findPreference<Preference>("clean_data")
        //摘要替换
        forcepdateUDb?.summary = MainActivity.databaseVersion
        appUpdate?.summary = MainActivity.nowVersionName
        //设置监听
        forcepdateUDb?.setOnPreferenceClickListener {
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
                goBack()
                return@OnKeyListener true
            }
            false
        })

    }

    private fun goBack() {
        MainActivity.fab.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_function,
                null
            )
        )
        findNavController().navigateUp()
    }
}
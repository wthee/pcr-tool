package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import cn.wthee.pcrtool.R
import javax.inject.Singleton

@Singleton
class MainSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

}
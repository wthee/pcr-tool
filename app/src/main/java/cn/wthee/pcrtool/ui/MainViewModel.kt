package cn.wthee.pcrtool.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.preferences.SettingPreferencesKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 初始化
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    init {
        loadSetting()
    }

    /**
     * 初始化加载设置、六星 id 列表
     */
    fun loadSetting() {
        viewModelScope.launch {
            //六星 id 初始化
            val r6Ids = unitRepository.getR6Ids()
            if (r6Ids != null) {
                MainActivity.r6Ids = r6Ids
            }

            //用户设置信息
            val preferences = MyApplication.context.dataStoreSetting.data.first()
            MainActivity.vibrateOnFlag =
                preferences[SettingPreferencesKeys.SP_VIBRATE_STATE] ?: true
            MainActivity.animOnFlag = preferences[SettingPreferencesKeys.SP_ANIM_STATE] ?: true
            MainActivity.dynamicColorOnFlag =
                preferences[SettingPreferencesKeys.SP_COLOR_STATE] ?: true
            MainActivity.regionType = RegionType.getByValue(
                preferences[SettingPreferencesKeys.SP_DATABASE_TYPE] ?: RegionType.CN.value
            )
        }
    }
}
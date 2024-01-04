package cn.wthee.pcrtool.ui.tool.extraequip.detail

import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentData
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.utils.JsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * 页面状态：ex装备详情
 */
@Immutable
data class ExtraEquipDetailUiState(
    //装备属性
    val equipData: ExtraEquipmentData? = null,
    //装备类型
    val category: Int = 0,
    //收藏信息
    val starIdList: List<Int> = emptyList(),
    //适用角色
    val unitIdList: List<Int> = emptyList(),
    //收藏角色
    val loved: Boolean = false,
    val loadingState: LoadingState = LoadingState.Loading,
    //技能
    val skillList: List<SkillDetail>? = null
)

/**
 * ex装备详情 ViewModel
 */
@HiltViewModel
class ExtraEquipDetailViewModel @Inject constructor(
    private val extraEquipmentRepository: ExtraEquipmentRepository,
    private val skillRepository: SkillRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val equipId: Int? = savedStateHandle[NavRoute.EQUIP_ID]

    private val _uiState = MutableStateFlow(ExtraEquipDetailUiState())
    val uiState: StateFlow<ExtraEquipDetailUiState> = _uiState.asStateFlow()

    init {
        if (equipId != null) {
            getEquip(equipId)
            getLoveState(equipId)
        }
    }


    /**
     * 获取装备信息
     *
     * @param equipId 装备编号
     */
    private fun getEquip(equipId: Int) {
        viewModelScope.launch {
            val data = extraEquipmentRepository.getEquipmentData(equipId)
            _uiState.update {
                it.copy(
                    equipData = data,
                    category = data?.category ?: 0,
                    loadingState = it.loadingState.isSuccess(data != null)
                )
            }
            if (data != null) {
                getExtraEquipUnitList(data.category)
                getExtraEquipPassiveSkills(data.getPassiveSkillIds())
            }
        }
    }

    /**
     * 获取可使用装备的角色列表
     */
    private fun getExtraEquipUnitList(category: Int) {
        viewModelScope.launch {
            val list = extraEquipmentRepository.getEquipUnitList(category)
            _uiState.update {
                it.copy(
                    unitIdList = list
                )
            }
        }
    }

    /**
     * 获取收藏列表
     */
    private fun getLoveState(equipId: Int) {
        viewModelScope.launch {
            val list = FilterExtraEquipment.getStarIdList()
            _uiState.update {
                it.copy(
                    loved = list.contains(equipId),
                    starIdList = list
                )
            }
        }
    }

    /**
     * 获取ex装备被动技能信息
     *
     * @param skillIds 技能编号列表
     */
    private fun getExtraEquipPassiveSkills(skillIds: List<Int>) {
        viewModelScope.launch {
            val skillList = skillRepository.getSkills(
                skillIds,
                arrayListOf(0),
                0,
                0
            )
            _uiState.update {
                it.copy(
                    skillList = skillList
                )
            }
        }
    }

    /**
     * 获取收藏列表
     */
    fun reloadStarList() {
        viewModelScope.launch {
            val list = FilterExtraEquipment.getStarIdList()
            _uiState.update {
                it.copy(starIdList = list)
            }
        }
    }

    /**
     * 更新收藏id
     */
    fun updateStarId() {
        viewModelScope.launch {
            if (equipId != null) {
                MyApplication.context.dataStoreMain.edit { preferences ->
                    val list =
                        JsonUtil.toIntList(preferences[MainPreferencesKeys.SP_STAR_EXTRA_EQUIP])
                    if (list.contains(equipId)) {
                        list.remove(equipId)
                        _uiState.update {
                            it.copy(
                                loved = false
                            )
                        }
                    } else {
                        list.add(equipId)
                        _uiState.update {
                            it.copy(
                                loved = true
                            )
                        }
                    }
                    preferences[MainPreferencesKeys.SP_STAR_EXTRA_EQUIP] = Json.encodeToString(list)
                }
            }

        }
    }
}

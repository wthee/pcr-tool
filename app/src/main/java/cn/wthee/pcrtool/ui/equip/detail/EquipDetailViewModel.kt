package cn.wthee.pcrtool.ui.equip.detail

import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.QuestRepository
import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.updateLoadingState
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
 * 页面状态：装备详情
 */
@Immutable
data class EquipDetailUiState(
    //装备属性
    val equipData: EquipmentMaxData? = null,
    //素材列表
    val materialList: List<EquipmentMaterial> = emptyList(),
    //收藏信息
    val starIdList: List<Int> = emptyList(),
    //适用角色
    val unitIdList: List<Int> = emptyList(),
    //收藏角色
    val loved: Boolean = false,
    val loadingState: LoadingState = LoadingState.Loading,
    val materialLoadingState: LoadingState = LoadingState.Loading,
)

/**
 * 装备详情 ViewModel
 *
 * @param equipmentRepository
 * @param questRepository
 */
@HiltViewModel
class EquipDetailViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
    private val questRepository: QuestRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val equipId: Int? = savedStateHandle[NavRoute.EQUIP_ID]

    private val _uiState = MutableStateFlow(EquipDetailUiState())
    val uiState: StateFlow<EquipDetailUiState> = _uiState.asStateFlow()

    init {
        if (equipId != null) {
            getEquip(equipId)
            getEquipUnitList(equipId)
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
            val data = equipmentRepository.getEquipmentData(equipId)
            _uiState.update {
                it.copy(
                    equipData = data,
                    loadingState = it.loadingState.isSuccess(data != null),
                    materialLoadingState = it.materialLoadingState.isNoData(data == null)
                )
            }
            if (data != null) {
                getEquipCraft(data)
            }
        }
    }

    /**
     * 获取装备制作材料信息
     *
     * @param equip 装备信息
     */
    private suspend fun getEquipCraft(equip: EquipmentMaxData) {
        val list = equipmentRepository.getEquipCraft(
            EquipmentBasicInfo(
                equip.equipmentId,
                equip.equipmentName,
                equip.craftFlg
            )
        )
        _uiState.update {
            it.copy(
                materialList = list,
                materialLoadingState = updateLoadingState(list)
            )
        }
    }

    /**
     * 获取装备适用角色
     */
    private fun getEquipUnitList(equipId: Int) {
        viewModelScope.launch {
            val unitIds = equipmentRepository.getEquipUnitList(equipId)
            _uiState.update {
                it.copy(
                    unitIdList = unitIds
                )
            }
        }
    }

    /**
     * 获取收藏列表
     */
    private fun getLoveState(equipId: Int) {
        viewModelScope.launch {
            val list = FilterEquip.getStarIdList()
            _uiState.update {
                it.copy(
                    loved = list.contains(equipId),
                    starIdList = list
                )
            }
        }
    }

    /**
     * 获取收藏列表
     */
    fun reloadStarList() {
        viewModelScope.launch {
            val list = FilterEquip.getStarIdList()
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
                        JsonUtil.toIntList(preferences[MainPreferencesKeys.SP_STAR_EQUIP])
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
                    preferences[MainPreferencesKeys.SP_STAR_EQUIP] = Json.encodeToString(list)
                }
            }

        }
    }
}

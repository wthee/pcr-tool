package cn.wthee.pcrtool.ui.equip

import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.QuestRepository
import cn.wthee.pcrtool.data.db.view.QuestDetail
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.getStarEquipIdList
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.LogReportUtil
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：装备素材详情
 */
@Immutable
data class EquipMaterialDetailUiState(
    //装备id
    val equipId: Int? = null,
    //装备名称
    val equipName: String = "",
    //装备掉落
    val dropQuestList: List<QuestDetail>? = null,
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
 * 装备素材详情 ViewModel
 */
@HiltViewModel
class EquipMaterialDropInfoViewModel @Inject constructor(
    private val questRepository: QuestRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val equipId: Int? = savedStateHandle[NavRoute.EQUIP_ID]
    private val equipName: String = savedStateHandle[NavRoute.EQUIP_NAME] ?: ""

    private val _uiState = MutableStateFlow(EquipMaterialDetailUiState())
    val uiState: StateFlow<EquipMaterialDetailUiState> = _uiState.asStateFlow()

    init {
        if (equipId != null) {
            _uiState.update {
                it.copy(
                    equipName = equipName,
                    equipId = equipId
                )
            }
            getDropInfo(equipId)
            getLoveState(equipId)
        }
    }


    /**
     * 获取装备掉落关卡信息
     *
     * @param equipId 装备编号
     */
    private fun getDropInfo(equipId: Int) {
        viewModelScope.launch {
            val list = try {
                //获取装备掉落信息
                questRepository.getEquipDropQuestList(equipId).sortedWith(getSort(equipId))
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getDropInfo#equipId:$equipId")
                null
            }
            _uiState.update {
                it.copy(
                    dropQuestList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

    /**
     * 获取收藏列表
     */
    private fun getLoveState(equipId: Int) {
        viewModelScope.launch {
            val list = getStarEquipIdList()
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
            val list = getStarEquipIdList()
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
                        GsonUtil.toIntList(preferences[MainPreferencesKeys.SP_STAR_EQUIP])
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
                    preferences[MainPreferencesKeys.SP_STAR_EQUIP] = Gson().toJson(list)
                }
            }

        }
    }

    /**
     * 根据掉率排序
     *
     * @param equipId 装备编号
     */
    private fun getSort(equipId: Int): java.util.Comparator<QuestDetail> {
        val str = equipId.toString()
        return Comparator { o1: QuestDetail, o2: QuestDetail ->
            val a = o1.getOddOfEquip(str)
            val b = o2.getOddOfEquip(str)
            b.compareTo(a)
        }
    }
}

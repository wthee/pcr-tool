package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.int
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max


/**
 * 页面状态：角色详情
 */
@Immutable
data class CharacterDetailUiState(
    val basicInfo: CharacterInfo? = null,
    val cutinId: Int = 0,
    val currentId: Int = 0,
    val isCutinSkill: Boolean = true,
    val maxValue: CharacterProperty = CharacterProperty(),
    val currentValue: CharacterProperty = CharacterProperty(),
    val allAttr: AllAttrData = AllAttrData(),
    val maxAtk:Int = 0
)


/**
 * 角色 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val unitId: Int? = savedStateHandle[NavRoute.UNIT_ID]

    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    init {
        if (unitId != null) {
            _uiState.update {
                it.copy(currentId = unitId)
            }
            getCutinId(unitId)
            getCharacterBasicInfo(unitId)
            getMaxRankAndRarity(unitId)
        }
    }

    /**
     * 获取角色基本信息
     *
     * @param unitId 角色编号
     */
    private fun getCharacterBasicInfo(unitId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(basicInfo = unitRepository.getCharacterBasicInfo(unitId))
            }
        }
    }

    /**
     * 获取特殊六星 id
     *
     * @param unitId 角色编号
     */
    private fun getCutinId(unitId: Int) {
        viewModelScope.launch {
            val cutinId = unitRepository.getCutinId(unitId)
            _uiState.update {
                it.copy(
                    cutinId = if (_uiState.value.isCutinSkill && cutinId != 0) {
                        cutinId
                    } else {
                        0
                    }
                )
            }
        }
    }

    /**
     * 获取最大Rank和星级等
     *
     * @param unitId 角色编号
     */
    private fun getMaxRankAndRarity(unitId: Int) {
        viewModelScope.launch {
            try {
                val rank = unitRepository.getMaxRank(unitId)
                val rarity = unitRepository.getMaxRarity(unitId)
                val level = unitRepository.getMaxLevel()
                val uniqueEquipLevel = equipmentRepository.getUniqueEquipMaxLv(1) ?: 0
                val uniqueEquipLevel2 = equipmentRepository.getUniqueEquipMaxLv(2) ?: 0
                val maxValue =
                    CharacterProperty(level, rank, rarity, uniqueEquipLevel, uniqueEquipLevel2)
                //数值信息
                if (!_uiState.value.currentValue.isInit()) {
                    //初始为最大值
                    _uiState.update {
                        it.copy(
                            currentValue = maxValue
                        )
                    }
                    //初始加载
                    getCharacterInfo(unitId, maxValue)
                }
                _uiState.update {
                    it.copy(
                        maxValue = maxValue
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getMaxRankAndRarity:$unitId")
                _uiState.update {
                    it.copy(
                        maxValue = CharacterProperty(level = -1)
                    )
                }
            }
        }
    }


    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     * @param unitId 角色编号
     * @param property 角色属性
     */
    private fun getCharacterInfo(unitId: Int, property: CharacterProperty?) {
        viewModelScope.launch {
            try {
                if (property != null && property.isInit()) {
                    val allAttr = unitRepository.getAttrs(
                        unitId,
                        property.level,
                        property.rank,
                        property.rarity,
                        property.uniqueEquipmentLevel,
                        property.uniqueEquipmentLevel2
                    )
                    _uiState.update {
                        it.copy(
                            allAttr = allAttr,
                            maxAtk = max(
                                allAttr.sumAttr.atk.int,
                                allAttr.sumAttr.magicStr.int
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                LogReportUtil.upload(
                    e,
                    "getCharacterInfo#unitId:$unitId,property:${property ?: ""}"
                )
            }
        }
    }

    /**
     * 形态切换
     */
    fun changeCutin() {
        unitId?.let {
            _uiState.update { state ->
                state.copy(
                    currentId = if (state.isCutinSkill) unitId else state.cutinId,
                    isCutinSkill = !state.isCutinSkill,
                )
            }
        }
    }

    /**
     * 更新当前属性值
     */
    fun updateCurrentValue(value: CharacterProperty) {
        _uiState.update {
            it.copy(
                currentValue = value
            )
        }
        if (unitId != null) {
            getCharacterInfo(unitId, _uiState.value.currentValue)
        }
    }

}


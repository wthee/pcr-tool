package cn.wthee.pcrtool.ui.skill.summon

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.AttackPattern
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.SummonData
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：角色技能召唤物
 */
@Immutable
data class SkillSummonUiState(
    //基本信息
    val summonData: SummonData? = null,
    //数值信息
    val attrs: AllAttrData? = null,
    //技能信息
    val attackPatternList: List<AttackPattern> = emptyList()
)

/**
 * 角色技能召唤物 ViewModel
 *
 * @param skillRepository
 */
@HiltViewModel
class SkillSummonDetailViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillSummonUiState())
    val uiState: StateFlow<SkillSummonUiState> = _uiState.asStateFlow()

    /**
     * 加载技能信息
     */
    fun loadData(unitId: Int, property: CharacterProperty) {
        getCharacterInfo(unitId, property)
        getSummonData(unitId)
        getSkillLoops(unitId)
    }

    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     *
     * @param unitId 角色编号
     * @param property 角色属性
     */
    private fun getCharacterInfo(unitId: Int, property: CharacterProperty) {
        viewModelScope.launch {
            if (property.isInit()) {
                val attrs = unitRepository.getCharacterInfo(unitId, property)
                _uiState.update {
                    it.copy(
                        attrs = attrs
                    )
                }
            }
        }
    }

    /**
     * 获取召唤物基本信息
     */
    private fun getSummonData(unitId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    summonData = unitRepository.getSummonData(unitId)
                )
            }
        }
    }

    /**
     * 获取角色技能循环
     *
     * @param unitId 角色编号
     */
    private fun getSkillLoops(unitId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    attackPatternList = skillRepository.getAttackPattern(unitId)
                )
            }
        }
    }
}

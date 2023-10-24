package cn.wthee.pcrtool.ui.skill

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.view.SpSkillLabelData
import cn.wthee.pcrtool.data.enums.SkillType
import cn.wthee.pcrtool.data.model.SkillDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：角色技能
 */
@Immutable
data class SkillUiState(
    val normalSkill: MutableList<SkillDetail> = arrayListOf(),
    val spSkill: MutableList<SkillDetail> = arrayListOf(),
    val spLabel: SpSkillLabelData? = null
)

/**
 * 角色技能 ViewModel
 *
 * @param skillRepository
 */
@HiltViewModel
class SkillListViewModel @Inject constructor(
    private val skillRepository: SkillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillUiState())
    val uiState: StateFlow<SkillUiState> = _uiState.asStateFlow()

    /**
     * 加载技能信息
     */
    fun loadSkillInfo(lv: Int, atk: Int, unitId: Int) {
        //普通技能
        loadCharacterSkills(
            lv,
            atk,
            unitId,
            SkillType.NORMAL
        )
        //特殊技能
        loadCharacterSkills(
            lv,
            atk,
            unitId,
            SkillType.SP
        )
        //标签
        loadSpSkillLabel(unitId)
    }

    /**
     * 获取角色技能信息
     *
     * @param lv 技能能级
     * @param atk 基础攻击力
     * @param unitId 角色编号
     */
    private fun loadCharacterSkills(lv: Int, atk: Int, unitId: Int, skillType: SkillType) {
        viewModelScope.launch {
            val skillList = skillRepository.getSkills(
                skillRepository.getSkillIds(unitId, skillType),
                arrayListOf(lv),
                atk,
                unitId
            )
            _uiState.update {
                if (skillType == SkillType.NORMAL) {
                    it.copy(
                        normalSkill = skillList
                    )
                } else {
                    it.copy(
                        spSkill = skillList
                    )
                }
            }
        }
    }

    /**
     * 获取角色特殊技能标签
     *
     * @param unitId 角色编号
     */
    private fun loadSpSkillLabel(unitId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    spLabel = skillRepository.getSpSkillLabel(unitId)
                )
            }
        }
    }

}

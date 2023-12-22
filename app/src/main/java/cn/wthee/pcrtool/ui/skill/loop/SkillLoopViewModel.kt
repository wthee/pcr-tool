package cn.wthee.pcrtool.ui.skill.loop

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EnemyRepository
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.SkillBasicData
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：角色技能循环
 */
@Immutable
data class SkillLoopUiState(
    //获取循环对应的图标
    val skillMap: HashMap<Int, SkillBasicData> = hashMapOf(),
    //普攻动作时间
    val atkCastTime: Double = 0.0
)

/**
 * 角色技能循环 ViewModel
 */
@HiltViewModel
class SkillLoopViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val unitRepository: UnitRepository,
    private val enemyRepository: EnemyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillLoopUiState())
    val uiState: StateFlow<SkillLoopUiState> = _uiState.asStateFlow()

    /**
     * 加载技能信息
     */
    fun loadData(loopIdList: List<Int>, unitId: Int, unitType: UnitType) {
        getSkillIconTypes(loopIdList, unitId)
        getAtkCastTime(unitId, unitType)
    }

    /**
     * 获取技能图标信息
     */
    private fun getSkillIconTypes(loopIdList: List<Int>, unitId: Int) {
        viewModelScope.launch {
            try {
                val map = hashMapOf<Int, SkillBasicData>()
                val unitSkill = skillRepository.getUnitSkill(unitId)
                val mLoopIdList = loopIdList.distinct()

                unitSkill?.let {
                    //技能id
                    val mainSkillIdList = arrayListOf(
                        unitSkill.main_skill_1,
                        unitSkill.main_skill_2,
                        unitSkill.main_skill_3,
                        unitSkill.main_skill_4,
                        unitSkill.main_skill_5,
                        unitSkill.main_skill_6,
                        unitSkill.main_skill_7,
                        unitSkill.main_skill_8,
                        unitSkill.main_skill_9,
                        unitSkill.main_skill_10,
                    )
                    //sp技能id
                    val spSkillIdList = arrayListOf(
                        unitSkill.sp_skill_1,
                        unitSkill.sp_skill_2,
                        unitSkill.sp_skill_3,
                        unitSkill.sp_skill_4,
                        unitSkill.sp_skill_5,
                    )

                    //处理循环技能id对应的技能图标
                    mLoopIdList.forEach { loopId ->
                        val skillId = when {
                            loopId / 1000 == 1 -> mainSkillIdList[loopId % 100 - 1]
                            loopId / 1000 == 2 -> spSkillIdList[loopId % 100 - 1]
                            else -> null
                        }
                        skillId?.let {
                            map[loopId] = skillRepository.getSkillIconType(skillId)
                        }
                    }
                }

                _uiState.update {
                    it.copy(
                        skillMap = map
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(
                    e,
                    Constants.EXCEPTION_SKILL + "getSkillIconTypes#loopIdList:$loopIdList,unitId:$unitId"
                )
            }
        }
    }

    /**
     * 获取普通攻击时间
     */
    private fun getAtkCastTime(id: Int, unitType: UnitType) {
        viewModelScope.launch {
            try {
                val atkCastTime =
                    if (unitType == UnitType.CHARACTER || unitType == UnitType.CHARACTER_SUMMON) {
                        unitRepository.getAtkCastTime(id)

                    } else {
                        enemyRepository.getAtkCastTime(id)
                    }
                _uiState.update {
                    it.copy(
                        atkCastTime = atkCastTime ?: 0.0
                    )
                }
            } catch (_: Exception) {

            }
        }
    }
}

package cn.wthee.pcrtool.ui.tool.unknownskill

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EnemyRepository
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.enums.SkillType
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.ui.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class UnknownSkillListUiState(
    //角色技能信息
    val unitSkillList: List<SkillDetail> = arrayListOf(),
    //敌人技能信息
    val enemySkillList: List<SkillDetail> = arrayListOf(),
    val unitSKillLoadState: LoadState = LoadState.Loading
)

@HiltViewModel
class UnknownSkillListViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val unitRepository: UnitRepository,
    private val enemyRepository: EnemyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnknownSkillListUiState())
    val uiState: StateFlow<UnknownSkillListUiState> = _uiState.asStateFlow()

    init {
        getUnknownUnitSkillList()
        getUnknownEnemySkillList()
    }

    /**
     * 获取全部角色技能
     */
    private fun getUnknownUnitSkillList() {
        viewModelScope.launch {
            val resultList = arrayListOf<SkillDetail>()

            unitRepository.getUnitIdList().forEach { unitId ->
                val skillList = skillRepository.getSkills(
                    skillRepository.getSkillIds(unitId, SkillType.ALL),
                    arrayListOf(400),
                    1000,
                    unitId
                )
                val filterList = skillList.filter {
                    it.getActionInfo().forEach { actionData ->
                        if (actionData.actionDesc.contains("?")) {
                            return@filter true
                        }
                    }
                    return@filter false
                }

                resultList.addAll(filterList)
            }

            _uiState.update {
                it.copy(
                    unitSkillList = resultList,
                    unitSKillLoadState = LoadState.Success
                )
            }
        }
    }

    /**
     * 获取全部敌人技能
     */
    private fun getUnknownEnemySkillList() {
        viewModelScope.launch {
            val resultList = arrayListOf<SkillDetail>()

            enemyRepository.getClanBossList().forEach { enemy ->
                val skillList = skillRepository.getAllEnemySkill(enemy)

                val filterList = skillList.filter {
                    it.getActionInfo().forEach { actionData ->
                        if (actionData.actionDesc.contains("?")) {
                            return@filter true
                        }
                    }
                    return@filter false
                }

                resultList.addAll(filterList)
            }

            _uiState.update {
                it.copy(
                    enemySkillList = resultList
                )
            }
        }
    }
}
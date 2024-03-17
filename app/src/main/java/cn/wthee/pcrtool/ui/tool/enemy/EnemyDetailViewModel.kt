package cn.wthee.pcrtool.ui.tool.enemy

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EnemyRepository
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.view.AttackPattern
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.db.view.EnemyTalentWeaknessData
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.utils.intArrayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：怪物信息
 */
@Immutable
data class EnemyDetailUiState(
    //怪物信息
    val enemyInfo: EnemyParameterPro? = null,
    //所有部位信息
    val partInfoList: List<EnemyParameterPro> = emptyList(),
    //技能
    val skillList: List<SkillDetail>? = null,
    //技能循环
    val attackPatternList: List<AttackPattern>? = null,
    //弱点属性
    val weaknessData: EnemyTalentWeaknessData? = null
)

/**
 * 怪物信息 ViewModel
 *
 * @param enemyRepository
 */
@HiltViewModel
class EnemyDetailViewModel @Inject constructor(
    private val enemyRepository: EnemyRepository,
    private val skillRepository: SkillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnemyDetailUiState())
    val uiState: StateFlow<EnemyDetailUiState> = _uiState.asStateFlow()

    fun loadData(enemyId: Int) {
        //重置数据
        _uiState.value = EnemyDetailUiState()
        //获取数据
        getEnemyAttr(enemyId)
        getMultiTargetEnemyInfo(enemyId)
    }

    /**
     * 获取敌人属性
     *
     * @param enemyId 敌人编号
     */
    private fun getEnemyAttr(enemyId: Int) {
        viewModelScope.launch {
            //基本属性信息
            val info = enemyRepository.getEnemyAttr(enemyId)
            _uiState.update {
                it.copy(
                    enemyInfo = info
                )
            }
            //弱点属性
            getEnemyWeaknessData(enemyId)
            //技能信息
            info?.let {
                getAllEnemySkill(it)
                getAllSkillLoops(it)
            }
        }
    }

    /**
     * 获取多目标部位属性
     *
     * @param enemyId 敌人编号
     */
    private fun getMultiTargetEnemyInfo(enemyId: Int) {
        viewModelScope.launch {
            val data = enemyRepository.getMultiTargetEnemyInfo(enemyId)
            data?.let {
                val list = enemyRepository.getEnemyAttrList(data.enemyPartIds.intArrayList)
                _uiState.update {
                    it.copy(
                        partInfoList = list
                    )
                }
            }
        }
    }

    /**
     * 获取怪物技能信息
     *
     * @param enemyParameterPro 怪物基本参数
     */
    private fun getAllEnemySkill(enemyParameterPro: EnemyParameterPro) {
        viewModelScope.launch {
            val infoList = skillRepository.getAllEnemySkill(enemyParameterPro)
            _uiState.update {
                it.copy(
                    skillList = infoList
                )
            }
        }
    }

    /**
     * 获取全部技能循环
     *
     * @param enemyParameterPro 怪物基本参数
     */
    private fun getAllSkillLoops(enemyParameterPro: EnemyParameterPro) {
        viewModelScope.launch {
            val list = skillRepository.getAttackPattern(enemyParameterPro.unitId)
            _uiState.update {
                it.copy(
                    attackPatternList = list
                )
            }
        }
    }

    /**
     * 获取弱点属性
     *
     * @param enemyId 怪物编号
     */
    private fun getEnemyWeaknessData(enemyId: Int) {
        viewModelScope.launch {
            val list = enemyRepository.getEnemyWeaknessData(enemyId)
            if (!list.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        weaknessData = list[0]
                    )
                }
            }
        }
    }
}

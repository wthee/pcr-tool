package cn.wthee.pcrtool.ui.tool.enemy

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EnemyRepository
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.utils.intArrayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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
    val partInfoList: List<EnemyParameterPro> = emptyList()
)

/**
 * 怪物信息 ViewModel
 *
 * @param enemyRepository
 */
@HiltViewModel
class EnemyDetailViewModel @Inject constructor(
    private val enemyRepository: EnemyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnemyDetailUiState())
    val uiState: StateFlow<EnemyDetailUiState> = _uiState.asStateFlow()

    fun loadData(enemyId: Int) {
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
            _uiState.update {
                it.copy(
                    enemyInfo = enemyRepository.getEnemyAttr(enemyId)
                )
            }
        }
    }

    /**
     * 获取 BOSS 属性，测试用
     */
    fun getAllBossIds() = flow {
        try {
            val list = arrayListOf<Int>()
            val boss = enemyRepository.getAllBossIds()
            boss.forEach {
                list.add(it.unitId)
            }
            emit(list)
        } catch (_: Exception) {

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
}

package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ClanBattleRepository
import cn.wthee.pcrtool.data.db.repository.EnemyRepository
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.ClanBattleTargetCountData
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.model.ClanBattleProperty
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.utils.JsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：公会战详情
 */
@Immutable
data class ClanBattleDetailUiState(
    //公会战列表
    val clanBattleList: List<ClanBattleInfo> = emptyList(),
    //当前选中boss下标
    val bossIndex: Int = 0,
    //当前阶段
    val phaseIndex: Int = 0,
    val minPhase: Int = 0,
    val maxPhase: Int = 0,
    //boss信息
    val bossDataList: List<EnemyParameterPro> = emptyList(),
    //所有部位信息
    val partEnemyMap: Map<Int, List<EnemyParameterPro>> = hashMapOf(),
    val openDialog: Boolean = false
)

/**
 * 公会战详情 ViewModel
 */
@HiltViewModel
class ClanBattleDetailViewModel @Inject constructor(
    private val clanBattleRepository: ClanBattleRepository,
    private val enemyRepository: EnemyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val clanBattleProperty: ClanBattleProperty? =
        JsonUtil.fromJson(savedStateHandle[NavRoute.CLAN_BATTLE_PROPERTY])
    private val clanBattleId: Int? = clanBattleProperty?.clanBattleId
    private val minPhase: Int? = clanBattleProperty?.minPhase
    private val maxPhase: Int? = clanBattleProperty?.maxPhase
    private val index: Int? = clanBattleProperty?.index

    private val _uiState = MutableStateFlow(ClanBattleDetailUiState())
    val uiState: StateFlow<ClanBattleDetailUiState> = _uiState.asStateFlow()

    init {
        if (minPhase != null && maxPhase != null && clanBattleId != null && index != null) {
            _uiState.update {
                it.copy(
                    phaseIndex = maxPhase - minPhase,
                    minPhase = minPhase,
                    maxPhase = maxPhase,
                    bossIndex = index
                )
            }
            getClanBattleInfo(clanBattleId, maxPhase)
        }
    }

    private fun reloadData(phaseIndex: Int) {
        if (clanBattleId != null && minPhase != null) {
            getClanBattleInfo(clanBattleId, phaseIndex + minPhase)
        }
    }


    /**
     * 弹窗状态更新
     */
    fun changeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDialog = openDialog
                )
            }
        }
    }

    /**
     * 获取全部公会战记录
     */
    private fun getClanBattleInfo(clanBattleId: Int, phase: Int) {
        viewModelScope.launch {
            val clanBattleList = clanBattleRepository.getClanBattleList(clanBattleId, phase)
            _uiState.update {
                it.copy(
                    clanBattleList = clanBattleList
                )
            }

            if (clanBattleList.isNotEmpty()) {
                val clanBattleValue = clanBattleList[0]
                getAllBossAttr(clanBattleValue.bossList.map { it.enemyId })
                getMultiEnemyAttr(clanBattleValue.bossList.map { it.targetCountData })
            }
        }
    }


    /**
     * 获取 BOSS 属性
     *
     * @param enemyIds boss编号列表
     */
    private fun getAllBossAttr(enemyIds: List<Int>) {
        viewModelScope.launch {
            val list = enemyRepository.getEnemyAttrList(enemyIds)
            _uiState.update {
                it.copy(
                    bossDataList = list
                )
            }
        }
    }

    /**
     * 获取多目标部位属性
     */
    private fun getMultiEnemyAttr(targetCountDataList: List<ClanBattleTargetCountData>) {
        viewModelScope.launch {
            val map = enemyRepository.getMultiEnemyAttr(targetCountDataList)
            _uiState.update {
                it.copy(
                    partEnemyMap = map
                )
            }
        }
    }

    /**
     * 切换选择阶段
     */
    fun changeSelect(phaseIndex: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    phaseIndex = phaseIndex
                )
            }
            reloadData(phaseIndex)
        }
    }
}

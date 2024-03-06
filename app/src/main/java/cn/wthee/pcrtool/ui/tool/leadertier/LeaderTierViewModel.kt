package cn.wthee.pcrtool.ui.tool.leadertier

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.enums.LeaderTierType
import cn.wthee.pcrtool.data.enums.TalentType
import cn.wthee.pcrtool.data.model.LeaderTierData
import cn.wthee.pcrtool.data.model.LeaderTierGroup
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.utils.fixedLeaderDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：角色评级
 */
@Immutable
data class LeaderTierUiState(
    val currentGroupList: List<LeaderTierGroup> = arrayListOf(),
    val count: Int = 0,
    val date: String = "",
    //角色评级类型
    val leaderTierType: LeaderTierType = LeaderTierType.ALL,
    val leaderTierMap: HashMap<Int, ResponseData<LeaderTierData>> = hashMapOf(),
    val openDialog: Boolean = false,
    val loadState: LoadState = LoadState.Loading,

    //天赋筛选相关
    val talentType: TalentType = TalentType.ALL,
    val talentUnitMap: HashMap<Int, ArrayList<Int>> = hashMapOf(),
    val openTalentDialog: Boolean = false,

    )

/**
 * 角色评级 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class LeaderTierViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderTierUiState())
    val uiState: StateFlow<LeaderTierUiState> = _uiState.asStateFlow()

    init {
        getTalentUnitMap()
        getLeaderTier(LeaderTierType.ALL.type, TalentType.ALL.type)
    }

    /**
     * 获取角色基本信息
     *
     * @param unitId 角色编号
     */
    fun getCharacterBasicInfo(unitId: Int) = flow {
        emit(unitRepository.getCharacterBasicInfo(unitId))
    }

    /**
     * 获取排行评级
     */
    private fun getLeaderTier(
        type: Int = _uiState.value.leaderTierType.type,
        talentType: Int = _uiState.value.talentType.type
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadState = LoadState.Loading
                )
            }

            val leaderTierMap = _uiState.value.leaderTierMap
            if (leaderTierMap[type] == null) {
                leaderTierMap[type] = apiRepository.getLeaderTier(type)
            }

            //角色id（按天赋筛选）
            val unitIdList = _uiState.value.talentUnitMap[talentType] ?: arrayListOf()

            //分组
            val groupList = arrayListOf<LeaderTierGroup>()
            var count = 0
            leaderTierMap[type]?.data?.let { data ->
                data.leader.forEach { leaderItem ->
                    var group = groupList.find {
                        it.tier == leaderItem.tier
                    }
                    if (group == null) {
                        val descInfo = data.tierSummary.find {
                            it.tier == leaderItem.tier
                        }
                        group =
                            LeaderTierGroup(leaderItem.tier, arrayListOf(), descInfo?.desc ?: "")
                        groupList.add(group)
                    }
                    if (unitIdList.isNotEmpty()) {
                        //天赋筛选
                        if (unitIdList.contains(leaderItem.unitId)) {
                            group.leaderList.add(leaderItem)
                            count++
                        }
                    } else {
                        group.leaderList.add(leaderItem)
                        count++
                    }
                }
            }



            _uiState.update {
                it.copy(
                    currentGroupList = groupList,
                    count = count,
                    date = leaderTierMap[type]?.data?.desc?.fixedLeaderDate ?: "",
                    leaderTierMap = leaderTierMap,
                    leaderTierType = LeaderTierType.getByValue(type),
                    talentType = TalentType.getByType(talentType),
                    loadState = it.loadState.isSuccess(leaderTierMap[type]?.data != null)
                )
            }
        }
    }

    /**
     * 切换类型
     */
    fun changeSelect(type: Int) {
        getLeaderTier(type = type)
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
     * 弹窗状态更新
     */
    fun updateCount(count: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    count = count
                )
            }
        }
    }

    /**
     * 切换天赋类型
     */
    fun changeTalentSelect(type: Int) {
        getLeaderTier(talentType = type)
    }

    /**
     * 弹窗状态更新
     */
    fun changeTalentDialog(openDialog: Boolean) {
        _uiState.update {
            it.copy(
                openTalentDialog = openDialog
            )
        }
    }

    /**
     * 获取角色id按天赋分组
     */
    private fun getTalentUnitMap() {
        viewModelScope.launch {
            val map = unitRepository.getTalentUnitMap()
            _uiState.update {
                it.copy(
                    talentUnitMap = map
                )
            }
        }
    }
}

package cn.wthee.pcrtool.ui.tool.leaderboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.model.FilterLeaderboard
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.getToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：角色排行
 */
@Immutable
data class LeaderboardUiState(
    val leaderboardResponseData: ResponseData<List<LeaderboardData>>? = null,
    val currentList: List<LeaderboardData> = emptyList(),
    //筛选
    val filterLeader: FilterLeaderboard = FilterLeaderboard(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 角色排行 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private val day = 30


    init {
        initLeader()
    }

    /**
     * 初始加载排行
     */
    private fun initLeader() {
        viewModelScope.launch {
            val responseData = apiRepository.getLeader()
            _uiState.update {
                it.copy(
                    leaderboardResponseData = responseData,
                    currentList = sortLeaderboardList(
                        FilterLeaderboard(),
                        responseData.data
                    ) ?: emptyList(),
                    loadingState = it.loadingState.isSuccess(responseData.data != null)
                )
            }
        }
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
     * 获取排行
     */
    fun refreshLeader(filter: FilterLeaderboard) {
        viewModelScope.launch {
            var data = _uiState.value.leaderboardResponseData?.data
            data = sortLeaderboardList(filter, data)

            data?.let {
                _uiState.update {
                    it.copy(
                        currentList = data
                    )
                }
            }
        }
    }

    private fun sortLeaderboardList(
        filter: FilterLeaderboard,
        list: List<LeaderboardData>?
    ): List<LeaderboardData>? {
        //筛选最近编辑的角色
        var data = list
        if (filter.onlyLast) {
            val today = getToday()
            data = data?.filter {
                it.updateTime == null || today.days(it.updateTime, showDay = false)
                    .toInt() <= day
            }
        }

        //排序
        data = data?.sortedWith { o1, o2 ->
            (if (filter.asc) 1 else -1) * when (filter.sort) {
                1 -> {
                    //主线
                    val quest = (o1.questScore).compareTo(o2.questScore)
                    if (quest == 0) {
                        (o1.towerScore + o1.pvpScore + o1.clanScore).compareTo(o2.towerScore + o2.pvpScore + o2.clanScore)
                    } else {
                        quest
                    }
                }

                2 -> {
                    //露娜塔
                    val tower = (o1.towerScore).compareTo(o2.towerScore)
                    if (tower == 0) {
                        (o1.questScore + o1.pvpScore + o1.clanScore).compareTo(o2.questScore + o2.pvpScore + o2.clanScore)
                    } else {
                        tower
                    }
                }

                3 -> {
                    //pvp
                    val pvp = (o1.pvpScore).compareTo(o2.pvpScore)
                    if (pvp == 0) {
                        (o1.questScore + o1.towerScore + o1.clanScore).compareTo(o2.questScore + o2.towerScore + o2.clanScore)
                    } else {
                        pvp
                    }
                }

                4 -> {
                    //公会战
                    val clan = (o1.clanScore).compareTo(o2.clanScore)
                    if (clan == 0) {
                        (o1.questScore + o1.towerScore + o1.pvpScore).compareTo(o2.questScore + o2.towerScore + o2.pvpScore)
                    } else {
                        clan
                    }
                }

                else -> {
                    //综合
                    val all =
                        (o1.questScore + o1.towerScore + o1.pvpScore + o1.clanScore).compareTo(
                            o2.questScore + o2.towerScore + o2.pvpScore + o2.clanScore
                        )
                    //综合分数相等时
                    if (all == 0) {
                        val sub =
                            (o1.towerScore + o1.pvpScore + o1.clanScore).compareTo(o1.towerScore + o2.pvpScore + o2.clanScore)
                        //露娜塔、pvp、公会战总分相同时
                        if (sub == 0) {
                            (o1.pvpScore + o1.clanScore).compareTo(o2.pvpScore + o2.clanScore)
                        } else {
                            sub
                        }
                    } else {
                        all
                    }
                }
            }
        }
        return data
    }

}

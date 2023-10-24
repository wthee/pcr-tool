package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.enums.LeaderTierType
import cn.wthee.pcrtool.data.model.LeaderTierData
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.ui.tool.FilterLeaderboard
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.getToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 角色排行 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class LeaderViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository
) : ViewModel() {
    private val day = 30
    //筛选
    val filterLeader = MutableLiveData(FilterLeaderboard())

    private var leaderData: ResponseData<List<LeaderboardData>>? = null
    private var leaderTierMap: HashMap<Int, ResponseData<LeaderTierData>> = hashMapOf()

    /**
     * 角色评级类型
     */
    val leaderTierType = MutableLiveData(LeaderTierType.ALL)

    /**
     * 获取排行
     */
    fun getLeader(filter: FilterLeaderboard) = flow {
        if (leaderData == null) {
            leaderData = apiRepository.getLeader()
        }
        var data = leaderData?.data
        //筛选最近编辑的角色
        if (filter.onlyLast) {
            val today = getToday()
            data = leaderData?.data?.filter {
                it.updateTime == null || today.days(it.updateTime, showDay = false).toInt() <= day
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
                        (o1.questScore + o1.towerScore + o1.pvpScore + o1.clanScore).compareTo(o2.questScore + o2.towerScore + o2.pvpScore + o2.clanScore)
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

        leaderData?.let {
            emit(ResponseData(it.status, data, it.message))
        }
    }

    /**
     * 获取排行评级
     */
    fun getLeaderTier(type: Int) = flow {
        if (leaderTierMap[type] == null) {
            leaderTierMap[type] = apiRepository.getLeaderTier(type)
        }
        emit(leaderTierMap[type])
    }
}

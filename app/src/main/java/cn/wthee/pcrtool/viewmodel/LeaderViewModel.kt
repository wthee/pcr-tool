package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.model.LeaderTierData
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
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
    private var leaderData: ResponseData<List<LeaderboardData>>? = null
    private var leaderTierMap: HashMap<Int, ResponseData<LeaderTierData>> = hashMapOf()

    /**
     * 获取排行
     */
    fun getLeader(sort: Int, asc: Boolean) = flow {
        if (leaderData == null) {
            leaderData = apiRepository.getLeader()
        }
        //排序
        leaderData?.data = leaderData?.data?.sortedWith { o1, o2 ->
            (if (asc) 1 else -1) * when (sort) {
                1 -> {
                    //露娜塔
                    val tower = (o1.towerScore).compareTo(o2.towerScore)
                    if (tower == 0) {
                        (o1.pvpScore + o2.clanScore).compareTo(o2.pvpScore + o2.clanScore)
                    } else {
                        tower
                    }
                }
                2 -> {
                    //pvp
                    val pvp = (o1.pvpScore).compareTo(o2.pvpScore)
                    if (pvp == 0) {
                        (o1.towerScore + o2.clanScore).compareTo(o2.towerScore + o2.clanScore)
                    } else {
                        pvp
                    }
                }
                3 -> {
                    //公会战
                    val clan = (o1.clanScore).compareTo(o2.clanScore)
                    if (clan == 0) {
                        (o1.towerScore + o2.pvpScore).compareTo(o2.towerScore + o2.pvpScore)
                    } else {
                        clan
                    }
                }
                else -> {
                    //综合
                    val all =
                        (o1.towerScore + o1.pvpScore + o1.clanScore).compareTo(o2.towerScore + o2.pvpScore + o2.clanScore)
                    if (all == 0) {
                        (o1.pvpScore + o1.clanScore).compareTo(o2.pvpScore + o2.clanScore)
                    } else {
                        all
                    }
                }
            }
        }
        leaderData?.let {
            emit(ResponseData(it.status, it.data, it.message))
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

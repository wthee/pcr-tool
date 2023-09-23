package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.ClanBattleRepository
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.intArrayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 公会战 ViewModel
 *
 * @param clanBattleRepository
 */
@HiltViewModel
class ClanBattleViewModel @Inject constructor(
    private val clanBattleRepository: ClanBattleRepository
) : ViewModel() {

    /**
     * 获取公会战记录
     */
    fun getAllClanBattleData(clanBattleId: Int = 0, phase: Int = 2) = flow {
        try {
            val targetList = clanBattleRepository.getAllClanBattleTargetCount(phase, clanBattleId)
            val clanList = clanBattleRepository.getAllClanBattleData(clanBattleId)
            //设置多目标数
            clanList.forEach { info ->
                val subIndex = (phase - info.minPhase) * 5
                info.enemyIdList = info.enemyIds.intArrayList.subList(subIndex, subIndex + 5)
                info.unitIdList = info.unitIds.intArrayList.subList(subIndex, subIndex + 5)

                val findDataList =
                    targetList.filter { target -> target.clanBattleId == info.clanBattleId }
                findDataList.forEachIndexed { index, clanBattleTargetCountData ->
                    if (index != 0) {
                        clanBattleTargetCountData.offset =
                            findDataList[index - 1].enemyPartIds.intArrayList.filter { it > 0 }.size
                    }
                }
                info.targetCountDataList = findDataList
            }
            emit(clanList)
        } catch (e: Exception) {
            LogReportUtil.upload(
                e,
                "getAllClanBattleData#clanBattleId:$clanBattleId,phase:$phase"
            )
            emit(arrayListOf())
        }
    }

}

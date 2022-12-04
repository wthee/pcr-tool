package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.ClanBattleRepository
import cn.wthee.pcrtool.data.db.view.ClanBattleTargetCountData
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
    fun getAllClanBattleData(clanBattleId: Int = 0, pharse: Int = 1) = flow {
        try {
            val targetList = clanBattleRepository.getAllClanBattleTargetCount(pharse)
            val clanList = clanBattleRepository.getAllClanBattleData(clanBattleId)
            //设置多目标数
            clanList.forEach {
                val subIndex = (pharse - 1) * 5
                it.enemyIdList = it.enemyIds.intArrayList.subList(subIndex, subIndex + 5)
                it.unitIdList = it.unitIds.intArrayList.subList(subIndex, subIndex + 5)

                val findData =
                    targetList.firstOrNull { target -> target.clanBattleId == it.clanBattleId }
                if (findData != null) {
                    it.targetCountData = findData
                } else {
                    it.targetCountData = ClanBattleTargetCountData()
                }
            }
            emit(clanList)
        } catch (_: Exception) {

        }
    }

}

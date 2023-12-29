package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ClanBattleDao
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.intArrayList
import javax.inject.Inject

/**
 * 公会战 Repository
 *
 * @param clanBattleDao
 */
class ClanBattleRepository @Inject constructor(private val clanBattleDao: ClanBattleDao) {

    private suspend fun getAllClanBattleData(clanBattleId: Int) = try {
        clanBattleDao.getAllClanBattleData(clanBattleId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllClanBattleData#clanBattleId:$clanBattleId")
        emptyList()
    }

    private suspend fun getAllClanBattleTargetCount(clanBattleId: Int, phase: Int) = try {
        clanBattleDao.getAllClanBattleTargetCount(clanBattleId = clanBattleId, phase = phase)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllClanBattleData#clanBattleId:$clanBattleId,phase:$phase")
        emptyList()
    }

    /**
     * 获取公会战列表
     */
    suspend fun getClanBattleList(clanBattleId: Int, phase: Int) = try {
        val targetList = getAllClanBattleTargetCount(clanBattleId = clanBattleId, phase = phase)
        val clanList = getAllClanBattleData(clanBattleId)
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
        clanList
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getClanBattleList#clanBattleId:$clanBattleId,phase:$phase")
        emptyList()
    }
}
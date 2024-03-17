package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ClanBattleDao
import cn.wthee.pcrtool.data.db.dao.EnemyDao
import cn.wthee.pcrtool.data.db.view.ClanBattleBossData
import cn.wthee.pcrtool.data.db.view.ClanBattleTargetCountData
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.intArrayList
import javax.inject.Inject

/**
 * 公会战 Repository
 *
 * @param clanBattleDao
 */
class ClanBattleRepository @Inject constructor(
    private val clanBattleDao: ClanBattleDao,
    private val enemyDao: EnemyDao
) {

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
        val weaknessDataList = getAllEnemyTalentWeaknessList()
        //设置多目标数
        clanList.forEach { info ->
            val subIndex = (phase - info.minPhase) * 5
            val bossDataList = arrayListOf<ClanBattleBossData>()

            val enemyIdList = info.enemyIds.intArrayList.subList(subIndex, subIndex + 5)
            val unitIdList = info.unitIds.intArrayList.subList(subIndex, subIndex + 5)
            //多部位处理
            val targetFindDataList =
                targetList.filter { target -> target.clanBattleId == info.clanBattleId }
            targetFindDataList.forEachIndexed { index, clanBattleTargetCountData ->
                if (index != 0) {
                    clanBattleTargetCountData.offset =
                        targetFindDataList[index - 1].enemyPartIds.intArrayList.filter { it > 0 }.size
                }
            }

            enemyIdList.forEachIndexed { index, enemyId ->
                bossDataList.add(
                    ClanBattleBossData(
                        enemyId = enemyId,
                        unitId = unitIdList[index],
                        weaknessData = weaknessDataList.find {
                            enemyId == it.enemyId
                        },
                        targetCountData = targetFindDataList.find {
                            it.multiEnemyId % 10 == index + 1 + it.offset
                        } ?: ClanBattleTargetCountData()
                    )
                )
            }

            //设置 boss 信息
            info.bossList = bossDataList

        }

        //返回信息
        clanList
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getClanBattleList#clanBattleId:$clanBattleId,phase:$phase")
        emptyList()
    }


    private suspend fun getAllEnemyTalentWeaknessList() = try {
        enemyDao.getAllEnemyTalentWeaknessList(0)
    } catch (_: Exception) {
        emptyList()
    }
}
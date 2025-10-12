package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ClanBattleDao
import cn.wthee.pcrtool.data.db.dao.EnemyDao
import cn.wthee.pcrtool.data.db.view.ClanBattleBossData
import cn.wthee.pcrtool.data.db.view.ClanBattleTargetCountData
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.ui.MainActivity
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
    // fixme 台服需要 -1，不清楚为什么，先这样处理吧
    private val clanIdOffset = if (MainActivity.regionType == RegionType.TW) 1 else 0

    private suspend fun getAllClanBattleData(clanBattleId: Int) = try {
        clanBattleDao.getAllClanBattleData(clanBattleId = clanBattleId, clanIdOffset = clanIdOffset)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllClanBattleData#clanBattleId:$clanBattleId")
        emptyList()
    }

    private suspend fun getAllClanBattleTargetCount(clanBattleId: Int, phase: Int) = try {
        clanBattleDao.getAllClanBattleTargetCount(
            clanBattleId = clanBattleId,
            phase = phase,
            clanIdOffset = clanBattleId
        )
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllClanBattleData#clanBattleId:$clanBattleId,phase:$phase")
        emptyList()
    }

    /**
     * 获取公会战列表
     * @param fixed 获取详情是不再次修正
     */
    suspend fun getClanBattleList(clanBattleId: Int, phase: Int, fixed: Boolean = true) = try {
        val fixedId = if (clanBattleId != 0 && fixed) {
            clanBattleId + clanIdOffset
        } else {
            clanBattleId
        }
        val targetList = getAllClanBattleTargetCount(clanBattleId = fixedId, phase = phase)
        val clanList = getAllClanBattleData(clanBattleId = fixedId)
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
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllEnemyTalentWeaknessList")
        emptyList()
    }
}
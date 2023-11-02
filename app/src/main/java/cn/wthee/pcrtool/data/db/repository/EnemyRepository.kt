package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EnemyDao
import cn.wthee.pcrtool.data.db.view.ClanBattleTargetCountData
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.intArrayList
import javax.inject.Inject

/**
 * 怪物信息 Repository
 *
 * @param enemyDao
 */
class EnemyRepository @Inject constructor(private val enemyDao: EnemyDao) {

    /**
     * 获取多目标部位属性
     */
    suspend fun getEnemyAttrList(enemyPartIds: List<Int>) = try {
        val list = arrayListOf<EnemyParameterPro>()
        enemyPartIds.forEach {
            if (it != 0) {
                val data = enemyDao.getEnemyAttr(it)
                list.add(data)
            }
        }
        list
    } catch (_: Exception) {
        arrayListOf()
    }

    suspend fun getEnemyAttr(enemyId: Int) = try {
        enemyDao.getEnemyAttr(enemyId)
    } catch (_: Exception) {
        null
    }

    suspend fun getAllBossIds() = enemyDao.getAllBossIds()

    suspend fun getMultiTargetEnemyInfo(enemyId: Int) = enemyDao.getMultiTargetEnemyInfo(enemyId)

    suspend fun getAtkCastTime(unitId: Int) = enemyDao.getAtkCastTime(unitId)

    /**
     * 获取多目标部位属性
     */
    suspend fun getMultiEnemyAttr(targetCountDataList: List<ClanBattleTargetCountData>) = try {
        val map = hashMapOf<Int, List<EnemyParameterPro>>()
        targetCountDataList.forEach { targetCountData ->
            map[targetCountData.multiEnemyId] = getEnemyAttrList(
                targetCountData.enemyPartIds.intArrayList
            )
        }
        map
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getMultiEnemyAttr#targetCountDataList:$targetCountDataList")
        hashMapOf()
    }
}
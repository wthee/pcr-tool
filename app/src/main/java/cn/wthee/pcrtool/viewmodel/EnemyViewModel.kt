package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EnemyRepository
import cn.wthee.pcrtool.data.db.view.ClanBattleTargetCountData
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.utils.intArrayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 怪物信息 ViewModel
 *
 * @param enemyRepository
 */
@HiltViewModel
class EnemyViewModel @Inject constructor(
    private val enemyRepository: EnemyRepository
) : ViewModel() {

    /**
     * 获取敌人属性
     *
     * @param enemyId 敌人编号
     */
    fun getEnemyAttr(enemyId: Int) = flow {
        try {
            val data = enemyRepository.getEnemyAttr(enemyId)
            emit(data)
        } catch (_: Exception) {

        }
    }

    /**
     * 获取 BOSS 属性
     *
     * @param enemyIds boss编号列表
     */
    fun getAllBossAttr(enemyIds: List<Int>) = flow {
        try {
            val list = arrayListOf<EnemyParameterPro>()
            enemyIds.forEach {
                val data = enemyRepository.getEnemyAttr(it)
                list.add(data)
            }
            emit(list)
        } catch (_: Exception) {

        }
    }


    /**
     * 获取 BOSS 属性，测试用
     */
    fun getAllBossIds() = flow {
        try {
            val list = arrayListOf<Int>()
            val boss = enemyRepository.getAllClanBattleBossAttr()
            boss.forEach {
                list.add(it.unitId)
            }
            emit(list)
        } catch (_: Exception) {

        }
    }

    /**
     * 获取多目标部位属性
     */
    fun getMultiEnemyAttr(targetCountData: ClanBattleTargetCountData) = flow {
        try {
            val map = hashMapOf<Int, List<EnemyParameterPro>>()
            map[targetCountData.multiEnemyId] = getMultiPartEnemyList(
                targetCountData.enemyPartIds.intArrayList
            )
            emit(map)
        } catch (_: Exception) {

        }
    }


    /**
     * 获取多目标部位属性
     *
     * @param enemyId 敌人编号
     */
    fun getMutiTargetEnemyInfo(enemyId: Int) = flow {
        try {
            val data = enemyRepository.getMultiTargetEnemyInfo(enemyId)
            val list = getMultiPartEnemyList(data.enemyPartIds.intArrayList)
            emit(list)
        } catch (_: Exception) {

        }
    }

    /**
     * 获取多目标部位属性
     */
    private suspend fun getMultiPartEnemyList(enemyPartIds: List<Int>) = try {
        val list = arrayListOf<EnemyParameterPro>()
        enemyPartIds.forEach {
            if (it != 0) {
                val data = enemyRepository.getEnemyAttr(it)
                list.add(data)
            }
        }
        list
    } catch (_: Exception) {
        arrayListOf()
    }
}
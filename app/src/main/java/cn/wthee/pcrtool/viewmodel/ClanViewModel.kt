package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.ClanRepository
import cn.wthee.pcrtool.data.db.view.ClanBattleTargetCountData
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.utils.intArrayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 团队战 ViewModel
 *
 * @param clanRepository
 */
@HiltViewModel
class ClanViewModel @Inject constructor(
    private val clanRepository: ClanRepository
) : ViewModel() {

    /**
     * 获取团队战记录
     */
    fun getAllClanBattleData(clanBattleId: Int = 0, pharse: Int = 1) = flow {
        try {
            val targetList = clanRepository.getAllClanBattleTargetCount(pharse)
            val clanList = clanRepository.getAllClanBattleData(clanBattleId)
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

    /**
     * 获取敌人属性
     *
     * @param enemyId 敌人编号
     */
    fun getEnemyAttr(enemyId: Int) = flow {
        try {
            val data = clanRepository.getBossAttr(enemyId)
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
                val data = clanRepository.getBossAttr(it)
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
            val boss = clanRepository.getAllBossAttr()
            boss.forEach {
                list.add(it.unit_id)
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
            val list = arrayListOf<EnemyParameterPro>()
            targetCountData.enemyPartIds.intArrayList.forEach {
                if (it != 0) {
                    val data = clanRepository.getBossAttr(it)
                    list.add(data)
                }
            }
            map[targetCountData.multiEnemyId] = list
            emit(map)
        } catch (_: Exception) {

        }
    }

}

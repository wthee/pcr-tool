package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.ClanRepository
import cn.wthee.pcrtool.data.db.view.ClanBossTargetInfo
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 活动 ViewModel
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
    fun getAllClanBattleData() = flow {
        emit(clanRepository.getAllClanBattleData())
    }

    /**
     * 获取单个团队战信息
     *
     * @param clanId 团队战编号
     */
    fun getClanInfo(clanId: Int) = flow {
        emit(clanRepository.getClanInfo(clanId))
    }

    /**
     * 获取敌人属性
     *
     * @param enemyId 敌人编号
     */
    fun getEnemyAttr(enemyId: Int) = flow {
        val data = clanRepository.getBossAttr(enemyId)
        emit(data)
    }

    /**
     * 获取 BOSS 属性
     *
     * @param enemyIds boss编号列表
     */
    fun getAllBossAttr(enemyIds: List<Int>) = flow {
        val list = arrayListOf<EnemyParameterPro>()
        enemyIds.forEach {
            val data = clanRepository.getBossAttr(it)
            list.add(data)
        }
        emit(list)
    }

    /**
     * 获取 BOSS 属性，测试用
     */
    fun getAllBossIds() = flow {
        val list = arrayListOf<Int>()
        val boss = clanRepository.getAllBossAttr()
        boss.forEach {
            list.add(it.unit_id)
        }
        emit(list)
    }

    /**
     * 获取多目标部位属性
     *
     * @param bossList boss 信息
     */
    fun getPartEnemyAttr(bossList: List<ClanBossTargetInfo>) = flow {
        val map = hashMapOf<Int, List<EnemyParameterPro>>()
        bossList.forEach { boss ->
            if (boss.partEnemyIds.isNotEmpty()) {
                val list = arrayListOf<EnemyParameterPro>()
                boss.partEnemyIds.forEach {
                    val data = clanRepository.getBossAttr(it)
                    list.add(data)
                }
                map[boss.unitId] = list
            }
        }
        emit(map)
    }
}

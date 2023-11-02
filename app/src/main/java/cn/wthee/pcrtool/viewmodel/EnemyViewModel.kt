package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EnemyRepository
import cn.wthee.pcrtool.utils.LogReportUtil
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
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getEnemyAttr#enemyId:$enemyId")
        }
    }


    /**
     * 获取 BOSS 属性，测试用
     */
    fun getAllBossIds() = flow {
        try {
            val list = arrayListOf<Int>()
            val boss = enemyRepository.getAllBossIds()
            boss.forEach {
                list.add(it.unitId)
            }
            emit(list)
        } catch (_: Exception) {

        }
    }


    /**
     * 获取多目标部位属性
     *
     * @param enemyId 敌人编号
     */
    fun getMultiTargetEnemyInfo(enemyId: Int) = flow {
        try {
            val data = enemyRepository.getMultiTargetEnemyInfo(enemyId)
            data?.let {
                val list = enemyRepository.getEnemyAttrList(data.enemyPartIds.intArrayList)
                emit(list)
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getMultiTargetEnemyInfo#enemyId:$enemyId")
        }
    }

}

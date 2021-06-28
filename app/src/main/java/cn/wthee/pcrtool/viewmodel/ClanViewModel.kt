package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ClanRepository
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.ClanBossTargetInfo
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    var clanInfoList = MutableLiveData<List<ClanBattleInfo>>()
    var clanInfo = MutableLiveData<ClanBattleInfo>()
    var allClanBossAttr = MutableLiveData<List<EnemyParameterPro>>()
    var partEnemyAttrMap = MutableLiveData(hashMapOf<Int, List<EnemyParameterPro>>())

    /**
     * 获取团队战记录
     */
    fun getAllClanBattleData() {
        viewModelScope.launch {
            val data = clanRepository.getAllClanBattleData()
            clanInfoList.postValue(data)
        }
    }

    /**
     * 获取单个团队战信息
     *
     * @param clanId 团队战编号
     */
    fun getClanInfo(clanId: Int) {
        viewModelScope.launch {
            val data = clanRepository.getClanInfo(clanId)
            clanInfo.postValue(data)
        }
    }

    /**
     * 获取 BOSS 属性
     *
     * @param enemyIds boss编号列表
     */
    fun getAllBossAttr(enemyIds: List<Int>) {
        viewModelScope.launch {
            val list = arrayListOf<EnemyParameterPro>()
            enemyIds.forEach {
                val data = clanRepository.getBossAttr(it)
                list.add(data)
            }
            allClanBossAttr.postValue(list)
        }
    }

    /**
     * 获取多目标部位属性
     *
     * @param unitId boss 单位编号
     * @param enemyId boss编号列表
     */
    fun getPartEnemysAttr(bossList: List<ClanBossTargetInfo>) {
        viewModelScope.launch {
            val map = partEnemyAttrMap.value ?: hashMapOf()
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
            partEnemyAttrMap.postValue(map)
        }
    }
}

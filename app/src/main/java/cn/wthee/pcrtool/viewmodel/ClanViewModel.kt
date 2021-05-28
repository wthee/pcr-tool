package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.EnemyParameter
import cn.wthee.pcrtool.data.db.repository.ClanRepository
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
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
    var allClanBossAttr = MutableLiveData<List<EnemyParameter>>()


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
            val list = arrayListOf<EnemyParameter>()
            enemyIds.forEach {
                val data = clanRepository.getBossAttr(it)
                list.add(data)
            }
            allClanBossAttr.postValue(list)
        }
    }

}

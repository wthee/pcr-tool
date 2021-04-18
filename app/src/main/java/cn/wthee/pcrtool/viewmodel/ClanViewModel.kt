package cn.wthee.pcrtool.viewmodel

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ClanRepository
import cn.wthee.pcrtool.data.entity.EnemyParameter
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.database.getDatabaseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 活动 ViewModel
 *
 * 数据来源 [ClanRepository]
 */
@HiltViewModel
class ClanViewModel @Inject constructor(
    private val repository: ClanRepository
) : ViewModel() {

    var clanInfo = MutableLiveData<List<ClanBattleInfo>>()
    var clanBossAttr = MutableLiveData<EnemyParameter>()
    var state: Parcelable? = null


    /**
     * 获取团队战记录
     */
    fun getAllClanBattleData() {
        viewModelScope.launch {
            val data = repository.getAllClanBattleData(getDatabaseType())
            clanInfo.postValue(data)
        }
    }

    /**
     * 获取 BOSS 属性
     */
    fun getBossAttr(enemyId: Int) {
        viewModelScope.launch {
            val data = repository.getBossAttr(enemyId)
            clanBossAttr.postValue(data)
        }
    }

}

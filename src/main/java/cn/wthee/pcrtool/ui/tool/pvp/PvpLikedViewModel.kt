package cn.wthee.pcrtool.ui.tool.pvp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.data.db.repository.PvpRepository
import kotlinx.coroutines.launch

/**
 * 竞技场收藏 ViewModel
 *
 * 数据来源 [PvpRepository]
 */
class PvpLikedViewModel(
    private val repository: PvpRepository
) : ViewModel() {

    var allData = MutableLiveData<List<PvpLikedData>>()

    /**
     * 根据游戏版本 [region]，获取收藏信息
     */
    fun getLiked(region: Int) {
        viewModelScope.launch {
            val data = repository.getLiked(region)
            allData.postValue(data)
        }
    }


}

package cn.wthee.pcrtool.ui.tool.pvp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.data.db.repository.PvpRepository
import kotlinx.coroutines.launch


class PvpLikedViewModel(
    private val repository: PvpRepository
) : ViewModel() {

    var data = MutableLiveData<List<PvpLikedData>>()


    //收藏队伍信息
    fun getLiked(region: Int) {
        viewModelScope.launch {
            data.postValue(repository.getLiked(region))
        }
    }

    //收藏队伍信息
    suspend fun getLikedData(region: Int) = repository.getLiked(region)

}

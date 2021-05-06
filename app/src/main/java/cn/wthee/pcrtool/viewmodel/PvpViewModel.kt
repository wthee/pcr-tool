package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.data.db.repository.PvpRepository
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import com.google.gson.JsonArray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 竞技场收藏 ViewModel
 *
 * 数据来源 [PvpRepository]
 */
@HiltViewModel
class PvpViewModel @Inject constructor(
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


    val pvpResult = MutableLiveData<ResponseData<List<PvpResultData>>>()

    /**
     * 查询
     */
    fun getPVPData(ids: JsonArray) {
        viewModelScope.launch {
            if (pvpResult.value == null) {
                val data = MyAPIRepository.getInstance().getPVPData(ids)
                pvpResult.postValue(data)
            }
        }
    }
}

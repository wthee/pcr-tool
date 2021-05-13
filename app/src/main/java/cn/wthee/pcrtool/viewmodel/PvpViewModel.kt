package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
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
    private val repository: PvpRepository,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    var allFavorites = MutableLiveData<List<PvpFavoriteData>>()
    var favorites = MutableLiveData<List<PvpFavoriteData>>()
    val pvpResult = MutableLiveData<ResponseData<List<PvpResultData>>>()

    /**
     * 根据游戏版本 [region]，获取收藏信息
     */
    fun getAllFavorites(region: Int) {
        viewModelScope.launch {
            val data = repository.getLiked(region)
            allFavorites.postValue(data)
        }
    }

    /**
     * 根据防守队伍 [defs] 获取收藏信息
     */
    fun getFavoritesList(defs: String, region: Int) {
        viewModelScope.launch {
            val data = repository.getLikedList(defs, region)
            favorites.postValue(data)
        }
    }

    /**
     * 新增收藏信息
     */
    fun insert(data: PvpFavoriteData) {
        viewModelScope.launch {
            repository.insert(data)
            getFavoritesList(data.defs, data.region)
        }
    }

    /**
     * 删除收藏信息
     */
    fun delete(atks: String, defs: String, region: Int) {
        viewModelScope.launch {
            repository.delete(atks, defs, region)
            getAllFavorites(region)
            getFavoritesList(defs, region)
        }
    }

    /**
     * 查询
     */
    fun getPVPData(ids: JsonArray) {
        viewModelScope.launch {
            if (pvpResult.value == null) {
                val data = apiRepository.getPVPData(ids)
                pvpResult.postValue(data)
            }
        }
    }
}

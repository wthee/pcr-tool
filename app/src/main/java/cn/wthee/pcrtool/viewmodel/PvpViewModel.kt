package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.data.db.repository.PvpRepository
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.calcDate
import cn.wthee.pcrtool.utils.getToday
import com.google.gson.JsonArray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 竞技场 ViewModel
 *
 * @param pvpRepository
 * @param apiRepository
 */
@HiltViewModel
class PvpViewModel @Inject constructor(
    private val pvpRepository: PvpRepository,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    var allFavorites = MutableLiveData<List<PvpFavoriteData>>()
    var history = MutableLiveData<List<PvpHistoryData>>()
    var favorites = MutableLiveData<List<PvpFavoriteData>>()
    val pvpResult = MutableLiveData<ResponseData<List<PvpResultData>>>()
    var requesting = false

    /**
     * 获取收藏信息
     */
    fun getAllFavorites() {
        viewModelScope.launch {
            val data = pvpRepository.getLiked(MainActivity.regionType)
            allFavorites.postValue(data)
        }
    }

    /**
     * 根据防守队伍 [defs] 获取收藏信息
     */
    fun getFavoritesList(defs: String) {
        viewModelScope.launch {
            val data = pvpRepository.getLikedList(defs, MainActivity.regionType)
            favorites.postValue(data)
        }
    }

    /**
     * 新增收藏信息
     */
    fun insert(data: PvpFavoriteData) {
        viewModelScope.launch {
            pvpRepository.insert(data)
            getFavoritesList(data.defs)
        }
    }

    /**
     * 删除收藏信息
     */
    fun delete(atks: String, defs: String) {
        viewModelScope.launch {
            pvpRepository.delete(atks, defs, MainActivity.regionType)
            getAllFavorites()
            getFavoritesList(defs)
        }
    }


    /**
     * 获取搜索历史信息
     */
    fun getHistory() {
        viewModelScope.launch {
            val data = pvpRepository.getHistory(MainActivity.regionType)
            history.postValue(data)
        }
    }

    /**
     * 获取搜索历史信息
     */
    fun getRecentlyUsedUnitList() = flow {
        val today = getToday()
        //获取前30天
        val beforeDate = calcDate(today, 30, true)
        val data = pvpRepository.getHistory(MainActivity.regionType, beforeDate, today)
        val unitList = arrayListOf<PvpCharacterData>()
        val map = hashMapOf<Int, Int>()
        //统计数量
        data.forEach { pvpData ->
            pvpData.getDefIds().forEach { unitId ->
                var count = 1
                if (map[unitId] != null) {
                    count = map[unitId]!! + 1
                }
                map[unitId] = count
            }
        }
        map.forEach {
            unitList.add(
                PvpCharacterData(
                    unitId = it.key,
                    count = it.value
                )
            )
        }
        var list = unitList.sortedByDescending { it.count }
        if (list.size > 10) {
            list = list.subList(0, 10)
        }
        emit(list)
    }

    /**
     * 新增搜索信息
     */
    fun insert(data: PvpHistoryData) {
        viewModelScope.launch {
            pvpRepository.insert(data)
        }
    }

    /**
     * 查询
     */
    fun getPVPData(ids: JsonArray) {
        viewModelScope.launch {
            if (pvpResult.value == null && !requesting) {
                requesting = true
                val data = apiRepository.getPVPData(ids)
                pvpResult.postValue(data)
                requesting = false
            }
        }
    }
}

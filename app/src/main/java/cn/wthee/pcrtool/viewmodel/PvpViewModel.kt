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
import cn.wthee.pcrtool.utils.second
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

    val pvpResult = MutableLiveData<ResponseData<List<PvpResultData>>>()
    var requesting = false

    /**
     * 获取收藏信息
     */
    fun getAllFavorites() = flow {
        val data = pvpRepository.getLiked(MainActivity.regionType)
        emit(data)
    }

    /**
     * 根据防守队伍 [defs] 获取收藏信息
     */
    fun getFavoritesList(defs: String) = flow {
        val data = pvpRepository.getLikedList(defs, MainActivity.regionType)
        emit(data)
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
    fun getHistory() = flow {
        val data = pvpRepository.getHistory(MainActivity.regionType)
        emit(data)
    }

    /**
     * 获取搜索历史信息
     */
    fun getRecentlyUsedUnitList(characterDataList: List<PvpCharacterData>) = flow {
        try {
            val today = getToday()
            //获取前60天
            val beforeDate = calcDate(today, 60, true)
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
            //数量限制
            val limit = 50
            var list = unitList.sortedByDescending { it.count }
            if (list.size > limit) {
                list = list.subList(0, limit)
            }
            //处理最近使用角色的站位信息
            list.forEach {
                it.position = characterDataList.find { d -> d.unitId == it.unitId }?.position ?: 0
            }
            emit(list)
        } catch (_: Exception) {

        }
    }


    /**
     * 新增搜索信息
     */
    fun insert(data: PvpHistoryData) {
        viewModelScope.launch {
            val preData = pvpRepository.getHistory(MainActivity.regionType)
            //避免重复插入
            if (preData.isNotEmpty()) {
                //与上一记录不相同或间隔大于10分钟，插入新记录
                if (data.date.second(preData[0].date) > 10 * 60 || data.defs != preData[0].defs) {
                    pvpRepository.insert(data)
                }
            } else {
                pvpRepository.insert(data)
            }
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

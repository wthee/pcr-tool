package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.data.db.repository.PvpRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getIdStr
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.calcDate
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.second
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import java.util.UUID
import javax.inject.Inject


/**
 * 页面状态：竞技场查询
 */
@Immutable
data class PvpUiState(
    //所有角色
    val allUnitList: List<PvpCharacterData> = emptyList(),
    //查询结果
    val pvpResult: ResponseData<List<PvpResultData>>? = null,
    //收藏信息
    val favoritesList: List<PvpFavoriteData> = emptyList(),
    //所有收藏信息
    val allFavoritesList: List<PvpFavoriteData> = emptyList(),
    //历史记录
    val historyList: List<PvpHistoryData> = emptyList(),
    //最近使用的角色
    val recentlyUsedUnitList: List<PvpCharacterData> = emptyList(),
    val requesting: Boolean = false,
    val idArray: JsonArray? = null
)

/**
 * 竞技场 ViewModel
 *
 * @param pvpRepository
 * @param apiRepository
 */
@HiltViewModel
class PvpViewModel @Inject constructor(
    private val pvpRepository: PvpRepository,
    private val apiRepository: ApiRepository,
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PvpUiState())
    val uiState: StateFlow<PvpUiState> = _uiState.asStateFlow()


    init {
        getAllCharacter()
        getAllFavorites()
        getHistory()
    }

    /**
     * 获取收藏信息
     */
    private fun getAllFavorites() {
        viewModelScope.launch {
            val data = pvpRepository.getLiked(MainActivity.regionType.value)
            _uiState.update {
                it.copy(
                    allFavoritesList = data
                )
            }
        }
    }

    /**
     * 根据防守队伍 [defs] 获取收藏信息
     */
    private fun getFavoritesList(defs: String) {
        viewModelScope.launch {
            val data = pvpRepository.getLikedList(defs, MainActivity.regionType.value)
            _uiState.update {
                it.copy(
                    favoritesList = data
                )
            }
        }
    }

    /**
     * 新增收藏信息
     */
    fun insert(data: PvpFavoriteData) {
        viewModelScope.launch {
            pvpRepository.insert(data)
            getAllFavorites()
            getFavoritesList(data.defs)
        }
    }

    /**
     * 删除收藏信息
     */
    fun delete(atks: String, defs: String) {
        viewModelScope.launch {
            pvpRepository.delete(atks, defs, MainActivity.regionType.value)
            getAllFavorites()
            getFavoritesList(defs)
        }
    }


    /**
     * 获取搜索历史信息
     */
    private fun getHistory() {
        viewModelScope.launch {
            val data = pvpRepository.getHistory(MainActivity.regionType.value, 10)
            _uiState.update {
                it.copy(
                    historyList = data
                )
            }
        }
    }

    /**
     * 获取搜索历史信息
     */
    private fun getRecentlyUsedUnitList(characterDataList: List<PvpCharacterData>) {
        viewModelScope.launch {
            try {
                val today = getToday()
                //获取前60天
                val beforeDate = calcDate(today, 60, true)
                //仅保存60天数据，删除超过60天的历史数据
                pvpRepository.deleteOldHistory(MainActivity.regionType.value, beforeDate)

                //删除旧数据后，查询全部数据
                val data = pvpRepository.getHistory(MainActivity.regionType.value, Int.MAX_VALUE)

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
                //角色数量限制
                val limit = 50
                var list = unitList.sortedByDescending { it.count }
                if (list.size > limit) {
                    list = list.subList(0, limit)
                }
                //处理最近使用角色的站位信息
                list.forEach {
                    it.position =
                        characterDataList.find { d -> d.unitId == it.unitId }?.position ?: 0
                }
                _uiState.update {
                    it.copy(
                        recentlyUsedUnitList = list
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(
                    e,
                    "getRecentlyUsedUnitList#characterDataList:$characterDataList"
                )
            }
        }

    }


    /**
     * 新增搜索信息
     */
    fun insert(data: PvpHistoryData) {
        viewModelScope.launch {
            val preData = pvpRepository.getHistory(MainActivity.regionType.value, 1)
            //避免重复插入
            if (preData.isNotEmpty()) {
                //与上一记录不相同或间隔大于10分钟，插入新记录
                if (data.date.second(preData[0].date) > 10 * 60 || data.defs != preData[0].defs) {
                    pvpRepository.insert(data)
                }
            } else {
                pvpRepository.insert(data)
            }
            getHistory()
        }
    }

    /**
     * 查询
     */
    private fun getPvpData(ids: JsonArray) {
        viewModelScope.launch {
            if (_uiState.value.pvpResult == null && !_uiState.value.requesting) {
                _uiState.update {
                    it.copy(
                        requesting = true
                    )
                }
                val data = apiRepository.getPvpData(ids)

                _uiState.update {
                    it.copy(
                        pvpResult = data,
                        requesting = false,
                        idArray = ids
                    )
                }
            }
        }
    }

    /**
     * 重新查询
     */
    fun research() {
        viewModelScope.launch {
            resetResult()
            _uiState.value.idArray?.let { getPvpData(it) }
        }
    }

    /**
     * 从收藏或历史搜索
     */
    fun searchByDefs(defs: List<Int>) {
        viewModelScope.launch {
            resetResult()
            val selectedData = getPvpCharacterByIds(defs)
            val selectedIds = selectedData as ArrayList<PvpCharacterData>
            selectedIds.sortWith(comparePvpCharacterData())
            MainActivity.navViewModel.selectedPvpData.postValue(selectedIds)
            searchByCharacterList(selectedIds)
        }
    }

    /**
     * 处理数据后搜索
     */
    fun searchByCharacterList(characterDataList: List<PvpCharacterData>? = null) {
        viewModelScope.launch {
            val list = characterDataList ?: MainActivity.navViewModel.selectedPvpData.value
            if (list == null || list.contains(PvpCharacterData())) {
                ToastUtil.short(getString(R.string.tip_select_5))
                return@launch
            }
            resetResult()
            //加载数据
            val defIds = list.subList(0, 5).getIdStr()

            var unSplitDefIds = ""
            var isError = false

            val idArray = buildJsonArray {
                for (sel in list.subList(0, 5)) {
                    if (sel.unitId == 0) {
                        isError = true
                        return@buildJsonArray
                    }
                    add(sel.unitId)
                    unSplitDefIds += "${sel.unitId}-"
                }
            }

            if (!isError) {
                MainActivity.navViewModel.showResult.postValue(true)
                //搜索
                getPvpData(idArray)
                getFavoritesList(defIds)

                //添加搜索记录
                insert(
                    PvpHistoryData(
                        id = UUID.randomUUID().toString(),
                        defs = "${MainActivity.regionType.value}@$unSplitDefIds",
                        date = getToday(),
                    )
                )
            } else {
                ToastUtil.short(getString(R.string.tip_select_5))
            }
        }
    }
    /**
     * 竞技场角色信息
     */
    private fun getAllCharacter() {
        viewModelScope.launch {
            val data = unitRepository.getCharacterByPosition(1, 999)
            _uiState.update {
                it.copy(
                    allUnitList = data
                )
            }
            //加载常用角色
            getRecentlyUsedUnitList(data)
        }
    }

    /**
     * 角色站位
     */
    private suspend fun getPvpCharacterByIds(ids: List<Int>) =
        try {
            unitRepository.getCharacterByIds(ids).filter { it.position > 0 }
        } catch (e: Exception) {
            arrayListOf()
        }

    /**
     * 改变请求状态
     */
    fun changeRequesting(requesting: Boolean) {
        _uiState.update {
            it.copy(
                requesting = requesting
            )
        }
    }

    /**
     * 改变请求状态
     */
    private fun resetResult() {
        _uiState.update {
            it.copy(
                pvpResult = null
            )
        }
    }
}

package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecordData
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.repository.MockGachaRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.db.view.MockGachaProData
import cn.wthee.pcrtool.data.enums.MockGachaType
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.data.model.getIdsStr
import cn.wthee.pcrtool.data.model.getRaritysStr
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.getData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.simpleDateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * 页面状态：主线地图
 */
@Immutable
data class MockGachaUiState(
    //查询历史
    val historyList: List<MockGachaProData> = emptyList(),
    //抽卡结果
    val resultRecordList: List<MockGachaResultRecordData> = emptyList(),
    //模拟卡池结果显示
    val showResult: Boolean = false,
    //模拟卡池数据
    val gachaId: String = "",
    //模拟卡池类型
    val mockGachaType: MockGachaType = MockGachaType.PICK_UP,
    //模拟卡池 pickUp 角色
    val pickUpList: List<GachaUnitInfo> = emptyList(),
    //卡池角色
    val unitsInGacha: UnitsInGacha? = null,
    val openDialog: Boolean = false,

    )


/**
 * 模拟卡池 ViewModel
 *
 * @param unitRepository
 * @param mockGachaRepository
 */
@HiltViewModel
class MockGachaViewModel @Inject constructor(
    private val gachaRepository: GachaRepository,
    private val unitRepository: UnitRepository,
    private val mockGachaRepository: MockGachaRepository
) : ViewModel() {

    companion object {
        //模拟抽卡fes最大up数
        private const val MOCK_GACHA_FES_MAX_UP_COUNT = 2

        //模拟抽卡最大up数
        private const val MOCK_GACHA_MAX_UP_COUNT = 12
    }

    private val _uiState = MutableStateFlow(MockGachaUiState())
    val uiState: StateFlow<MockGachaUiState> = _uiState.asStateFlow()

    init {
        val mockGachaType: Int? = getData(NavRoute.MOCK_GACHA_TYPE, true)
        val pickUpList: List<GachaUnitInfo>? = getData(NavRoute.PICKUP_LIST, true)

        if (mockGachaType != null && pickUpList != null) {
            _uiState.update {
                it.copy(
                    mockGachaType = MockGachaType.getByValue(mockGachaType),
                    pickUpList = pickUpList
                )
            }
        }
        getGachaUnits()
        getHistory()
    }


    /**
     * 获取卡池角色
     */
    private fun getGachaUnits() {
        viewModelScope.launch {
            try {
                val fesUnitInfo = gachaRepository.getFesUnitIdList()
                if (fesUnitInfo != null) {
                    val fesList = arrayListOf<GachaUnitInfo>()
                    fesUnitInfo.getIds().forEachIndexed { index, i ->
                        fesList.add(
                            GachaUnitInfo(
                                unitId = i,
                                unitName = fesUnitInfo.getNames()[index],
                                isLimited = 1,
                                rarity = 3
                            )
                        )
                    }

                    val units = UnitsInGacha(
                        unitRepository.getGachaUnits(1),
                        unitRepository.getGachaUnits(2),
                        unitRepository.getGachaUnits(3),
                        unitRepository.getGachaUnits(4).filter {
                            !fesUnitInfo.getIds().contains(it.unitId)
                        },
                        fesList
                    )
                    _uiState.update {
                        it.copy(
                            unitsInGacha = units
                        )
                    }
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getGachaUnits")
            }
        }
    }

    /**
     * 创建卡池
     */
    fun createMockGacha(
        gachaId: String,
        mockGachaType: MockGachaType,
        pickUpList: List<GachaUnitInfo>
    ) {
        viewModelScope.launch {
            val nowTime = getToday()
            //处理不同顺序相同角色
            val sorted = sortGachaUnitInfo(mockGachaType, pickUpList)
            val data = MockGachaData(
                gachaId,
                MainActivity.regionType.value,
                mockGachaType.type,
                sorted.getIdsStr(),
                nowTime,
                nowTime
            )
            mockGachaRepository.insertGacha(data)
            //加载历史数据
            getHistory()
        }
    }

    /**
     * 处理不同顺序相同角色
     */
    private fun sortGachaUnitInfo(
        mockGachaType: MockGachaType,
        pickUpList: List<GachaUnitInfo>
    ): List<GachaUnitInfo> {
        val sorted = if (mockGachaType == MockGachaType.PICK_UP_SINGLE) {
            //复刻自选但角色，去除最后一个
            if (pickUpList.size > 1) {
                pickUpList.subList(0, pickUpList.size - 1).sortedBy {
                    it.unitId
                } + pickUpList.last()
            } else {
                pickUpList
            }
        } else {
            pickUpList.sortedBy {
                it.unitId
            }
        }
        return sorted
    }

    /**
     * 保存抽取结果
     */
    fun addMockResult(gachaId: String, resultList: List<GachaUnitInfo>) {
        viewModelScope.launch {
            val updateTime = getToday(true)
            mockGachaRepository.insertResult(
                MockGachaResultRecordData(
                    UUID.randomUUID().toString(),
                    gachaId,
                    resultList.getIdsStr(),
                    resultList.getRaritysStr(),
                    updateTime
                )
            )
            //更新日期
            mockGachaRepository.updateGacha(gachaId, updateTime.simpleDateFormat)
            //重新获取结果
            getResult(gachaId = gachaId)
        }
    }

    /**
     * 获取历史记录
     */
    private fun getHistory() {
        viewModelScope.launch {
            val data = mockGachaRepository.getHistory(MainActivity.regionType.value)
            _uiState.update {
                it.copy(
                    historyList = data
                )
            }
        }
    }

    /**
     * 获取卡池
     */
    suspend fun getGachaByPickUp(
        mockGachaType: MockGachaType,
        pickUpList: List<GachaUnitInfo>
    ): MockGachaData? {
        //处理不同顺序相同角色
        val sorted = sortGachaUnitInfo(mockGachaType, pickUpList)
        return mockGachaRepository.getGachaByPickUpIds(
            MainActivity.regionType.value,
            mockGachaType.type,
            sorted.getIdsStr()
        )
    }


    /**
     * 获取抽取结果
     */
    fun getResult(gachaId: String) {
        viewModelScope.launch {
            val data = mockGachaRepository.getResultByGachaId(gachaId)
            _uiState.update {
                it.copy(
                    resultRecordList = data
                )
            }
        }
    }

    /**
     * 删除指定卡池的所有记录
     */
    fun deleteGachaResultByGachaId(gachaId: String) {
        viewModelScope.launch {
            try {
                mockGachaRepository.deleteGachaResultByGachaId(gachaId)
                //刷新记录
                getResult(gachaId)
            } catch (e: Exception) {
                LogReportUtil.upload(e, "deleteGachaResultByGachaId#gachaId:$gachaId")
            }
        }
    }

    /**
     * 删除卡池
     */
    fun deleteGachaByGachaId(gachaId: String) {
        viewModelScope.launch {
            try {
                mockGachaRepository.deleteGachaByGachaId(gachaId)
                mockGachaRepository.deleteGachaResultByGachaId(gachaId)
                //刷新记录
                getHistory()
            } catch (e: Exception) {
                LogReportUtil.upload(e, "deleteGachaByGachaId#gachaId:$gachaId")
            }
        }
    }

    /**
     * 切换id
     */
    fun changeGachaId(gachaId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    gachaId = gachaId
                )
            }
        }
    }


    /**
     * 切换显示结果
     */
    fun changeShowResult(showResult: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showResult = showResult
                )
            }
        }
    }

    /**
     * 切换类型结果
     */
    fun changeSelect(mockGachaType: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    mockGachaType = MockGachaType.getByValue(mockGachaType)
                )
            }
        }
    }

    /**
     * 弹窗状态更新
     */
    fun changeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDialog = openDialog
                )
            }
        }
    }

    /**
     * 更新选中列表
     */
    fun updatePickUpList(data: GachaUnitInfo) {
        val pickUpList = _uiState.value.pickUpList
        val gachaType = _uiState.value.mockGachaType
        val maxPick =
            if (gachaType == MockGachaType.FES) MOCK_GACHA_FES_MAX_UP_COUNT else MOCK_GACHA_MAX_UP_COUNT

        val newList = arrayListOf<GachaUnitInfo>()
        newList.addAll(pickUpList)
        if (newList.contains(data)) {
            newList.remove(data)
        } else {
            if (pickUpList.size >= maxPick) {
                ToastUtil.short(getString(R.string.gacha_max_select_count, maxPick))
                return
            } else {
                newList.add(data)
            }
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pickUpList = newList
                )
            }
        }
    }

    /**
     * 更新选中列表
     */
    fun updatePickUpList(pickUpList: List<GachaUnitInfo>) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pickUpList = pickUpList
                )
            }
        }
    }
}

package cn.wthee.pcrtool.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecordData
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.repository.MockGachaRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.db.view.MockGachaProData
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.data.model.getIdsStr
import cn.wthee.pcrtool.data.model.getRaritysStr
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.simpleDateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

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

    val historyList = MutableLiveData<List<MockGachaProData>>()
    val resultRecordList = MutableLiveData<List<MockGachaResultRecordData>>()

    /**
     * 获取卡池角色
     */
    fun getGachaUnits() = flow {
        try {
            val fesUnitInfo = gachaRepository.getFesUnitIds()
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
            emit(units)
        } catch (e: Exception) {
            Log.e("DEBUG", e.message ?: "")
        }
    }

    /**
     * 创建卡池
     */
    fun createMockGacha(gachaId: String, gachaType: Int, pickUpList: List<GachaUnitInfo>) {
        viewModelScope.launch {
            val nowTime = getToday()
            val data = MockGachaData(
                gachaId,
                MainActivity.regionType,
                gachaType,
                pickUpList.getIdsStr(),
                nowTime,
                nowTime
            )
            mockGachaRepository.insertGacha(data)
            //加载历史数据
            getHistory()
        }
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
    fun getHistory() {
        viewModelScope.launch {
            val data = mockGachaRepository.getHistory(MainActivity.regionType)
            historyList.postValue(data)
        }
    }

    /**
     * 获取卡池
     */
    suspend fun getGacha(gachaId: String) = mockGachaRepository.getGachaByGachaId(gachaId)

    /**
     * 获取卡池
     */
    suspend fun getGachaByPickUp(pickUpList: List<GachaUnitInfo>) =
        mockGachaRepository.getGachaByPickUpIds(MainActivity.regionType, pickUpList.getIdsStr())

    /**
     * 获取抽取结果
     */
    fun getResult(gachaId: String) {
        viewModelScope.launch {
            val data = mockGachaRepository.getResultByGachaId(gachaId)
            resultRecordList.postValue(data)
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
            } catch (_: Exception) {

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
            } catch (_: Exception) {

            }
        }
    }
}

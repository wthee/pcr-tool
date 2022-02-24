package cn.wthee.pcrtool.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecord
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.repository.MockGachaRepository
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.data.model.getIdsStr
import cn.wthee.pcrtool.data.model.getRaritysStr
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.utils.getToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * 模拟卡池 ViewModel
 *
 * @param gachaRepository
 * @param mockGachaRepository
 */
@HiltViewModel
class MockGachaViewModel @Inject constructor(
    private val gachaRepository: GachaRepository,
    private val mockGachaRepository: MockGachaRepository
) : ViewModel() {

    val historyList = MutableLiveData<List<MockGachaData>>()
    val resultRecordList = MutableLiveData<List<MockGachaResultRecord>>()

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
                gachaRepository.getGachaUnits(1),
                gachaRepository.getGachaUnits(2),
                gachaRepository.getGachaUnits(3),
                gachaRepository.getGachaUnits(4).filter {
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
                getRegion(),
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
            mockGachaRepository.insertResult(
                MockGachaResultRecord(
                    UUID.randomUUID().toString(),
                    gachaId,
                    resultList.getIdsStr(),
                    resultList.getRaritysStr(),
                    getToday(true)
                )
            )
            getResult(gachaId = gachaId)
        }
    }


    /**
     * 获取历史记录
     */
    fun getHistory() {
        viewModelScope.launch {
            val region = getRegion()
            val data = mockGachaRepository.getHistory(region)
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
        mockGachaRepository.getGachaByPickUpIds(getRegion(), pickUpList.getIdsStr())


    /**
     * 获取抽取结果
     */
    fun getResult(gachaId: String) {
        viewModelScope.launch {
            val data = mockGachaRepository.getResultByGachaId(gachaId)
            resultRecordList.postValue(data)
        }

    }
}

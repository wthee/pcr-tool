package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.MockGachaDao
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecordData
import javax.inject.Inject

/**
 * 模拟抽卡 Repository
 *
 * @param mockGachaDao
 */
class MockGachaRepository @Inject constructor(private val mockGachaDao: MockGachaDao) {


    suspend fun insertGacha(data: MockGachaData) = mockGachaDao.insertGacha(data)

    suspend fun updateGacha(gachaId: String, updateTime: String) {
        val data = mockGachaDao.getGachaByGachaId(gachaId)
        data.lastUpdateTime = updateTime
        mockGachaDao.updateGacha(data)
    }

    suspend fun insertResult(data: MockGachaResultRecordData) = mockGachaDao.insertResult(data)

    suspend fun getHistory(region: Int) = mockGachaDao.getHistory(region)

    suspend fun getGachaByGachaId(gachaId: String) = mockGachaDao.getGachaByGachaId(gachaId)

    suspend fun getGachaByPickUpIds(region: Int, pickUpIds: String) =
        mockGachaDao.getGachaByPickUpIds(region, pickUpIds)

    suspend fun getResultByGachaId(gachaId: String) = mockGachaDao.getResultByGachaId(gachaId)

    suspend fun deleteGachaResultByGachaId(gachaId: String) =
        mockGachaDao.deleteGachaResultByGachaId(gachaId)

    suspend fun deleteGachaByGachaId(gachaId: String) = mockGachaDao.deleteGachaByGachaId(gachaId)

}
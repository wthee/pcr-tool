package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.MockGachaDao
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecord
import javax.inject.Inject

/**
 * 模拟抽卡 Repository
 *
 * @param mockGachaDao
 */
class MockGachaRepository @Inject constructor(private val mockGachaDao: MockGachaDao) {


    suspend fun insertGacha(data: MockGachaData) = mockGachaDao.insertGacha(data)

    suspend fun insertResult(data: MockGachaResultRecord) = mockGachaDao.insertResult(data)

    suspend fun getHistory(region: Int) = mockGachaDao.getHistory(region)

    suspend fun getGachaByGachaId(gachaId: String) = mockGachaDao.getGachaByGachaId(gachaId)

    suspend fun getGachaByPickUpIds(region: Int, pickUpIds: String) =
        mockGachaDao.getGachaByPickUpIds(region, pickUpIds)

    suspend fun getResultByGachaId(gachaId: String) = mockGachaDao.getResultByGachaId(gachaId)


}
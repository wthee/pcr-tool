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

    suspend fun getHistory() = mockGachaDao.getHistory()
}
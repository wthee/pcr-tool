package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.MockGachaDao
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecordData
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * 模拟抽卡 Repository
 *
 * @param mockGachaDao
 */
class MockGachaRepository @Inject constructor(private val mockGachaDao: MockGachaDao) {


    suspend fun insertGacha(data: MockGachaData) = try {
        mockGachaDao.insertGacha(data)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "updateGacha#data:$data")
    }

    suspend fun updateGacha(gachaId: String, updateTime: String) = try {
        val data = mockGachaDao.getGachaByGachaId(gachaId)
        data.lastUpdateTime = updateTime
        mockGachaDao.updateGacha(data)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "updateGacha#gachaId:$gachaId,updateTime:$updateTime")
    }


    suspend fun insertResult(data: MockGachaResultRecordData) = try {
        mockGachaDao.insertResult(data)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "insertResult#data:$data")
    }

    suspend fun getHistory(region: Int) = try {
        mockGachaDao.getHistory(region)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getHistory#region:$region")
        emptyList()
    }

    suspend fun getGachaByPickUpIds(
        region: Int,
        gachaType: Int,
        pickUpIds: String
    ) = try {
        mockGachaDao.getGachaByPickUpIds(
            region = region,
            gachaType = gachaType,
            pickUpIds = pickUpIds
        )
    } catch (e: Exception) {
        LogReportUtil.upload(
            e,
            "getGachaByPickUpIds#region:$region,gachaType:$gachaType,pickUpIds:$gachaType"
        )
        null
    }


    suspend fun getResultByGachaId(gachaId: String) = try {
        mockGachaDao.getResultByGachaId(gachaId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getResultByGachaId#gachaId:$gachaId")
        emptyList()
    }

    suspend fun deleteGachaResultByGachaId(gachaId: String) = try {
        mockGachaDao.deleteGachaResultByGachaId(gachaId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "deleteGachaResultByGachaId#gachaId:$gachaId")
    }

    suspend fun deleteGachaByGachaId(gachaId: String) = try {
        mockGachaDao.deleteGachaByGachaId(gachaId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "deleteGachaByGachaId#gachaId:$gachaId")
    }

}
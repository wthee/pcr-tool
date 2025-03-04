package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.GachaDao
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * 卡池 Repository
 *
 * @param gachaDao
 */
class GachaRepository @Inject constructor(private val gachaDao: GachaDao) {

    suspend fun getGachaHistory(limit: Int) = try {
        val historyInfoList = gachaDao.getGachaHistory(limit)
        val resultList = arrayListOf<GachaInfo>()
        historyInfoList.forEach { gacha ->
            resultList.add(gacha.covertData())
        }
        resultList
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getGachaHistory#limit:$limit")
        emptyList()
    }

    suspend fun getFesUnitIdList() = try {
        gachaDao.getFesUnitIdList()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getGachaFesUnitList")
        null
    }
}
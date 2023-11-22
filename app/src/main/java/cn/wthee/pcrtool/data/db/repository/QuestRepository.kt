package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.QuestDao
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * 主线地图 Repository
 *
 * @param questDao
 */
class QuestRepository @Inject constructor(private val questDao: QuestDao) {

    suspend fun getEquipDropQuestList(equipId: Int) = try {
        var query = ""
        if (equipId != 0) {
            query = equipId.toString()
        }
        questDao.getEquipDropQuestList(query)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipDropQuestList#equipId:$equipId")
        emptyList()
    }

}
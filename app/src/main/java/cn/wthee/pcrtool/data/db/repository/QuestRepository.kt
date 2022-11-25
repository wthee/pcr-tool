package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.QuestDao
import cn.wthee.pcrtool.data.db.view.QuestDetail
import javax.inject.Inject

/**
 * 主线地图 Repository
 *
 * @param questDao
 */
class QuestRepository @Inject constructor(private val questDao: QuestDao) {

    suspend fun getEquipDropQuestList(equipId: Int): List<QuestDetail> {
        var query = ""
        if(equipId != 0){
            query = equipId.toString()
        }
        return questDao.getEquipDropQuestList(query)
    }

}
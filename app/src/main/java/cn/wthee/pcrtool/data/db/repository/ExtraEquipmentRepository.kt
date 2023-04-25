package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ExtraEquipmentDao
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import javax.inject.Inject

/**
 * ex装备Repository
 *
 * @param equipmentDao
 */
class ExtraEquipmentRepository @Inject constructor(private val equipmentDao: ExtraEquipmentDao) {

    suspend fun getEquipmentData(equipId: Int) = equipmentDao.getEquipInfos(equipId)

    suspend fun getEquipments(filter: FilterExtraEquipment, limit: Int) = try {
        equipmentDao.getEquipments(
            filter.flag,
            filter.rarity,
            filter.name,
            when {
                //公会
                filter.category > 0 -> getEquipCategoryList()[filter.category - 1].category
                //全部
                else -> 0
            },
            if (filter.all) 1 else 0,
            filter.starIds,
            limit
        )
    } catch (_: Exception) {
        null
    }


    suspend fun getEquipColorNum() = try {
        equipmentDao.getEquipColorNum()
    } catch (_: Exception) {
        0
    }

    suspend fun getEquipCategoryList() = try {
        equipmentDao.getEquipCategoryList()
    } catch (_: Exception) {
        arrayListOf()
    }

    suspend fun getEquipUnitList(category: Int) = equipmentDao.getEquipUnitList(category)

    suspend fun getDropQuestList(equipId: Int) = equipmentDao.getDropQuestList(equipId)

    suspend fun getSubRewardList(questId: Int) = equipmentDao.getSubRewardList(questId)

    suspend fun getTravelAreaList() = try {
        val areaList = equipmentDao.getTravelAreaList()
        areaList.forEach {
            it.questList = equipmentDao.getTravelQuestList(it.travelAreaId)
        }
        areaList
    } catch (_: Exception) {
        null
    }

    suspend fun getTravelQuest(questId: Int) = equipmentDao.getTravelQuest(questId)

    suspend fun getCharacterExtraEquipList(unitId: Int) = try {
        equipmentDao.getCharacterExtraEquipList(unitId)
    } catch (_: Exception) {
        null
    }

    suspend fun getAllEquipSkillIdList() = equipmentDao.getAllEquipSkillIdList()
}

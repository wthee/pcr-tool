package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ExtraEquipmentDao
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * ex装备Repository
 *
 * @param equipmentDao
 */
class ExtraEquipmentRepository @Inject constructor(private val equipmentDao: ExtraEquipmentDao) {

    suspend fun getEquipmentData(equipId: Int) = try {
        equipmentDao.getEquipInfo(equipId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipmentData#equipId:$equipId")
        null
    }

    suspend fun getEquipmentList(filter: FilterExtraEquipment, limit: Int) = try {
        val filterList = equipmentDao.getEquipments(
            filter.flag,
            filter.rarity,
            filter.name,
            when {
                //公会
                filter.category > 0 -> getEquipCategoryList()[filter.category - 1].category
                //全部
                else -> 0
            },
            limit
        )
        if (filter.all) {
            filterList
        } else {
            //筛选收藏的
            val starIdList = FilterExtraEquipment.getStarIdList()
            filterList.filter {
                starIdList.contains(it.equipmentId)
            }
        }
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

    suspend fun getDropQuestList(equipId: Int) = try {
        equipmentDao.getDropQuestList(equipId)
    } catch (_: Exception) {
        null
    }

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

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
            flag = filter.flag,
            rarity = filter.rarity,
            name = filter.name,
            category = when {
                //类型
                filter.category > 0 -> getEquipCategoryList()[filter.category - 1].category
                //全部
                else -> 0
            },
            limit = limit
        )
        if (filter.all) {
            filterList
        } else {
            //筛选收藏的
            val favoriteIdList = FilterExtraEquipment.getFavoriteIdList()
            filterList.filter {
                favoriteIdList.contains(it.equipmentId)
            }
        }
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipmentList#filter:$filter")
        null
    }


    suspend fun getEquipColorNum() = try {
        equipmentDao.getEquipColorNum()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipColorNum")
        0
    }

    suspend fun getEquipCategoryList() = try {
        equipmentDao.getEquipCategoryList()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipCategoryList")
        arrayListOf()
    }

    suspend fun getEquipUnitList(category: Int) = try {
        equipmentDao.getEquipUnitList(category)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipUnitList")
        emptyList()
    }

    suspend fun getDropQuestList(equipId: Int) = try {
        equipmentDao.getDropQuestList(equipId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getDropQuestList")
        null
    }

    suspend fun getSubRewardList(questId: Int) = try {
        equipmentDao.getSubRewardList(questId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getSubRewardList#questId:$questId")
        emptyList()
    }

    suspend fun getTravelAreaList() = try {
        val areaList = equipmentDao.getTravelAreaList()
        areaList.forEach {
            it.questList = equipmentDao.getTravelQuestList(it.travelAreaId)
        }
        areaList
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getTravelAreaList")
        null
    }

    suspend fun getTravelQuest(questId: Int) = try {
        equipmentDao.getTravelQuest(questId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getTravelQuest#questId:$questId")
        null
    }
    suspend fun getCharacterExtraEquipList(unitId: Int) = try {
        equipmentDao.getCharacterExtraEquipList(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getCharacterExtraEquipList#unitId:$unitId")
        null
    }

}

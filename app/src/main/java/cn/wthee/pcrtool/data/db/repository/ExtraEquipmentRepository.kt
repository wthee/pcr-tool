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

    suspend fun getEquipments(filter: FilterExtraEquipment, limit: Int) =
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

    suspend fun getCount() = equipmentDao.getCount()

    suspend fun getEquipColorNum() = equipmentDao.getEquipColorNum()

    suspend fun getEquipCategoryList() = equipmentDao.getEquipCategoryList()

    suspend fun getEquipUnitList(category: Int) = equipmentDao.getEquipUnitList(category)

}
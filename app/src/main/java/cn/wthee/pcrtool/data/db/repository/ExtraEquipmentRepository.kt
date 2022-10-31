package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ExtraEquipmentDao
import cn.wthee.pcrtool.data.model.FilterEquipment
import javax.inject.Inject

/**
 * ex装备Repository
 *
 * @param equipmentDao
 */
class ExtraEquipmentRepository @Inject constructor(private val equipmentDao: ExtraEquipmentDao) {

    suspend fun getEquipments(filter: FilterEquipment, limit: Int) =
        equipmentDao.getEquipments(
            filter.craft,
            filter.colorType,
            filter.name,
            if (filter.all) 1 else 0,
            filter.starIds,
            limit
        )

    suspend fun getCount() = equipmentDao.getCount()

    suspend fun getEquipColorNum() = equipmentDao.getEquipColorNum()

}
package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.bean.FilterEquipment
import cn.wthee.pcrtool.data.db.dao.EquipmentDao

/**
 * 装备Repository
 *
 * 数据来源 [EquipmentDao]
 */
class EquipmentRepository(private val equipmentDao: EquipmentDao) {

    suspend fun getEquipmentData(eid: Int) = equipmentDao.getEquipInfos(eid)

    suspend fun getEquipTypes() = equipmentDao.getEquipTypes()

    fun getPagingEquipments(name: String, filter: FilterEquipment) =
        equipmentDao.getPagingEquipments(
            filter.type, name,
            if (filter.all) 1 else 0,
            filter.starIds
        )

    suspend fun getEquipmentCount(name: String, filter: FilterEquipment) =
        equipmentDao.getEquipmentCount(
            filter.type, name,
            if (filter.all) 1 else 0,
            filter.starIds
        )

    suspend fun getEquipDropAreas(eid: Int) = equipmentDao.getEquipDropAreas(eid)

    suspend fun getEquipmentCraft(eid: Int) = equipmentDao.getEquipmentCraft(eid)

    suspend fun getUniqueEquipInfos(uid: Int, lv: Int) = equipmentDao.getUniqueEquipInfos(uid, lv)

    suspend fun getUniqueEquipMaxLv() = equipmentDao.getUniqueEquipMaxLv()

    companion object {

        @Volatile
        private var instance: EquipmentRepository? = null

        fun getInstance(equipmentDao: EquipmentDao) =
            instance ?: synchronized(this) {
                instance ?: EquipmentRepository(equipmentDao).also { instance = it }
            }
    }
}
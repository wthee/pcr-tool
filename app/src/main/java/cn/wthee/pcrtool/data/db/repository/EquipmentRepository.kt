package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.model.FilterEquipment

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
            filter.type,
            name,
            if (filter.all) 1 else 0,
            filter.starIds
        )

    suspend fun getEquipmentCount(name: String, filter: FilterEquipment) =
        equipmentDao.getEquipmentCount(filter.type, name, if (filter.all) 1 else 0, filter.starIds)

    suspend fun getEquipDropAreas(eid: Int) = equipmentDao.getEquipDropAreas(eid)

    suspend fun getEquipmentCraft(eid: Int) = equipmentDao.getEquipmentCraft(eid)

    suspend fun getUniqueEquipInfo(uid: Int, lv: Int) = equipmentDao.getUniqueEquipInfos(uid, lv)

    suspend fun getUniqueEquipMaxLv() = equipmentDao.getUniqueEquipMaxLv()

    suspend fun getEquipByRank(uid: Int, startRank: Int, endRank: Int) =
        equipmentDao.getEquipByRank(uid, startRank, endRank)

    companion object {

        fun getInstance(equipmentDao: EquipmentDao) = EquipmentRepository(equipmentDao)
    }
}
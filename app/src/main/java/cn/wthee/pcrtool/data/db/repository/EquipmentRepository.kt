package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.model.FilterEquipment
import javax.inject.Inject

/**
 * 装备Repository
 *
 * @param equipmentDao
 */
class EquipmentRepository @Inject constructor(private val equipmentDao: EquipmentDao) {

    suspend fun getEquipmentData(equipId: Int) = equipmentDao.getEquipInfos(equipId)

    suspend fun getEquipTypes() = equipmentDao.getEquipTypes()

    suspend fun getEquipments(filter: FilterEquipment, typeName: String) =
        equipmentDao.getEquipments(
            filter.craft,
            typeName,
            filter.name,
            if (filter.all) 1 else 0,
            filter.starIds
        )

    suspend fun getEquipments(limit: Int) = equipmentDao.getEquipments(limit)

    suspend fun getEquipDropAreas(equipId: Int) = equipmentDao.getEquipDropAreas(equipId)

    suspend fun getEquipmentCraft(equipId: Int) = equipmentDao.getEquipmentCraft(equipId)

    suspend fun getUniqueEquipInfo(unitId: Int, lv: Int) =
        equipmentDao.getUniqueEquipInfos(unitId, lv)

    suspend fun getUniqueEquipMaxLv() = equipmentDao.getUniqueEquipMaxLv()

    suspend fun getEquipByRank(unitId: Int, startRank: Int, endRank: Int) =
        equipmentDao.getEquipByRank(unitId, startRank, endRank)

    /**
     * 获取角色各 RANK 装备信息
     */
    suspend fun getAllRankEquip(unitId: Int) = equipmentDao.getAllRankEquip(unitId)
}
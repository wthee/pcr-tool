package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.utils.Constants
import javax.inject.Inject

/**
 * 装备Repository
 *
 * @param equipmentDao
 */
class EquipmentRepository @Inject constructor(private val equipmentDao: EquipmentDao) {

    suspend fun getEquipmentData(equipId: Int) = equipmentDao.getEquipInfos(equipId)

    suspend fun getEquipBasicInfo(equipId: Int) = equipmentDao.getEquipBasicInfo(equipId)

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

    suspend fun getEquipDropAreas(equipId: Int) = equipmentDao.getEquipDropAreas(equipId)

    suspend fun getEquipmentCraft(equipId: Int) = equipmentDao.getEquipmentCraft(equipId)

    suspend fun getUniqueEquipInfo(unitId: Int, lv: Int) = try {
        //TODO 校验逻辑是否正确
        if (lv > Constants.TP_LIMIT_LEVEL) {
            // <= 260 的总属性
            val attr0 = equipmentDao.getUniqueEquipInfosV2(unitId, Constants.TP_LIMIT_LEVEL, 0)
            // > 260 的部分
            val att1 = equipmentDao.getUniqueEquipInfosV2(unitId, lv - Constants.TP_LIMIT_LEVEL, 1)
            //计算总属性
            val sumAttr = att1?.let { attr0?.attr?.add(it.attr) }
            if (sumAttr != null) {
                attr0?.attr = sumAttr
            }
            attr0
        } else {
            equipmentDao.getUniqueEquipInfosV2(unitId, lv, 0)
        }
    } catch (e: Exception) {
        equipmentDao.getUniqueEquipInfos(unitId, lv)
    }

    suspend fun getUniqueEquipMaxLv() = equipmentDao.getUniqueEquipMaxLv()

    suspend fun getEquipByRank(unitId: Int, startRank: Int, endRank: Int) =
        equipmentDao.getEquipByRank(unitId, startRank, endRank)

    /**
     * 获取角色各 RANK 装备信息
     */
    suspend fun getAllRankEquip(unitId: Int) = equipmentDao.getAllRankEquip(unitId)

    suspend fun getMaxArea() = equipmentDao.getMaxArea()

    suspend fun getEquipColorNum() = equipmentDao.getEquipColorNum()

}
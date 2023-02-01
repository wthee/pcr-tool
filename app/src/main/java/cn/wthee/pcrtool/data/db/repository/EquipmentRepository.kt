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

    suspend fun getEquipmentCraft(equipId: Int) = equipmentDao.getEquipmentCraft(equipId)

    suspend fun getUniqueEquipInfo(unitId: Int, lv: Int) = try {
        //TODO 校验逻辑是否正确
        if (lv > Constants.TP_LIMIT_LEVEL) {
            // 获取专武奖励属性
            val bonusAttr = equipmentDao.getUniqueEquipBonus(unitId, lv - Constants.TP_LIMIT_LEVEL)
            val level = if (bonusAttr != null) {
                //不为空，说明是带tp相关属性的专武，仅计算260及之前等级提升的属性
                Constants.TP_LIMIT_LEVEL
            } else {
                //正常计算等级提升属性
                lv
            }
            val equipmentMaxData = equipmentDao.getUniqueEquipInfosV2(unitId, level)
            // 奖励属性不为空，计算总属性：初始属性 + 奖励属性
            if (bonusAttr != null && equipmentMaxData != null) {
                equipmentMaxData.attr = equipmentMaxData.attr.add(bonusAttr)
            }

            equipmentMaxData
        } else {
            equipmentDao.getUniqueEquipInfosV2(unitId, lv)
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

    suspend fun getEquipColorNum() = try {
        equipmentDao.getEquipColorNum()
    } catch (_: Exception) {
        0
    }

}
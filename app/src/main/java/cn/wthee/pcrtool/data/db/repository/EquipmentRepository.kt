package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity
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

    suspend fun getUniqueEquipCount() = try {
        equipmentDao.getUniqueEquipCountV2()
    } catch (_: Exception) {
        equipmentDao.getUniqueEquipCount()
    }

    suspend fun getEquipmentCraft(equipId: Int) = equipmentDao.getEquipmentCraft(equipId)

    suspend fun getUniqueEquipInfo(unitId: Int, lv: Int) = if (lv > Constants.TP_LIMIT_LEVEL) {
        // 获取专武奖励属性
        val bonusAttr = getUniqueEquipBonus(unitId, lv - Constants.TP_LIMIT_LEVEL)
        val level = if (bonusAttr != null) {
            //不为空，说明是带tp相关属性的专武，仅计算260及之前等级提升的属性
            Constants.TP_LIMIT_LEVEL
        } else {
            //正常计算等级提升属性
            lv
        }
        val equipmentMaxData = getUniqueEquip(unitId, level)
        // 奖励属性不为空，计算总属性：初始属性 + 奖励属性
        if (bonusAttr != null && equipmentMaxData != null) {
            equipmentMaxData.isTpLimitAction = true
            equipmentMaxData.attr = equipmentMaxData.attr.add(bonusAttr)
        }
        equipmentMaxData
    } else {
        getUniqueEquip(unitId, lv)
    }

    /**
     * 查询两张专武关联表，适配不同游戏版本
     */
    private suspend fun getUniqueEquip(unitId: Int, lv: Int) = try {
        equipmentDao.getUniqueEquipInfosV2(unitId, lv)
    } catch (e: Exception) {
        equipmentDao.getUniqueEquipInfos(unitId, lv)
    }

    /**
     * 查询两张专武关联表，适配不同游戏版本
     */
    private suspend fun getUniqueEquipBonus(unitId: Int, lv: Int) = try {
        equipmentDao.getUniqueEquipBonusV2(unitId, lv)
    } catch (e: Exception) {
        equipmentDao.getUniqueEquipBonus(unitId, lv)
    }

    suspend fun getUniqueEquipMaxLv() = equipmentDao.getUniqueEquipMaxLv()

    suspend fun getEquipByRank(unitId: Int, startRank: Int, endRank: Int) =
        equipmentDao.getEquipByRank(unitId, startRank, endRank)

    suspend fun getAllRankEquip(unitId: Int) = equipmentDao.getAllRankEquip(unitId)

    suspend fun getMaxArea() = equipmentDao.getMaxArea()

    suspend fun getEquipUnitList(equipId: Int) = equipmentDao.getEquipUnitList(equipId)

    suspend fun getEquipColorNum() = try {
        equipmentDao.getEquipColorNum()
    } catch (_: Exception) {
        0
    }

    suspend fun getMaxRank() = try {
        equipmentDao.getMaxRank()
    } catch (_: Exception) {
        0
    }

    suspend fun getUniqueEquipList(name: String): List<UniqueEquipBasicData> {
        val data = (try {
            val data = equipmentDao.getUniqueEquipListV2(name)
            if (data.isEmpty()) {
                equipmentDao.getUniqueEquipList(name)
            }
            data
        } catch (_: Exception) {
            equipmentDao.getUniqueEquipList(name)
        }).reversed()

        //处理台服排序
        return if (MainActivity.regionType == RegionType.TW) {
            data.sortedBy {
                arrayListOf(
                    138011,
                    138021,
                    138041,
                    138061
                ).contains(it.equipId)
            }
        } else {
            data
        }
    }
}
package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * 装备Repository
 *
 * @param equipmentDao
 */
class EquipmentRepository @Inject constructor(private val equipmentDao: EquipmentDao) {

    suspend fun getEquipmentData(equipId: Int) = try {
        equipmentDao.getEquipInfo(equipId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipmentData#equipId:$equipId")
        null
    }

    suspend fun getEquipBasicInfo(equipId: Int) = equipmentDao.getEquipBasicInfo(equipId)

    suspend fun getEquipmentList(filter: FilterEquip, limit: Int) = try {
        equipmentDao.getEquipmentList(
            filter.craft,
            filter.colorType,
            filter.name,
            if (filter.all) 1 else 0,
            filter.starIds,
            limit
        )
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipmentList#filter:$filter")
        null
    }

    suspend fun getCount() = try {
        equipmentDao.getCount()
    }catch (_:Exception){
        0
    }

    suspend fun getUniqueEquipCount() = try {
        val uniqueEquipCount = equipmentDao.getUniqueEquipCountV2()
        if (uniqueEquipCount.size > 1) {
            "${uniqueEquipCount[0].count} · ${uniqueEquipCount[1].count}"
        } else {
            uniqueEquipCount[0].count.toString()
        }

    } catch (_: Exception) {
        try {
            val uniqueEquipCount = equipmentDao.getUniqueEquipCount()
            if (uniqueEquipCount.isNotEmpty()) {
                uniqueEquipCount[0].count.toString()
            } else {
                "0"
            }
        }catch (_:Exception){
            "0"
        }
    }

    suspend fun getEquipmentCraft(equipId: Int) = equipmentDao.getEquipmentCraft(equipId)

    suspend fun getUniqueEquipInfo(unitId: Int, lv: Int, lv2: Int) =
        if (lv > Constants.TP_LIMIT_LEVEL) {
            // 获取专武奖励属性
            val bonusAttr = getUniqueEquipBonus(unitId, lv - Constants.TP_LIMIT_LEVEL)
            val level = if (bonusAttr != null) {
                //不为空，说明是带tp相关属性的专武，仅计算260及之前等级提升的属性
                Constants.TP_LIMIT_LEVEL
            } else {
                //正常计算等级提升属性
                lv
            }
            val equipmentMaxData = getUniqueEquip(unitId, level, lv2)
            // 专武1奖励属性不为空，计算总属性：初始属性 + 奖励属性
            if (bonusAttr != null && equipmentMaxData.isNotEmpty() && equipmentMaxData[0].equipmentId % 10 == 1) {
                equipmentMaxData[0].isTpLimitAction = true
                equipmentMaxData[0].attr = equipmentMaxData[0].attr.add(bonusAttr)
            }
            equipmentMaxData
        } else {
            getUniqueEquip(unitId, lv, lv2)
        }

    /**
     * 查询两张专武关联表，适配不同游戏版本
     */
    private suspend fun getUniqueEquip(
        unitId: Int,
        lv: Int,
        lv2: Int
    ): List<UniqueEquipmentMaxData> {
        val list = arrayListOf<UniqueEquipmentMaxData>()
        try {
            equipmentDao.getUniqueEquipInfoV2(unitId, lv, 1)?.let {
                list.add(it)
            }
            equipmentDao.getUniqueEquipInfoV2(unitId, lv2 + 1, 2)?.let {
                list.add(it)
            }
        } catch (e: Exception) {
            equipmentDao.getUniqueEquipInfo(unitId, lv)?.let {
                list.add(it)
            }
        }
        return list
    }

    /**
     * 查询两张专武关联表，适配不同游戏版本
     */
    private suspend fun getUniqueEquipBonus(unitId: Int, lv: Int) = try {
        equipmentDao.getUniqueEquipBonusV2(unitId, lv)
    } catch (e: Exception) {
        equipmentDao.getUniqueEquipBonus(unitId, lv)
    }

    suspend fun getUniqueEquipMaxLv(slot: Int) = equipmentDao.getUniqueEquipMaxLv(slot)

    /**
     * 获取所有角色所需的装备统计
     */
    suspend fun getEquipByRank(unitId: Int, startRank: Int, endRank: Int) = try {
        val data = equipmentDao.getEquipByRank(unitId, startRank, endRank)
        //计算倍数
        val materials = arrayListOf<EquipmentMaterial>()
        data.forEach { equipCountData ->
            try {
                val equip = equipmentDao.getEquipBasicInfo(equipCountData.equipId)
                val material = getEquipCraft(equip)
                material.map {
                    it.count *= equipCountData.equipCount
                }
                materials.addAll(material)
            } catch (_: Exception) {
            }

        }
        //合并重复项
        val map = mutableMapOf<Int, EquipmentMaterial>()
        materials.forEach {
            var i = it.count
            val key = it.id
            if (map[key] != null) {
                i = map[key]!!.count + it.count
            }
            it.count = i
            map[key] = it
        }
        //转换为列表
        map.values.sortedByDescending {
            it.count
        }
    } catch (e: Exception) {
        LogReportUtil.upload(
            e,
            "getEquipByRank#unitId:$unitId,startRank:$startRank,endRank:$endRank"
        )
        null
    }

    suspend fun getRankEquipList(unitId: Int) = try {
        equipmentDao.getRankEquipList(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllRankEquipList#unitId:$unitId")
        null
    }

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

    suspend fun getUniqueEquipList(name: String, slot: Int) = try {
            val data = (try {
                val data = equipmentDao.getUniqueEquipListV2(name, slot)
                data
            } catch (_: Exception) {
                equipmentDao.getUniqueEquipList(name, slot)
            }).reversed()

            //处理台服排序
            if (MainActivity.regionType == RegionType.TW) {
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
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getUniqueEquipInfoList")
            null
        }


    /**
     * 获取装备制作材料信息
     *
     * @param equip 装备信息
     */
    suspend fun getEquipCraft(equip: EquipmentBasicInfo) = try {
        val materials = arrayListOf<EquipmentMaterial>()
        if (equip.craftFlg == 0) {
            materials.add(
                EquipmentMaterial(
                    equip.equipmentId,
                    equip.equipmentName,
                    1
                )
            )
        } else {
            getAllMaterial(materials, equip.equipmentId, equip.equipmentName, 1, 1)
        }
        materials
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipCraft#equip:${equip.equipmentId}")
        emptyList()
    }

    /**
     * 迭代获取合成材料
     *
     * @param materials 合成素材
     * @param equipmentId 装备编号
     * @param name 装备名称
     * @param count 所需数量
     * @param craftFlg 是否可合成 0：不可，1：可合成
     */
    private suspend fun getAllMaterial(
        materials: ArrayList<EquipmentMaterial>,
        equipmentId: Int,
        name: String,
        count: Int,
        craftFlg: Int
    ) {
        try {
            if (craftFlg == 1) {
                val material = equipmentDao.getEquipmentCraft(equipmentId).getAllMaterialId()
                material.forEach {
                    val data = equipmentDao.getEquipBasicInfo(it.id)
                    getAllMaterial(
                        materials,
                        data.equipmentId,
                        data.equipmentName,
                        it.count,
                        data.craftFlg
                    )
                }
            } else {
                val material =
                    EquipmentMaterial(
                        equipmentId,
                        name,
                        count
                    )
                var flag = -1
                materials.forEachIndexed { index, equipmentMaterial ->
                    if (equipmentMaterial.id == material.id) {
                        flag = index
                    }
                }
                if (flag == -1) {
                    materials.add(material)
                } else {
                    materials[flag].count += material.count
                }
            }
        } catch (_: Exception) {

        }
    }
}
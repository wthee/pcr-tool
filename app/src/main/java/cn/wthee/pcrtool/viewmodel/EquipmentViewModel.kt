package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.view.EquipmentDropInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaterial
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 装备 ViewModel
 *
 * @param equipmentRepository
 */
@HiltViewModel
class EquipmentViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    /**
     * 获取装备列表
     *
     * @param params 装备筛选
     */
    fun getEquips(params: FilterEquipment) = flow {
        val typeName =
            if (params.type > 0) equipmentRepository.getEquipTypes()[params.type - 1] else "全部"
        val data = equipmentRepository.getEquipments(params, typeName, Int.MAX_VALUE)
        emit(data)
    }

    /**
     * 获取装备信息
     *
     * @param equipId 装备编号
     */
    fun getEquip(equipId: Int) = flow {
        emit(equipmentRepository.getEquipmentData(equipId))
    }

    /**
     * 获取装备类型
     */
    fun getTypes() = flow {
        emit(equipmentRepository.getEquipTypes())
    }

    /**
     * 根据角色id [unitId] 获取对应 Rank 范围 所需的装备
     *
     * @param unitId 角色编号
     * @param startRank 当前rank
     * @param endRank 目标rank
     */
    fun getEquipByRank(unitId: Int, startRank: Int, endRank: Int) = flow {
        if (startRank > 0 && endRank > 0 && startRank <= endRank) {
            val data = equipmentRepository.getEquipByRank(unitId, startRank, endRank)
            val materials = arrayListOf<EquipmentMaterial>()
            data.getAllEquipId().forEach { map ->
                val equip = equipmentRepository.getEquipmentData(map.key)
                val material = getEquipCraft(equip)
                material.map {
                    it.count *= map.value
                }
                materials.addAll(material)
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
            emit(map.values.sortedByDescending {
                it.count
            })
        }
    }

    /**
     * 获取装备制作材料信息
     *
     * @param equip 装备信息
     */
    fun getEquipInfos(equip: EquipmentMaxData) = flow {
        emit(getEquipCraft(equip))
    }

    /**
     * 获取角色 [unitId] 所有 RANK 装备列表
     *
     * @param unitId 角色编号
     */
    fun getAllRankEquipList(unitId: Int) = flow {
        emit(equipmentRepository.getAllRankEquip(unitId))
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
        if (craftFlg == 1) {
            val material = equipmentRepository.getEquipmentCraft(equipmentId).getAllMaterialId()
            material.forEach {
                val data = equipmentRepository.getEquipmentData(it.id)
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
    }

    /**
     * 获取装备制作材料信息
     *
     * @param equip 装备信息
     */
    private suspend fun getEquipCraft(equip: EquipmentMaxData): ArrayList<EquipmentMaterial> {
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
        return materials
    }

    /**
     * 获取装备掉落关卡信息
     *
     * @param equipId 装备编号
     */
    fun getDropInfos(equipId: Int) = flow {
        if (equipId != UNKNOWN_EQUIP_ID) {
            val equip = equipmentRepository.getEquipmentData(equipId)
            val fixedId = if (equip.craftFlg == 1) {
                equipmentRepository.getEquipmentCraft(equipId).cid1
            } else
                equipId
            //获取装备掉落信息
            val infos =
                equipmentRepository.getEquipDropAreas(fixedId).sortedWith(getSort(equipId))
            emit(infos)
        }
    }

    /**
     * 根据掉率排序
     *
     * @param equipId 装备编号
     */
    private fun getSort(equipId: Int): java.util.Comparator<EquipmentDropInfo> {
        val str = equipId.toString()
        return Comparator { o1: EquipmentDropInfo, o2: EquipmentDropInfo ->
            val a = o1.getOddOfEquip(str)
            val b = o2.getOddOfEquip(str)
            b.compareTo(a)
        }
    }

}

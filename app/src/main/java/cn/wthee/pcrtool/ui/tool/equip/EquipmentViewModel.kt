package cn.wthee.pcrtool.ui.tool.equip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.view.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 装备 ViewModel
 *
 * 数据来源 [EquipmentRepository]
 */
class EquipmentViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    lateinit var equipments: Flow<PagingData<EquipmentMaxData>>
    var updateEquip = MutableLiveData<Boolean>()
    var reset = MutableLiveData<Boolean>()
    var equipmentCounts = MutableLiveData<Int>()
    var uniqueEquip = MutableLiveData<UniqueEquipmentMaxData?>()
    var equipMaterialInfos = MutableLiveData<List<EquipmentMaterial>>()
    var rankEquipMaterials = MutableLiveData<List<EquipmentMaterial>>()

    /**
     * 获取装备列表
     */
    fun getEquips(params: FilterEquipment, name: String, reload: Boolean = true) {
        viewModelScope.launch {
            if (!this@EquipmentViewModel::equipments.isInitialized || reload) {
                equipments = Pager(
                    PagingConfig(
                        pageSize = 10,
                        enablePlaceholders = false
                    )
                ) {
                    equipmentRepository.getPagingEquipments(
                        name,
                        params
                    )
                }.flow.cachedIn(viewModelScope)
            }
            equipmentCounts.postValue(
                equipmentRepository.getEquipmentCount(
                    name,
                    params
                )
            )
            updateEquip.postValue(true)
        }
    }


    /**
     * 根据 [uid] 专武等级 [lv]，获取专武信息
     */
    fun getUniqueEquipInfos(uid: Int, lv: Int) {
        viewModelScope.launch {
            uniqueEquip.postValue(equipmentRepository.getUniqueEquipInfo(uid, lv))
        }
    }

    /**
     * 获取装备类型
     */
    suspend fun getTypes() = equipmentRepository.getEquipTypes()

    /**
     * 根据角色id [uid] 获取对应 Rank 范围 所需的装备
     */
    fun getEquipByRank(uid: Int, startRank: Int, endRank: Int) {
        viewModelScope.launch {
            val data = equipmentRepository.getEquipByRank(uid, startRank, endRank)
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
            rankEquipMaterials.postValue(map.values.sortedByDescending {
                it.count
            })
        }
    }

    /**
     * 获取装备制作材料信息
     */
    fun getEquipInfos(equip: EquipmentMaxData) {
        viewModelScope.launch {
            equipMaterialInfos.postValue(getEquipCraft(equip))
        }
    }

    /**
     * 获取合成材料
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
     * 根据 [equipmentId]，获取装备掉落关卡信息
     */
    suspend fun getDropInfos(equipmentId: Int): List<EquipmentDropInfo> {
        val equip = equipmentRepository.getEquipmentData(equipmentId)
        val fixedId = if (equip.craftFlg == 1) {
            equipmentRepository.getEquipmentCraft(equipmentId).cid1
        } else
            equipmentId
        //获取装备掉落信息
        val infos = equipmentRepository.getEquipDropAreas(fixedId)
        return infos.sortedWith(getSort(equipmentId))
    }

    /**
     * 根据掉率排序
     */
    private fun getSort(eid: Int): java.util.Comparator<EquipmentDropInfo> {
        val str = eid.toString()
        return Comparator { o1: EquipmentDropInfo, o2: EquipmentDropInfo ->
            val a = o1.getOddOfEquip(str)
            val b = o2.getOddOfEquip(str)
            b.compareTo(a)
        }
    }

}

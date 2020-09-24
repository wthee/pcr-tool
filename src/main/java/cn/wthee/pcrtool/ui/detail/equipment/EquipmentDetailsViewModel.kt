package cn.wthee.pcrtool.ui.detail.equipment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.database.entity.EquipmentDropInfo
import cn.wthee.pcrtool.database.entity.EquipmentMaterial
import cn.wthee.pcrtool.database.entity.EquipmentMaxData
import kotlinx.coroutines.launch


class EquipmentDetailsViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    private var materials = arrayListOf<EquipmentMaterial>()
    var equipMaterialInfos = MutableLiveData<List<EquipmentMaterial>>()
    var isLoading = MutableLiveData<Boolean>()

    //获取装备制作材料信息
    fun getEquipInfos(equip: EquipmentMaxData) {
        isLoading.postValue(true)
        viewModelScope.launch {
            if (equip.craftFlg == 0) {
                materials.add(
                    EquipmentMaterial(
                        equip.equipmentId,
                        equip.equipmentName,
                        1
                    )
                )
            } else {
                getAllMaterial(equip.equipmentId, equip.equipmentName, 1, 1)
            }
            equipMaterialInfos.postValue(materials)
            isLoading.postValue(false)
        }
    }

    private suspend fun getAllMaterial(equipmentId: Int, name: String, count: Int, craftFlg: Int) {
        if (craftFlg == 1) {
            val material = equipmentRepository.getEquipmentCraft(equipmentId).getAllMaterialId()
            material.forEach {
                val data = equipmentRepository.getEquipmentData(it.id)
                getAllMaterial(data.equipmentId, data.equipmentName, it.count, data.craftFlg)
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

    //获取装备掉落关卡信息
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

    private fun getSort(eid: Int): java.util.Comparator<EquipmentDropInfo> {
        val str = eid.toString()
        return Comparator { o1: EquipmentDropInfo, o2: EquipmentDropInfo ->
            val a = o1.getOddOfEquip(str)
            val b = o2.getOddOfEquip(str)
            b.compareTo(a)
        }
    }
}

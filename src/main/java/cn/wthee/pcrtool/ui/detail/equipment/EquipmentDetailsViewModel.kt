package cn.wthee.pcrtool.ui.detail.equipment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.data.model.EquipmentDropInfo
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentDetailsViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipMaterialInfos = MutableLiveData<List<EquipmentMaterial>>()
    var materials = arrayListOf<EquipmentMaterial>()
    var isLoading = MutableLiveData<Boolean>()

    //获取装备制作材料信息
    fun getEquipInfos(equip: EquipmentData) {
        isLoading.postValue(true)
        viewModelScope.launch {
            if (equip.craftFlg == 0) {
                materials.add(EquipmentMaterial(0, "", 1, getDropInfos(equip.equipmentId, 0)))
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
                EquipmentMaterial(equipmentId, name, count, getDropInfos(equipmentId, craftFlg))
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
    private suspend fun getDropInfos(
        equipmentId: Int,
        craftFlg: Int
    ): ArrayList<EquipmentDropInfo> {
        val finalData = arrayListOf<EquipmentDropInfo>()
        val fixedId = if (craftFlg == 1) {
            equipmentRepository.getEquipmentCraft(equipmentId).cid1
        } else
            equipmentId
        val infos = equipmentRepository.getDropWaveID(fixedId)

        infos.forEach { info ->
            val each3Wave = arrayListOf<Int>()
            val odds = arrayListOf<EquipmentIdWithOdd>()
            equipmentRepository.getDropRewardID(info.getWaveIds()).forEach {
                each3Wave.addAll(it.getRewardIds())
            }
            equipmentRepository.getOdds(each3Wave).forEach {
                odds.addAll(it.getOdds())
            }
            odds.sortBy { it.odd }
            finalData.add(EquipmentDropInfo(info.questId, fixedId, info.questName, odds))
        }
        finalData.sortWith(getSort(fixedId))
        return finalData
    }

    private fun getSort(eid: Int): java.util.Comparator<EquipmentDropInfo> {
        return Comparator { o1: EquipmentDropInfo, o2: EquipmentDropInfo ->
            val a = o1.odds.first { it.eid == eid }.odd
            val b = o2.odds.first { it.eid == eid }.odd
            b.compareTo(a)
        }
    }
}

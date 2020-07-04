package cn.wthee.pcrtool.ui.detail.equipment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.data.model.EquipmentDropInfo
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdd
import kotlinx.coroutines.launch


class EquipmentDetailsViewModel internal constructor(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipDropInfos = MutableLiveData<List<EquipmentDropInfo>>()

    //获取装备掉落关卡信息
    fun getDropInfos(equip: EquipmentData) {
        viewModelScope.launch {
            val finalData = arrayListOf<EquipmentDropInfo>()
            val fixedId = if (equip.craftFlg == 1) {
                equipmentRepository.getEquipmentCraft(equip.equipmentId).condition_equipment_id_1
            } else
                equip.equipmentId
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
            equipDropInfos.postValue(finalData)
        }
    }

    private fun getSort(eid: Int): java.util.Comparator<EquipmentDropInfo> {
        return Comparator { o1: EquipmentDropInfo, o2: EquipmentDropInfo ->
            val a = o1.odds.first { it.eid == eid }.odd
            val b = o2.odds.first { it.eid == eid }.odd
            b.compareTo(a)
        }
    }
}

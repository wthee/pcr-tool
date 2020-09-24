package cn.wthee.pcrtool.ui.detail.character

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.database.entity.Attr
import cn.wthee.pcrtool.database.entity.add
import cn.wthee.pcrtool.database.entity.multiply
import cn.wthee.pcrtool.database.entity.EquipmentMaxData
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import kotlinx.coroutines.launch


class CharacterPromotionViewModel(
    private val characterRepository: CharacterRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentMaxData>>()
    var sumInfo = MutableLiveData<Attr>()
    var maxData = MutableLiveData<List<Int>>()

    //获取角色属性信息
    fun getCharacterInfo(unitId: Int, rank: Int, rarity: Int, lv: Int) {
        //计算属性
        viewModelScope.launch {
            val rankData = characterRepository.getRankStutas(unitId, rank)
            val rarityData = characterRepository.getRarity(unitId, rarity)
            val ids = characterRepository.getEquipmentIds(unitId, rank).getAllIds()
            //计算指定rank星级下的角色属性
            val info = rankData.attr
                .add(rarityData.attr)
                .add(Attr.setGrowthValue(rarityData).multiply(lv + rank))
            val eqs = arrayListOf<EquipmentMaxData>()
            ids.forEach {
                if (it == UNKNOW_EQUIP_ID)
                    eqs.add(EquipmentMaxData.unknow())
                else
                    eqs.add(equipmentRepository.getEquipmentData(it))
            }
            //rank装备信息
            equipments.postValue(eqs)
            //计算穿戴装备后属性
            eqs.forEach { eq ->
                if (eq.equipmentId == UNKNOW_EQUIP_ID) return@forEach
                info.add(eq.attr)
            }
            //专武
            val uniqueEquip = equipmentRepository.getUniqueEquipInfos(unitId)
            if (uniqueEquip != null) {
                info.add(uniqueEquip.attr)
            }
            sumInfo.postValue(info)
        }

    }

    //获取最大Rank和星级
    fun getMaxRankAndRarity(id: Int) {
        viewModelScope.launch {
            val rank = characterRepository.getMaxRank(id)
            val rarity = characterRepository.getMaxRarity(id)
            val level = characterRepository.getMaxLevel()
            maxData.postValue(listOf(rank, rarity, level))
        }
    }

}

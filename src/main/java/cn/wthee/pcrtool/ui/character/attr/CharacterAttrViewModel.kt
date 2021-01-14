package cn.wthee.pcrtool.ui.character.attr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.CharacterRepository
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.view.*
import cn.wthee.pcrtool.utils.Constants.UNKNOWN_EQUIP_ID
import kotlinx.coroutines.launch


class CharacterAttrViewModel(
    private val characterRepository: CharacterRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentMaxData>>()
    var storyAttrs = MutableLiveData<Attr>()
    var sumInfo = MutableLiveData<Attr>()
    var maxData = MutableLiveData<Map<String, Int>>()

    //获取角色属性信息
    fun getCharacterInfo(unitId: Int, rank: Int, rarity: Int, lv: Int, ueLv: Int) {
        //计算属性
        viewModelScope.launch {
            val attr = getAttrs(unitId, rank, rarity, lv, ueLv)
            sumInfo.postValue(attr)
        }

    }

    //获取角色属性信息
    suspend fun getAttrs(unitId: Int, rank: Int, rarity: Int, lv: Int, ueLv: Int): Attr {
        try {
            val rankData = characterRepository.getRankStatus(unitId, rank)
            val rarityData = characterRepository.getRarity(unitId, rarity)
            val ids = characterRepository.getEquipmentIds(unitId, rank).getAllIds()
            //计算指定rank星级下的角色属性
            val info = rankData.attr
                .add(rarityData.attr)
                .add(Attr.setGrowthValue(rarityData).multiply(lv + rank))
            val eqs = arrayListOf<EquipmentMaxData>()
            ids.forEach {
                if (it == UNKNOWN_EQUIP_ID)
                    eqs.add(EquipmentMaxData.unknown())
                else
                    eqs.add(equipmentRepository.getEquipmentData(it))
            }
            //rank装备信息
            equipments.postValue(eqs)
            //计算穿戴装备后属性
            eqs.forEach { eq ->
                if (eq.equipmentId == UNKNOWN_EQUIP_ID) return@forEach
                info.add(eq.attr)
            }
            //专武
            val uniqueEquip = equipmentRepository.getUniqueEquipInfos(unitId, ueLv)
            if (uniqueEquip != null) {
                info.add(uniqueEquip.attr)
            }
            //故事剧情
            val storyAttr = getStoryAttrs(unitId)
            storyAttrs.postValue(storyAttr)
            info.add(storyAttr)
            return info
        } catch (e: Exception) {
        }
        return Attr()
    }

    private suspend fun getStoryAttrs(id: Int): Attr {
        val storyAttr = Attr()
        val storyInfo = characterRepository.getCharacterStoryStatus(id)
        storyInfo.forEach {
            storyAttr.add(it.getAttr())
        }
        return storyAttr
    }

    //获取最大Rank和星级
    fun getMaxRankAndRarity(id: Int) {
        viewModelScope.launch {
            try {
                val rank = characterRepository.getMaxRank(id)
                val rarity = characterRepository.getMaxRarity(id)
                val level = characterRepository.getMaxLevel()
                val ueLv = equipmentRepository.getUniqueEquipMaxLv()
                val map = HashMap<String, Int>()
                map["rank"] = rank
                map["rarity"] = rarity
                map["level"] = level
                map["ueLv"] = ueLv
                maxData.postValue(map)
            } catch (e: Exception) {

            }
        }
    }

    suspend fun isUnknown(id: Int): Boolean {
        try {
            characterRepository.getMaxRank(id)
            characterRepository.getMaxRarity(id)
            if (characterRepository.getEquipmentIds(id, 2).getAllIds().isEmpty()) {
                return true
            }
        } catch (e: Exception) {
            return true
        }
        return false
    }
}

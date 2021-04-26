package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.model.CharacterSelectInfo
import cn.wthee.pcrtool.data.view.*
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.utils.int
import com.umeng.umcrash.UMCrash
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 角色面板属性 ViewModel
 *
 * 数据来源 [UnitRepository] [EquipmentRepository]
 *
 */
@HiltViewModel
class CharacterAttrViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    val equipments = MutableLiveData<List<EquipmentMaxData>>()
    val storyAttrs = MutableLiveData<Attr>()
    val sumInfo = MutableLiveData<Attr>()
    val maxData = MutableLiveData<CharacterSelectInfo>()
    val selData = MutableLiveData<CharacterSelectInfo>()
    val level = MutableLiveData(0)
    val atk = MutableLiveData(0)
    val loading = MutableLiveData(true)

    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     */
    fun getCharacterInfo(unitId: Int, data: CharacterSelectInfo) {
        //计算属性
        viewModelScope.launch {
            val attr = getAttrs(unitId, data)
            level.postValue(data.level)
            atk.postValue(attr.atk.int)
            sumInfo.postValue(attr)
            loading.postValue(false)
        }

    }

    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     */
    suspend fun getAttrs(unitId: Int, data: CharacterSelectInfo): Attr {
        val info = Attr()
        try {
            val rank = data.rank
            val rarity = data.rarity
            val lv = data.level
            val ueLv = data.uniqueEquipLevel

            val rankData = unitRepository.getRankStatus(unitId, rank)
            val rarityData = unitRepository.getRarity(unitId, rarity)
            val ids = unitRepository.getEquipmentIds(unitId, rank).getAllIds()
            //计算指定rank星级下的角色属性
            info.add(rankData.attr)
                .add(rarityData.attr)
                .add(Attr.setGrowthValue(rarityData).multiply(lv + rank))
            val eqs = arrayListOf<EquipmentMaxData>()
            ids.forEach {
                if (it == UNKNOWN_EQUIP_ID || it == 0)
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
            val uniqueEquip = equipmentRepository.getUniqueEquipInfo(unitId, ueLv)
            if (uniqueEquip != null) {
                info.add(uniqueEquip.attr)
            }
            //故事剧情
            val storyAttr = getStoryAttrs(unitId)
            storyAttrs.postValue(storyAttr)
            info.add(storyAttr)

        } catch (e: Exception) {
            MainScope().launch {
                UMCrash.generateCustomLog(
                    e,
                    Constants.EXCEPTION_LOAD_ATTR +
                            "uid:$unitId," +
                            "rank:${data.rank}," +
                            "ratity:${data.rarity}" +
                            "lv:${data.level}" +
                            "ueLv:${data.uniqueEquipLevel}"
                )
            }
        }
        return info
    }

    /**
     *根据 [unitId]，获取角色剧情属性 [Attr]
     */
    private suspend fun getStoryAttrs(unitId: Int): Attr {
        val storyAttr = Attr()
        try {
            val storyInfo = unitRepository.getCharacterStoryStatus(unitId)
            storyInfo.forEach {
                storyAttr.add(it.getAttr())
            }
        } catch (e: Exception) {

        }
        return storyAttr
    }

    /**
     * 根据 [unitId]，获取最大Rank和星级
     */
    fun getMaxRankAndRarity(unitId: Int) {
        viewModelScope.launch {
            try {
                val rank = unitRepository.getMaxRank(unitId)
                val rarity = unitRepository.getMaxRarity(unitId)
                val level = unitRepository.getMaxLevel()
                val ueLv = equipmentRepository.getUniqueEquipMaxLv()
                maxData.postValue(CharacterSelectInfo(rank, rarity, level, ueLv))
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 根据 [unitId]，判断角色是否有技能等信息
     */
    suspend fun isUnknown(unitId: Int): Boolean {
        try {
            unitRepository.getMaxRank(unitId)
            unitRepository.getMaxRarity(unitId)
            if (unitRepository.getEquipmentIds(unitId, 2).getAllIds().isEmpty()) {
                return true
            }
        } catch (e: Exception) {
            return true
        }
        return false
    }
}

package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.*
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.RankCompareData
import cn.wthee.pcrtool.data.model.getRankCompareList
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UNKNOWN_EQUIP_ID
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

    val allAttr = MutableLiveData<AllAttrData>()
    val attrCompareData = MutableLiveData<List<RankCompareData>>()
    val isUnknown = MutableLiveData<Boolean>()
    val levelValue = MutableLiveData<Int>()
    val rankValue = MutableLiveData<Int>()
    val rarityValue = MutableLiveData<Int>()
    val uniqueEquipLevelValue = MutableLiveData<Int>()

    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     */
    fun getCharacterInfo(unitId: Int, level: Int, rank: Int, rarity: Int, uniqueEquipLevel: Int) {
        //计算属性
        viewModelScope.launch {
            val attr = getAttrs(unitId, level, rank, rarity, uniqueEquipLevel)
            allAttr.postValue(attr)
        }

    }

    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     */
    suspend fun getAttrs(
        unitId: Int,
        level: Int,
        rank: Int,
        rarity: Int,
        uniqueEquipLevel: Int
    ): AllAttrData {
        val info = Attr()
        val allData = AllAttrData()
        try {

            val rankData = unitRepository.getRankStatus(unitId, rank)
            val rarityData = unitRepository.getRarity(unitId, rarity)
            val ids = unitRepository.getEquipmentIds(unitId, rank).getAllOrderIds()
            //计算指定rank星级下的角色属性
            rankData?.let {
                info.add(rankData.attr)
            }
            info.add(rarityData.attr)
                .add(Attr.setGrowthValue(rarityData).multiply(level + rank))

            val eqs = arrayListOf<EquipmentMaxData>()
            ids.forEach {
                if (it == UNKNOWN_EQUIP_ID || it == 0)
                    eqs.add(EquipmentMaxData.unknown())
                else
                    eqs.add(equipmentRepository.getEquipmentData(it))
            }
            //rank装备信息
//            equipments.postValue(eqs)
            allData.equips = eqs
            //计算穿戴装备后属性
            eqs.forEach { eq ->
                if (eq.equipmentId == UNKNOWN_EQUIP_ID) return@forEach
                info.add(eq.attr)
            }
            //专武
            val uniqueEquip = equipmentRepository.getUniqueEquipInfo(unitId, uniqueEquipLevel)
            if (uniqueEquip != null) {
                info.add(uniqueEquip.attr)
                allData.uniqueEquip = uniqueEquip
            }
            //故事剧情
            val storyAttr = getStoryAttrs(unitId)
//            storyAttrs.postValue(storyAttr)
            info.add(storyAttr)
            allData.stroyAttr = storyAttr
            allData.sumAttr = info
            allAttr.postValue(allData)
        } catch (e: Exception) {
            MainScope().launch {
                UMCrash.generateCustomLog(
                    e,
                    Constants.EXCEPTION_LOAD_ATTR +
                            "uid:$unitId," +
                            "rank:${rank}," +
                            "ratity:${rarity}" +
                            "lv:${level}" +
                            "ueLv:${uniqueEquipLevel}"
                )
            }
        }
        return allData
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
     * 0: level
     * 1: rank
     * 3: rarity
     * 4: uniqueEquipLevel
     */
    suspend fun getMaxRankAndRarity(unitId: Int): ArrayList<Int> {
        try {
            val rank = unitRepository.getMaxRank(unitId)
            val rarity = unitRepository.getMaxRarity(unitId)
            val level = unitRepository.getMaxLevel()
            val ueLv = equipmentRepository.getUniqueEquipMaxLv()
            return arrayListOf(level, rank, rarity, ueLv)
        } catch (e: Exception) {

        }
        return arrayListOf()
    }

    /**
     * 根据 [unitId]，判断角色是否有技能等信息
     */
    fun isUnknown(unitId: Int) {
        viewModelScope.launch {
            val data = unitRepository.getRankStatus(unitId, 2)
            if (data == null) {
                isUnknown.postValue(true)
            } else {
                isUnknown.postValue(false)
            }
        }
    }

    /**
     * 获取指定角色属性
     */
    fun getUnitAttrCompare(
        unitId: Int,
        level: Int,
        rarity: Int,
        uniqueEquipLevel: Int,
        rank0: Int,
        rank1: Int
    ) {
        viewModelScope.launch {
            val attr0 = getAttrs(unitId, level, rank0, rarity, uniqueEquipLevel)
            val attr1 = getAttrs(unitId, level, rank1, rarity, uniqueEquipLevel)
            attrCompareData.postValue(getRankCompareList(attr0.sumAttr, attr1.sumAttr))
        }
    }
}

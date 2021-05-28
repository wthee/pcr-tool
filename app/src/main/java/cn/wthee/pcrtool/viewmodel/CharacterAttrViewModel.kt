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
 * @param unitRepository
 * @param equipmentRepository
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
     * @param unitId 角色编号
     * @param level 角色等级
     * @param rank 角色rank
     * @param rarity 角色星级
     * @param uniqueEquipLevel 角色专武等级
     */
    fun getCharacterInfo(unitId: Int, level: Int, rank: Int, rarity: Int, uniqueEquipLevel: Int) {
        //计算属性
        viewModelScope.launch {
            val attr = getAttrs(unitId, level, rank, rarity, uniqueEquipLevel)
            allAttr.postValue(attr)
        }

    }

    /**
     * 获取角色属性信息
     */
    private suspend fun getAttrs(
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
            allData.equips = eqs
            //计算穿戴装备后属性
            eqs.forEach { eq ->
                if (eq.equipmentId == UNKNOWN_EQUIP_ID) return@forEach
                info.add(eq.attr)
            }
            //专武
            val uniqueEquip = equipmentRepository.getUniqueEquipInfo(unitId, uniqueEquipLevel)
            if (uniqueEquip != null) {
                if (uniqueEquipLevel == 0) {
                    uniqueEquip.attr = Attr()
                }
                info.add(uniqueEquip.attr)
                allData.uniqueEquip = uniqueEquip
            }
            //故事剧情
            val storyAttr = getStoryAttrs(unitId)
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
                            "rarity:${rarity}" +
                            "lv:${level}" +
                            "ueLv:${uniqueEquipLevel}"
                )
            }
        }
        return allData
    }

    /**
     * 获取角色剧情属性
     *
     * @param unitId 角色编号
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
     * 获取最大Rank和星级
     * 0: level
     * 1: rank
     * 3: rarity
     * 4: uniqueEquipLevel
     *
     * @param unitId 角色编号
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
     * 判断角色是否有技能等信息
     *
     * @param unitId 角色编号
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
     * 获取指定角色属性对比
     *
     * @param rank0 当前rank
     * @param rank1 目标rank
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

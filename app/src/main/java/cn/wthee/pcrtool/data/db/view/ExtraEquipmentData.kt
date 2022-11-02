package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID

/**
 * ex装备最大强化后属性视图
 */
data class ExtraEquipmentData(
    @ColumnInfo(name = "equipment_id") var equipmentId: Int = UNKNOWN_EQUIP_ID,
    @ColumnInfo(name = "equipment_name") var equipmentName: String = "",
    @ColumnInfo(name = "category") var category: Int = 0,
    @ColumnInfo(name = "category_name") var categoryName: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "clan_battle_equip_flag") var clanFlag: Int = 0,
    @ColumnInfo(name = "rarity") var rarity: Int = 0,
    @ColumnInfo(name = "passive_skill_id_1") var passiveSkillId1: Int = 0,
    @ColumnInfo(name = "passive_skill_id_2") var passiveSkillId2: Int = 0,
    @ColumnInfo(name = "passive_skill_power") var passiveSkillPower: Int = 0,
    @Embedded var attrDefault: AttrDefaultInt = AttrDefaultInt(),
    @Embedded var attr: AttrInt = AttrInt(),
) {

    /**
     * 装备描述
     */
    fun getDesc() = description.replace("\\n", "")

    /**
     * 被动技能列表
     */
    fun getPassiveSkillIds() = arrayListOf(
        passiveSkillId1,
        passiveSkillId2
    ).filter {
        it != 0
    }

    companion object {
        fun unknown() =
            ExtraEquipmentData(
                UNKNOWN_EQUIP_ID,
                "?",
                0,
                "",
                "",
                0,
                0,
                0,
                0,
                0,
                AttrDefaultInt(),
                AttrInt()
            )

    }

    /**
     * 属性对比差值
     */
    private fun compareList(): List<AttrValue> {
        val attrs = this.attr.allNotZero()
        val attrs1 = this.attrDefault.allNotZero()
        val compareValue = arrayListOf<AttrValue>()
        attrs.forEachIndexed { index, attrValue ->
            compareValue.add(AttrValue(attrValue.title, attrValue.value - attrs1[index].value))
        }
        return compareValue
    }

    /**
     * 属性整合
     */
    fun fixAttrList(): List<AttrCompareData> {
        val dataList = arrayListOf<AttrCompareData>()
        val compared = compareList()
        this.attrDefault.allNotZero().forEachIndexed { index, attrValue ->
            dataList.add(
                AttrCompareData(
                    attrValue.title,
                    attrValue.value,
                    this.attr.allNotZero()[index].value,
                    compared[index].value
                )
            )
        }
        return dataList
    }
}


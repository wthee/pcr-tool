package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID

/**
 * ex装备属性
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
     * 属性整合
     */
    fun fixAttrList(isPreview: Boolean = false): List<AttrCompareData> {
        val dataList = arrayListOf<AttrCompareData>()
        this.attrDefault.allNotZero(isPreview).forEachIndexed { index, attrValue ->
            dataList.add(
                AttrCompareData(
                    attrValue.title,
                    attrValue.value,
                    this.attr.allNotZero(isPreview)[index].value,
                    0.0
                )
            )
        }
        return dataList
    }
}


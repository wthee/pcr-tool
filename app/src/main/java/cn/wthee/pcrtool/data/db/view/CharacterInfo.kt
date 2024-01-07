package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore


/**
 * 角色信息视图
 */
data class CharacterInfo(
    @ColumnInfo(name = "unit_id") var id: Int = -1,
    @ColumnInfo(name = "unit_name") var name: String = "",
    @ColumnInfo(name = "rarity") var rarity: Int = 3,
    @ColumnInfo(name = "kana") var kana: String = "",
    @ColumnInfo(name = "age_int") var age: String = "",
    @ColumnInfo(name = "guild") var guild: String = "",
    @ColumnInfo(name = "race") var race: String = "",
    @ColumnInfo(name = "height_int") var height: String = "",
    @ColumnInfo(name = "weight_int") var weight: String = "",
    @ColumnInfo(name = "birth_month_int") var birthMonth: String = "",
    @ColumnInfo(name = "birth_day_int") var birthDay: String = "",
    @ColumnInfo(name = "search_area_width") var position: Int = 0,
    @ColumnInfo(name = "atk_type") var atkType: Int = 1,
    @ColumnInfo(name = "unit_start_time") var startTime: String = "",
    @ColumnInfo(name = "r6Id") var r6Id: Int = 1,
    @ColumnInfo(name = "limit_type") var limitType: Int = 0,
    @Ignore var uniqueEquipType: Int = 0,
) {
    constructor() : this(-1, "", 3, "", "", "", "", "", "", "", "", 0, 1, "", 1, 0, 0)

    /**
     * 获取名字，去除限定类型
     */
    fun getNameF(): String {
        val index = this.name.indexOf("（")
        return if (index == -1) {
            this.name
        } else {
            val sp = this.name.split("（")
            sp[0]
        }
    }

    /**
     * 获取限定类型
     */
    fun getNameL(): String {
        val index = this.name.indexOf("（")
        return if (index == -1) {
            ""
        } else {
            this.name.substring(index + 1, this.name.length - 1)
        }
    }

}
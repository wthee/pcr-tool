package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo


/**
 * 角色信息视图
 */
data class CharacterInfo(
    @ColumnInfo(name = "unit_id") val id: Int = -1,
    @ColumnInfo(name = "unit_name") val name: String = "",
    @ColumnInfo(name = "rarity") val rarity: Int = 3,
    @ColumnInfo(name = "kana") val kana: String = "",
    @ColumnInfo(name = "age_int") val age: String = "",
    @ColumnInfo(name = "guild") val guild: String = "",
    @ColumnInfo(name = "race") val race: String = "",
    @ColumnInfo(name = "height_int") val height: String = "",
    @ColumnInfo(name = "weight_int") val weight: String = "",
    @ColumnInfo(name = "birth_month_int") val birthMonth: String = "",
    @ColumnInfo(name = "birth_day_int") val birthDay: String = "",
    @ColumnInfo(name = "search_area_width") val position: Int = 0,
    @ColumnInfo(name = "atk_type") val atkType: Int = 1,
    @ColumnInfo(name = "unit_start_time") val startTime: String = "2020-01-01 00:00:00",
    @ColumnInfo(name = "r6Id") val r6Id: Int = 1,
    @ColumnInfo(name = "limit_type") val limitType: Int = 0,
) {

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
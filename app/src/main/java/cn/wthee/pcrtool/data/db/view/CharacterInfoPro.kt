package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo


/**
 * 角色详情信息视图
 */
data class CharacterInfoPro(
    @ColumnInfo(name = "unit_id") val unitId: Int = 100101,
    @ColumnInfo(name = "unit_name") val unitName: String = "???",
    @ColumnInfo(name = "kana") val kana: String = "???",
    @ColumnInfo(name = "actual_name") val actualName: String = "???",
    @ColumnInfo(name = "age_int") val age: String = "999",
    @ColumnInfo(name = "guild") val guild: String = "???",
    @ColumnInfo(name = "race") val race: String = "???",
    @ColumnInfo(name = "height_int") val height: String = "999",
    @ColumnInfo(name = "weight_int") val weight: String = "999",
    @ColumnInfo(name = "birth_month") val birthMonth: String = "12",
    @ColumnInfo(name = "birth_day") val birthDay: String = "11",
    @ColumnInfo(name = "blood_type") val bloodType: String = "A",
    @ColumnInfo(name = "favorite") val favorite: String = "???",
    @ColumnInfo(name = "voice") val voice: String = "???",
    @ColumnInfo(name = "catch_copy") val catchCopy: String = "???",
    @ColumnInfo(name = "self_text") val selfText: String = "???",
    @ColumnInfo(name = "search_area_width") val position: Int = 100,
    @ColumnInfo(name = "intro") val intro: String = "???",
    @ColumnInfo(name = "atk_type") val atkType: Int = 1,
    @ColumnInfo(name = "r6Id") val r6Id: Int = 0,
    @ColumnInfo(name = "rarity") val rarity: Int = 3,
) {

    /**
     * 格式化年龄
     */
    fun getFixedAge() = if (age == "999") "?" else age

    /**
     * 格式化身高
     */
    fun getFixedHeight() = if (height == "999") "?" else height

    /**
     * 格式化体重
     */
    fun getFixedWeight() = if (weight == "999") "?" else weight

    /**
     * 角色自我介绍
     */
    fun getSelf() = if (this.selfText.contains("test") || this.selfText.isBlank()) {
        null
    } else {
        this.selfText.replace("\\n", "")
    }

    /**
     * 角色介绍
     */
    fun getIntroText() = intro.replace("\\n", "")

}
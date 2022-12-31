package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo


/**
 * 角色详情信息视图
 */
data class CharacterInfoPro(
    @ColumnInfo(name = "unit_id") val unitId: Int = 100101,
    @ColumnInfo(name = "unit_name") val unitName: String = "",
    @ColumnInfo(name = "kana") val kana: String = "",
    @ColumnInfo(name = "actual_name") val actualName: String = "",
    @ColumnInfo(name = "age_int") val age: String = "999",
    @ColumnInfo(name = "guild") val guild: String = "???",
    @ColumnInfo(name = "race") val race: String = "???",
    @ColumnInfo(name = "height_int") val height: String = "999",
    @ColumnInfo(name = "weight_int") val weight: String = "999",
    @ColumnInfo(name = "birth_month_int") val birthMonth: String = "999",
    @ColumnInfo(name = "birth_day_int") val birthDay: String = "999",
    @ColumnInfo(name = "blood_type") val bloodType: String = "A",
    @ColumnInfo(name = "favorite") val favorite: String = "???",
    @ColumnInfo(name = "voice") val voice: String = "???",
    @ColumnInfo(name = "catch_copy") val catchCopy: String = "???",
    @ColumnInfo(name = "self_text") val selfText: String = "???",
    @ColumnInfo(name = "intro") val intro: String = "???",
) {

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
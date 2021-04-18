package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import java.io.Serializable


/**
 * 角色详情信息视图
 */
data class CharacterInfoPro(
    @ColumnInfo(name = "unit_id") val id: Int,
    @ColumnInfo(name = "unit_name") val name: String,
    @ColumnInfo(name = "kana") val kana: String,
    @ColumnInfo(name = "actual_name") val actualName: String,
    @ColumnInfo(name = "age_int") val age: String,
    @ColumnInfo(name = "guild") val guild: String,
    @ColumnInfo(name = "race") val race: String,
    @ColumnInfo(name = "height_int") val height: String,
    @ColumnInfo(name = "weight_int") val weight: String,
    @ColumnInfo(name = "birth_month") val birthMonth: String,
    @ColumnInfo(name = "birth_day") val birthDay: String,
    @ColumnInfo(name = "blood_type") val bloodType: String,
    @ColumnInfo(name = "favorite") val favorite: String,
    @ColumnInfo(name = "voice") val voice: String,
    @ColumnInfo(name = "catch_copy") val catchCopy: String,
    @ColumnInfo(name = "self_text") val selfText: String,
    @ColumnInfo(name = "search_area_width") val position: Int,
    @ColumnInfo(name = "intro") val intro: String,
    @ColumnInfo(name = "atk_type") val atkType: Int,
    @ColumnInfo(name = "rarity_6_quest_id") val r6Id: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
    @ColumnInfo(name = "comments") val comments: String,
    @ColumnInfo(name = "room_comments") val roomComments: String,
) : Serializable {

    /**
     * 格式化年龄
     */
    fun getFixedAge() = if (age == "999") "?" else age

    /**
     * 格式化身高
     */
    fun getFixedHeight() = if (height == "999") "?" else height

    /**
     * 格式划体重
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

    /**
     * 交流
     */
    fun getCommentsText() =
        comments.replace("\\n", "").replace("-", "\n\n")


    /**
     * 小屋交流
     */
    fun getRoomCommentsText() =
        roomComments.replace("\\n", "").replace("-", "\n\n")

    /**
     * 生日
     */
    fun getBirth() = "$birthMonth 月 $birthDay 日"


}
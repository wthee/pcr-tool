package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import java.io.Serializable


//角色详情信息
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
) : Serializable {

    fun getFixedAge() = if (age == "999") "?" else age
    fun getFixedHeight() = if (height == "999") "?" else height
    fun getFixedWeight() = if (weight == "999") "?" else weight

    //获取名字，去除限定类型
    fun getNameF(): String {
        val index = this.name.indexOf("（")
        return if (index == -1) {
            this.name
        } else {
            val sp = this.name.split("（")
            sp[0]
        }
    }

    //获取限定类型
    fun getNameL(): String {
        val index = this.name.indexOf("（")
        return if (index == -1) {
            ""
        } else {
            val sp = this.name.split("（")
            sp[1].substring(0, sp[1].lastIndex)
        }
    }

    //角色自我介绍
    fun getSelf() = if (this.selfText.contains("test") || this.selfText.isBlank()) {
        "......"
    } else {
        this.selfText.replace("\\n", "")
    }


    //角色介绍
    fun getIntroText() = intro.replace("\\n", "")

    //交流
    fun getCommentsText() =
        comments.replace("\\n", "").replace("-", "\n\n")

}
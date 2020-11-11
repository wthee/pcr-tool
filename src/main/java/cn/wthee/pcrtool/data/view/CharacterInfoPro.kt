package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.Constants
import java.io.Serializable


//多表查询结果保存
data class CharacterInfoPro(
    @ColumnInfo(name = "unit_id") val id: Int,
    @ColumnInfo(name = "unit_name") val name: String,
    @ColumnInfo(name = "kana") val kana: String,
    @ColumnInfo(name = "actual_name") val actualName: String,
    @ColumnInfo(name = "age") val age: String,
    @ColumnInfo(name = "guild") val guild: String,
    @ColumnInfo(name = "race") val race: String,
    @ColumnInfo(name = "height") val height: String,
    @ColumnInfo(name = "weight") val weight: String,
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

    fun getStarId(star: Int): String {
        val idStr = id.toString()
        return idStr.substring(0, 4) + star + idStr[idStr.lastIndex]
    }

    fun getAllStarId(): List<String> {
        val list = arrayListOf<String>()
        list.add(this.getStarId(1))
        list.add(this.getStarId(3))
        if (this.r6Id != 0) {
            list.add(this.getStarId(6))
        }
        return list
    }

    fun getAllUrl(): ArrayList<String> {
        val list = arrayListOf<String>()
        if (this.r6Id != 0) {
            list.add(Constants.CHARACTER_URL + getStarId(6) + Constants.WEBP)
        }
        list.add(Constants.CHARACTER_URL + getStarId(3) + Constants.WEBP)
        list.add(Constants.CHARACTER_URL + getStarId(1) + Constants.WEBP)
        list.add(Constants.Reality_CHARACTER_URL + getFixedId() + Constants.WEBP)
        return list
    }

    //去除无效id
    private fun getFixedId() = if (Constants.errorIDs.contains(id)) id + 31 else 30

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
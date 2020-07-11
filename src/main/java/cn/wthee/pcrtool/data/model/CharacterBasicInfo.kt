package cn.wthee.pcrtool.data.model

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import java.io.Serializable


//多表查询结果保存
data class CharacterBasicInfo(
    @ColumnInfo(name = "unit_id") val id: Int,
    @ColumnInfo(name = "unit_name") val name: String,
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
    @ColumnInfo(name = "serif_1") val serif1: String,
    @ColumnInfo(name = "serif_2") val serif2: String,
    @ColumnInfo(name = "serif_3") val serif3: String,
    @ColumnInfo(name = "search_area_width") val position: Int,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "rarity_6_quest_id") val r6Id: Int
) : Serializable {

    private fun getStarId(star: Int): String {
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


    fun getFixedId(): Int {
        val errorIds = arrayListOf(
            101301, 105401, 101501,
            101001, 102201, 102801,
            103801, 104501, 104601
        )
        return if (errorIds.contains(id)) {
            id + 31
        } else {
            id + 30
        }
    }

    fun getSelf() =
        if (this.selfText.contains("？") || this.selfText.contains("?") || this.selfText.contains("test") || this.selfText.isEmpty()) {
            "......"
        } else {
            this.selfText.replace("\\n", "")
        }

    fun getNameF(): String {
        val index = this.name.indexOf("（")
        return if (index == -1) {
            this.name
        } else {
            val sp = this.name.split("（")
            sp[0]
        }
    }

    fun getNameL(): String {
        val index = this.name.indexOf("（")
        return if (index == -1) {
            ""
        } else {
            val sp = this.name.split("（")
            sp[1].substring(0, sp[1].lastIndex)
        }
    }

    fun getPositionIcon() = when (this.position) {
        in 0..300 -> R.drawable.ic_position_0_300
        in 301..600 -> R.drawable.ic_position_301_600
        in 601..9999 -> R.drawable.ic_position_600
        else -> R.drawable.ic_position_600
    }

    fun getLoveSelfText(): String {
        val text = serif1 + serif2 + serif3
        return if (this.selfText.contains("？") || this.selfText.contains("?") || this.selfText.contains("test") || this.selfText.isEmpty()) {
            "......"
        } else {
            text.replace("\\n", "")
        }
    }

    fun getFixedComment() = comment.replace("\\n", "")
}
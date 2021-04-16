package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import java.io.Serializable


/**
 * 角色信息视图
 */
data class CharacterInfo(
    @ColumnInfo(name = "unit_id") val id: Int,
    @ColumnInfo(name = "unit_name") val name: String,
    @ColumnInfo(name = "kana") val kana: String,
    @ColumnInfo(name = "age_int") val age: String,
    @ColumnInfo(name = "guild") val guild: String,
    @ColumnInfo(name = "race") val race: String,
    @ColumnInfo(name = "height_int") val height: String,
    @ColumnInfo(name = "weight_int") val weight: String,
    @ColumnInfo(name = "search_area_width") val position: Int,
    @ColumnInfo(name = "atk_type") val atkType: Int,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "rarity_6_quest_id") val r6Id: Int,
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
            kana
        } else {
            val sp = this.name.split("（")
            sp[1].substring(0, sp[1].lastIndex)
        }
    }

}

/**
 * 根据位置 [position]
 */
fun getPositionIcon(position: Int) = when (position) {
    in 0..299 -> R.drawable.ic_position_0
    in 300..599 -> R.drawable.ic_position_1
    in 600..9999 -> R.drawable.ic_position_2
    else -> R.drawable.ic_position_2
}
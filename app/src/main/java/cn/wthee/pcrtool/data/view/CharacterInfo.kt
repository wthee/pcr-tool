package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import java.io.Serializable


/**
 * 角色信息视图
 */
data class CharacterInfo(
    @ColumnInfo(name = "unit_id") var id: Int = 0,
    @ColumnInfo(name = "unit_name") var name: String = "test",
    @ColumnInfo(name = "kana") var kana: String = "test",
    @ColumnInfo(name = "age_int") var age: String = "8",
    @ColumnInfo(name = "guild") var guild: String = "?",
    @ColumnInfo(name = "race") var race: String = "?",
    @ColumnInfo(name = "height_int") var height: String = "?",
    @ColumnInfo(name = "weight_int") var weight: String = "?",
    @ColumnInfo(name = "search_area_width") var position: Int = 0,
    @ColumnInfo(name = "atk_type") var atkType: Int = 0,
    @ColumnInfo(name = "start_time") var startTime: String = "2030-01-01 00:00:00",
    @ColumnInfo(name = "rarity_6_quest_id") var r6Id: Int = 0,
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
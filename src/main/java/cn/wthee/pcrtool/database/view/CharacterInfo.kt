package cn.wthee.pcrtool.database.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import java.io.Serializable


//多表查询结果保存
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
) : Serializable {

    fun getFixedAge() = if (age == "999") "??" else age
    fun getFixedHeight() = if (height == "999") "??" else height
    fun getFixedWeight() = if (weight == "999") "??" else weight

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
            kana
        } else {
            val sp = this.name.split("（")
            sp[1].substring(0, sp[1].lastIndex)
        }
    }

}

//位置
fun getPositionIcon(position: Int) = when (position) {
    in 0..299 -> R.drawable.ic_position_front
    in 300..599 -> R.drawable.ic_position_middle
    in 600..9999 -> R.drawable.ic_position_after
    else -> R.drawable.ic_position_after
}
package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 动作循环
 */
@Entity(tableName = "unit_attack_pattern")
data class AttackPattern(
    @PrimaryKey
    @ColumnInfo(name = "pattern_id") val patternId: Int,
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "loop_start") val loopStart: Int,
    @ColumnInfo(name = "loop_end") val loopEnd: Int,
    @ColumnInfo(name = "atk_pattern_1") val atkPattern1: Int,
    @ColumnInfo(name = "atk_pattern_2") val atkPattern2: Int,
    @ColumnInfo(name = "atk_pattern_3") val atkPattern3: Int,
    @ColumnInfo(name = "atk_pattern_4") val atkPattern4: Int,
    @ColumnInfo(name = "atk_pattern_5") val atkPattern5: Int,
    @ColumnInfo(name = "atk_pattern_6") val atkPattern6: Int,
    @ColumnInfo(name = "atk_pattern_7") val atkPattern7: Int,
    @ColumnInfo(name = "atk_pattern_8") val atkPattern8: Int,
    @ColumnInfo(name = "atk_pattern_9") val atkPattern9: Int,
    @ColumnInfo(name = "atk_pattern_10") val atkPattern10: Int,
    @ColumnInfo(name = "atk_pattern_11") val atkPattern11: Int,
    @ColumnInfo(name = "atk_pattern_12") val atkPattern12: Int,
    @ColumnInfo(name = "atk_pattern_13") val atkPattern13: Int,
    @ColumnInfo(name = "atk_pattern_14") val atkPattern14: Int,
    @ColumnInfo(name = "atk_pattern_15") val atkPattern15: Int,
    @ColumnInfo(name = "atk_pattern_16") val atkPattern16: Int,
    @ColumnInfo(name = "atk_pattern_17") val atkPattern17: Int,
    @ColumnInfo(name = "atk_pattern_18") val atkPattern18: Int,
    @ColumnInfo(name = "atk_pattern_19") val atkPattern19: Int,
    @ColumnInfo(name = "atk_pattern_20") val atkPattern20: Int
) {

    fun getBefore(): MutableList<Int> {
        val list = getList()
        return list.subList(0, loopStart - 1)
    }

    fun getLoop(): MutableList<Int> {
        val list = getList()
        return list.subList(loopStart - 1, loopEnd)
    }


    private fun getList(): ArrayList<Int> {
        val list = arrayListOf<Int>()
        list.add(atkPattern1)
        list.add(atkPattern2)
        list.add(atkPattern3)
        list.add(atkPattern4)
        list.add(atkPattern5)
        list.add(atkPattern6)
        list.add(atkPattern7)
        list.add(atkPattern8)
        list.add(atkPattern9)
        list.add(atkPattern10)
        list.add(atkPattern11)
        list.add(atkPattern12)
        list.add(atkPattern13)
        list.add(atkPattern14)
        list.add(atkPattern15)
        list.add(atkPattern16)
        list.add(atkPattern17)
        list.add(atkPattern18)
        list.add(atkPattern19)
        list.add(atkPattern20)
        return list
    }
}



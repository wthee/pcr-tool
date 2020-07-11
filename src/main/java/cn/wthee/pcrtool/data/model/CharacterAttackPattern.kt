package cn.wthee.pcrtool.data.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色羁绊Rank提升文本
@Entity(tableName = "unit_attack_pattern")
data class CharacterAttackPattern(
    @PrimaryKey
    @ColumnInfo(name = "pattern_id") val pattern_id: Int,
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "loop_start") val loop_start: Int,
    @ColumnInfo(name = "loop_end") val loop_end: Int,
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
) : Serializable


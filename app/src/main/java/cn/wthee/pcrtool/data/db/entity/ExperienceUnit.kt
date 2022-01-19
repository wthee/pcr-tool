package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 角色等级信息
 */
@Entity(tableName = "experience_unit")
data class ExperienceUnit(
    @PrimaryKey
    @ColumnInfo(name = "unit_level") val level: Int,
    @ColumnInfo(name = "total_exp") val exp: Int
)
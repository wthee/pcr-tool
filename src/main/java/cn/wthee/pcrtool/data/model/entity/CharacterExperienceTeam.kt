package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//主角等级信息
@Entity(tableName = "experience_team")
data class CharacterExperienceTeam(
    @PrimaryKey
    @ColumnInfo(name = "team_level") val teamLevel: Int,
    @ColumnInfo(name = "total_exp") val totalExp: Int,
    @ColumnInfo(name = "max_stamina") val maxStamina: Int,
    @ColumnInfo(name = "over_limit_stamina") val limitStamina: Int,
    @ColumnInfo(name = "recover_stamina_count") val recoverCount: Int,
)
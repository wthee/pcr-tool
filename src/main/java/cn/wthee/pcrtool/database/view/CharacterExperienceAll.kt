package cn.wthee.pcrtool.database.view

import androidx.room.ColumnInfo
import java.io.Serializable

data class CharacterExperienceAll(
    @ColumnInfo(name = "level") val level: Int,
    @ColumnInfo(name = "exp_team") val expTeam: Int,
    @ColumnInfo(name = "exp_team_abs") val expTeamAbs: Int,
    @ColumnInfo(name = "exp_unit") val expUnit: Int,
    @ColumnInfo(name = "exp_unit_abs") val expUnitAbs: Int
) : Serializable
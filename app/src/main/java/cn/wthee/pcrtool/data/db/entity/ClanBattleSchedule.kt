package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clan_battle_schedule")
data class ClanBattleSchedule(
    @PrimaryKey
    @ColumnInfo(name = "clan_battle_id") val clan_battle_id: Int,
    @ColumnInfo(name = "release_month") val release_month: Int,
    @ColumnInfo(name = "last_clan_battle_id") val last_clan_battle_id: Int,
    @ColumnInfo(name = "point_per_stamina") val point_per_stamina: Int,
    @ColumnInfo(name = "cost_group_id") val cost_group_id: Int,
    @ColumnInfo(name = "cost_group_id_s") val cost_group_id_s: Int,
    @ColumnInfo(name = "map_bgm") val map_bgm: String,
    @ColumnInfo(name = "resource_id") val resource_id: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
)
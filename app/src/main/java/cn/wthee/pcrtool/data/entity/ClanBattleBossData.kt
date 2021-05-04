package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clan_battle_2_boss_data")
data class ClanBattleBossData(
    @PrimaryKey
    @ColumnInfo(name = "boss_id") val boss_id: Int,
    @ColumnInfo(name = "clan_battle_id") val clan_battle_id: Int,
    @ColumnInfo(name = "difficulty") val difficulty: Int,
    @ColumnInfo(name = "order_num") val order_num: Int,
    @ColumnInfo(name = "boss_thumb_id") val boss_thumb_id: Int,
    @ColumnInfo(name = "position_x") val position_x: Int,
    @ColumnInfo(name = "position_y") val position_y: Int,
    @ColumnInfo(name = "scale_ratio") val scale_ratio: Double,
//    @ColumnInfo(name = "tap_width_ratio") val tap_width_ratio: Double,
//    @ColumnInfo(name = "tap_height_ratio") val tap_height_ratio: Double,
    @ColumnInfo(name = "map_position_x") val map_position_x: Int,
    @ColumnInfo(name = "map_position_y") val map_position_y: Int,
    @ColumnInfo(name = "cursor_position") val cursor_position: Int,
    @ColumnInfo(name = "result_boss_position_y") val result_boss_position_y: Int,
    @ColumnInfo(name = "quest_detail_bg_id") val quest_detail_bg_id: Int,
    @ColumnInfo(name = "quest_detail_bg_position") val quest_detail_bg_position: Int,
    @ColumnInfo(name = "quest_detail_monster_size") val quest_detail_monster_size: Double,
    @ColumnInfo(name = "quest_detail_monster_height") val quest_detail_monster_height: Int,
    @ColumnInfo(name = "battle_report_monster_size") val battle_report_monster_size: Double,
    @ColumnInfo(name = "battle_report_monster_height") val battle_report_monster_height: Int,
    @ColumnInfo(name = "background") val background: Int,
    @ColumnInfo(name = "wave_bgm") val wave_bgm: String,
)
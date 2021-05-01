package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ClanBattleInfoPro(
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "release_month") val release_month: Int,
    @ColumnInfo(name = "boss_id") val boss_id: Int,
    @ColumnInfo(name = "clan_battle_id") val clan_battle_id: Int,
    @ColumnInfo(name = "order_num") val order_num: Int,
    @ColumnInfo(name = "enemy_id") val enemy_id: Int,
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "level") val level: Int,
    @Embedded var attr: AttrInt = AttrInt(),
    @ColumnInfo(name = "union_burst_level") val union_burst_level: Int,
    @ColumnInfo(name = "main_skill_lv_1") val main_skill_lv_1: Int,
    @ColumnInfo(name = "main_skill_lv_2") val main_skill_lv_2: Int,
    @ColumnInfo(name = "main_skill_lv_3") val main_skill_lv_3: Int,
    @ColumnInfo(name = "main_skill_lv_4") val main_skill_lv_4: Int,
    @ColumnInfo(name = "main_skill_lv_5") val main_skill_lv_5: Int,
    @ColumnInfo(name = "main_skill_lv_6") val main_skill_lv_6: Int,
    @ColumnInfo(name = "main_skill_lv_7") val main_skill_lv_7: Int,
    @ColumnInfo(name = "main_skill_lv_8") val main_skill_lv_8: Int,
    @ColumnInfo(name = "main_skill_lv_9") val main_skill_lv_9: Int,
    @ColumnInfo(name = "main_skill_lv_10") val main_skill_lv_10: Int,
    @ColumnInfo(name = "ex_skill_lv_1") val ex_skill_lv_1: Int,
    @ColumnInfo(name = "ex_skill_lv_2") val ex_skill_lv_2: Int,
    @ColumnInfo(name = "ex_skill_lv_3") val ex_skill_lv_3: Int,
    @ColumnInfo(name = "ex_skill_lv_4") val ex_skill_lv_4: Int,
    @ColumnInfo(name = "ex_skill_lv_5") val ex_skill_lv_5: Int,
)
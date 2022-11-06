package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * ex装备掉落信息
 */
data class ExtraEquipQuestData(
    @PrimaryKey
    @ColumnInfo(name = "travel_quest_id") val travelQuestId: Int,
    @ColumnInfo(name = "travel_area_id") val travelAreaId: Int,
    @ColumnInfo(name = "travel_quest_name") val travelQuestName: String,
    @ColumnInfo(name = "travel_time") val travelTime: Int,
    @ColumnInfo(name = "travel_time_decrease_limit") val travelTimeDecreaseLimit: Int,
    @ColumnInfo(name = "travel_decrease_flag") val travel_decrease_flag: Int,
    @ColumnInfo(name = "need_power") val need_power: Int,
    @ColumnInfo(name = "icon_id") val icon_id: Int,
    @ColumnInfo(name = "limit_unit_num") val limit_unit_num: Int,
    @ColumnInfo(name = "main_reward_ids") val main_reward_ids: String
)

/**
 * 次要掉落
 */
data class ExtraEquipSubRewardData(
    @PrimaryKey
    @ColumnInfo(name = "travel_quest_id") val travelQuestId: Int,
    @ColumnInfo(name = "category") val category: Int,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "sub_reward_ids") val subRewardIds: String,
)
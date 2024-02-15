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
    @ColumnInfo(name = "limit_unit_num") val limitUnitNum: Int,
    @ColumnInfo(name = "travel_time") val travelTime: Long,
    @ColumnInfo(name = "travel_time_decrease_limit") val travelTimeDecreaseLimit: Long,
    @ColumnInfo(name = "travel_decrease_flag") val travelDecreaseFlag: Int,
    @ColumnInfo(name = "need_power") val needPower: Int,
    @ColumnInfo(name = "icon_id") val iconId: Int
){
    /**
     * 获取标题
     */
    fun getTitle() = "${travelAreaId % 10}-${travelQuestId % 10} ${getQuestName()}"

    /**
     * 获取名称
     */


    private fun getQuestName() = travelQuestName.replace("\\n", "·")
}

/**
 * 次要掉落
 */
data class ExtraEquipSubRewardData(
    @PrimaryKey
    @ColumnInfo(name = "travel_quest_id") val travelQuestId: Int,
    @ColumnInfo(name = "category") val category: Int,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "sub_reward_ids") val subRewardIds: String,
    @ColumnInfo(name = "sub_reward_drops") val subRewardDrops: String,
)
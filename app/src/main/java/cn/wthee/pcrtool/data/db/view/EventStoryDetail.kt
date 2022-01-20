package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 剧情活动详情
 */
data class EventStoryDetail(
    @PrimaryKey
    @ColumnInfo(name = "story_id") val story_id: Int,
    @ColumnInfo(name = "story_group_id") val story_group_id: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "sub_title") val sub_title: String,
    @ColumnInfo(name = "visible_type") val visible_type: Int,
    @ColumnInfo(name = "story_end") val story_end: Int,
    @ColumnInfo(name = "pre_story_id") val pre_story_id: Int,
    @ColumnInfo(name = "love_level") val love_level: Int,
    @ColumnInfo(name = "requirement_id") val requirement_id: Int,
    @ColumnInfo(name = "unlock_quest_id") val unlock_quest_id: Int,
    @ColumnInfo(name = "story_quest_id") val story_quest_id: Int,
    @ColumnInfo(name = "reward_type_1") val reward_type_1: Int,
    @ColumnInfo(name = "reward_id_1") val reward_id_1: Int,
    @ColumnInfo(name = "reward_value_1") val reward_value_1: Int,
    @ColumnInfo(name = "reward_type_2") val reward_type_2: Int,
    @ColumnInfo(name = "reward_id_2") val reward_id_2: Int,
    @ColumnInfo(name = "reward_value_2") val reward_value_2: Int,
    @ColumnInfo(name = "reward_type_3") val reward_type_3: Int,
    @ColumnInfo(name = "reward_id_3") val reward_id_3: Int,
    @ColumnInfo(name = "reward_value_3") val reward_value_3: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
)

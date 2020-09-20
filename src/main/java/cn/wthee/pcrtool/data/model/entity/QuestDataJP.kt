package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//关卡信息
@Entity(tableName = "quest_data")
class QuestDataJP(
    @PrimaryKey
    @ColumnInfo(name = "quest_id") val quest_id: Int,
    @ColumnInfo(name = "area_id") val area_id: Int,
    @ColumnInfo(name = "quest_name") val quest_name: String,
    @ColumnInfo(name = "limit_team_level") val limit_team_level: Int,
    @ColumnInfo(name = "position_x") val position_x: Int,
    @ColumnInfo(name = "position_y") val position_y: Int,
    @ColumnInfo(name = "icon_id") val icon_id: Int,
    @ColumnInfo(name = "stamina") val stamina: Int,
    @ColumnInfo(name = "stamina_start") val stamina_start: Int,
    @ColumnInfo(name = "team_exp") val team_exp: Int,
    @ColumnInfo(name = "unit_exp") val unit_exp: Int,
    @ColumnInfo(name = "love") val love: Int,
    @ColumnInfo(name = "limit_time") val limit_time: Int,
    @ColumnInfo(name = "daily_limit") val daily_limit: Int,
    @ColumnInfo(name = "clear_reward_group") val clear_reward_group: Int,
    @ColumnInfo(name = "rank_reward_group") val rank_reward_group: Int,
    @ColumnInfo(name = "background_1") val background_1: Int,
    @ColumnInfo(name = "wave_group_id_1") val wave_group_id_1: Int,
    @ColumnInfo(name = "wave_bgm_sheet_id_1") val wave_bgm_sheet_id_1: String,
    @ColumnInfo(name = "wave_bgm_que_id_1") val wave_bgm_que_id_1: String,
    @ColumnInfo(name = "story_id_wavestart_1") val story_id_wavestart_1: Int,
    @ColumnInfo(name = "story_id_waveend_1") val story_id_waveend_1: Int,
    @ColumnInfo(name = "background_2") val background_2: Int,
    @ColumnInfo(name = "wave_group_id_2") val wave_group_id_2: Int,
    @ColumnInfo(name = "wave_bgm_sheet_id_2") val wave_bgm_sheet_id_2: String,
    @ColumnInfo(name = "wave_bgm_que_id_2") val wave_bgm_que_id_2: String,
    @ColumnInfo(name = "story_id_wavestart_2") val story_id_wavestart_2: Int,
    @ColumnInfo(name = "story_id_waveend_2") val story_id_waveend_2: Int,
    @ColumnInfo(name = "background_3") val background_3: Int,
    @ColumnInfo(name = "wave_group_id_3") val wave_group_id_3: Int,
    @ColumnInfo(name = "wave_bgm_sheet_id_3") val wave_bgm_sheet_id_3: String,
    @ColumnInfo(name = "wave_bgm_que_id_3") val wave_bgm_que_id_3: String,
    @ColumnInfo(name = "story_id_wavestart_3") val story_id_wavestart_3: Int,
    @ColumnInfo(name = "story_id_waveend_3") val story_id_waveend_3: Int,
    @ColumnInfo(name = "enemy_image_1") val enemy_image_1: Int,
    @ColumnInfo(name = "enemy_image_2") val enemy_image_2: Int,
    @ColumnInfo(name = "enemy_image_3") val enemy_image_3: Int,
    @ColumnInfo(name = "enemy_image_4") val enemy_image_4: Int,
    @ColumnInfo(name = "enemy_image_5") val enemy_image_5: Int,
    @ColumnInfo(name = "reward_image_1") val reward_image_1: Int,
    @ColumnInfo(name = "reward_image_2") val reward_image_2: Int,
    @ColumnInfo(name = "reward_image_3") val reward_image_3: Int,
    @ColumnInfo(name = "reward_image_4") val reward_image_4: Int,
    @ColumnInfo(name = "reward_image_5") val reward_image_5: Int,
    @ColumnInfo(name = "quest_detail_bg_id") val quest_detail_bg_id: Int,
    @ColumnInfo(name = "quest_detail_bg_position") val quest_detail_bg_position: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
    //jp
    @ColumnInfo(name = "lv_reward_flag") val lv_reward_flag: Int,
    @ColumnInfo(name = "add_treasure_num") val add_treasure_num: Int,
) : Serializable
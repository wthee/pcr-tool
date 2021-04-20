package cn.wthee.pcrtool.data.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * 关卡信息
 */
@Entity(tableName = "quest_data")
class QuestDataJP(
    @PrimaryKey
    @ColumnInfo(name = "quest_id") var quest_id: Int = 0,
    @ColumnInfo(name = "area_id") var area_id: Int = 0,
    @ColumnInfo(name = "quest_name") var quest_name: String = "",
    @ColumnInfo(name = "limit_team_level") var limit_team_level: Int = 0,
    @ColumnInfo(name = "position_x") var position_x: Int = 0,
    @ColumnInfo(name = "position_y") var position_y: Int = 0,
    @ColumnInfo(name = "icon_id") var icon_id: Int = 0,
    @ColumnInfo(name = "stamina") var stamina: Int = 0,
    @ColumnInfo(name = "stamina_start") var stamina_start: Int = 0,
    @ColumnInfo(name = "team_exp") var team_exp: Int = 0,
    @ColumnInfo(name = "unit_exp") var unit_exp: Int = 0,
    @ColumnInfo(name = "love") var love: Int = 0,
    @ColumnInfo(name = "limit_time") var limit_time: Int = 0,
    @ColumnInfo(name = "daily_limit") var daily_limit: Int = 0,
    @ColumnInfo(name = "clear_reward_group") var clear_reward_group: Int = 0,
    @ColumnInfo(name = "rank_reward_group") var rank_reward_group: Int = 0,
    @ColumnInfo(name = "background_1") var background_1: Int = 0,
    @ColumnInfo(name = "wave_group_id_1") var wave_group_id_1: Int = 0,
    @ColumnInfo(name = "wave_bgm_sheet_id_1") var wave_bgm_sheet_id_1: String = "",
    @ColumnInfo(name = "wave_bgm_que_id_1") var wave_bgm_que_id_1: String = "",
    @ColumnInfo(name = "story_id_wavestart_1") var story_id_wavestart_1: Int = 0,
    @ColumnInfo(name = "story_id_waveend_1") var story_id_waveend_1: Int = 0,
    @ColumnInfo(name = "background_2") var background_2: Int = 0,
    @ColumnInfo(name = "wave_group_id_2") var wave_group_id_2: Int = 0,
    @ColumnInfo(name = "wave_bgm_sheet_id_2") var wave_bgm_sheet_id_2: String = "",
    @ColumnInfo(name = "wave_bgm_que_id_2") var wave_bgm_que_id_2: String = "",
    @ColumnInfo(name = "story_id_wavestart_2") var story_id_wavestart_2: Int = 0,
    @ColumnInfo(name = "story_id_waveend_2") var story_id_waveend_2: Int = 0,
    @ColumnInfo(name = "background_3") var background_3: Int = 0,
    @ColumnInfo(name = "wave_group_id_3") var wave_group_id_3: Int = 0,
    @ColumnInfo(name = "wave_bgm_sheet_id_3") var wave_bgm_sheet_id_3: String = "",
    @ColumnInfo(name = "wave_bgm_que_id_3") var wave_bgm_que_id_3: String = "",
    @ColumnInfo(name = "story_id_wavestart_3") var story_id_wavestart_3: Int = 0,
    @ColumnInfo(name = "story_id_waveend_3") var story_id_waveend_3: Int = 0,
    @ColumnInfo(name = "enemy_image_1") var enemy_image_1: Int = 0,
    @ColumnInfo(name = "enemy_image_2") var enemy_image_2: Int = 0,
    @ColumnInfo(name = "enemy_image_3") var enemy_image_3: Int = 0,
    @ColumnInfo(name = "enemy_image_4") var enemy_image_4: Int = 0,
    @ColumnInfo(name = "enemy_image_5") var enemy_image_5: Int = 0,
    @ColumnInfo(name = "reward_image_1") var reward_image_1: Int = 0,
    @ColumnInfo(name = "reward_image_2") var reward_image_2: Int = 0,
    @ColumnInfo(name = "reward_image_3") var reward_image_3: Int = 0,
    @ColumnInfo(name = "reward_image_4") var reward_image_4: Int = 0,
    @ColumnInfo(name = "reward_image_5") var reward_image_5: Int = 0,
    @ColumnInfo(name = "quest_detail_bg_id") var quest_detail_bg_id: Int = 0,
    @ColumnInfo(name = "quest_detail_bg_position") var quest_detail_bg_position: Int = 0,
    @ColumnInfo(name = "start_time") var start_time: String = "",
    @ColumnInfo(name = "end_time") var end_time: String = "",
    @ColumnInfo(name = "lv_reward_flag") var lv_reward_flag: Int = 0,
    //jp
    @ColumnInfo(name = "add_treasure_num") var add_treasure_num: Int = 0,
) : Serializable
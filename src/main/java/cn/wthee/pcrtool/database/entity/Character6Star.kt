package cn.wthee.pcrtool.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

//六星角色信息
@Entity(
    tableName = "rarity_6_quest_data",
    indices = [Index(
        value = arrayOf("rarity_6_quest_id"),
        unique = false,
        name = "rarity_6_quest_data_0_rarity_6_quest_id"
    )]
)
data class Character6Star(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val dataId: Int,
    @ColumnInfo(name = "rarity_6_quest_id") val r6Id: Int,
    @ColumnInfo(name = "quest_name") val questName: String,
    @ColumnInfo(name = "limit_time") val limitTime: Int,
    @ColumnInfo(name = "recommended_level") val recommendedLevel: Int,
    @ColumnInfo(name = "reward_group_id") val rewardGroupId: Int,
    @ColumnInfo(name = "treasure_type") val treasureType: Int,
    @ColumnInfo(name = "reward_image_1") val rewardImage1: Int,
    @ColumnInfo(name = "reward_count_1") val rewardCount1: Int,
    @ColumnInfo(name = "reward_image_2") val rewardImage2: Int,
    @ColumnInfo(name = "reward_count_2") val rewardCount2: Int,
    @ColumnInfo(name = "reward_image_3") val rewardImage3: Int,
    @ColumnInfo(name = "reward_count_3") val rewardCount3: Int,
    @ColumnInfo(name = "reward_image_4") val rewardImage4: Int,
    @ColumnInfo(name = "reward_count_4") val rewardCount4: Int,
    @ColumnInfo(name = "reward_image_5") val rewardImage5: Int,
    @ColumnInfo(name = "reward_count_5") val rewardCount5: Int,
    @ColumnInfo(name = "background") val background: Int,
    @ColumnInfo(name = "bg_position") val bgPosition: Int,
    @ColumnInfo(name = "wave_group_id") val waveGroupId: Int,
    @ColumnInfo(name = "enemy_position_x_1") val enemyPositionX1: Int,
    @ColumnInfo(name = "enemy_local_position_y_1") val enemyLocalPositionY1: Int,
    @ColumnInfo(name = "enemy_size_1") val enemySize1: Double,
    @ColumnInfo(name = "enemy_position_x_2") val enemyPositionX_2: Int,
    @ColumnInfo(name = "enemy_local_position_y_2") val enemyLocalPositionY2: Int,
    @ColumnInfo(name = "enemy_size_2") val enemySize2: Double,
    @ColumnInfo(name = "enemy_position_x_3") val enemyPositionX3: Int,
    @ColumnInfo(name = "enemy_local_position_y_3") val enemyLocalPositionY3: Int,
    @ColumnInfo(name = "enemy_size_3") val enemySize3: Double,
    @ColumnInfo(name = "enemy_position_x_4") val enemyPositionX: Int,
    @ColumnInfo(name = "enemy_local_position_y_4") val enemyLocalPositionY4: Int,
    @ColumnInfo(name = "enemy_size_4") val enemySize4: Double,
    @ColumnInfo(name = "enemy_position_x_5") val enemyPositionX5: Int,
    @ColumnInfo(name = "enemy_local_position_y_5") val enemyLocalPositionY5: Int,
    @ColumnInfo(name = "enemy_size_5") val enemySize5: Double,
    @ColumnInfo(name = "wave_bgm") val waveBgm: String
) : Serializable

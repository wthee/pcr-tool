package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 掉落信息
 */
@Entity(tableName = "enemy_reward_data")
class EnemyRewardData(
    @PrimaryKey
    @ColumnInfo(name = "drop_reward_id") val drop_reward_id: Int,
    @ColumnInfo(name = "drop_count") val drop_count: Int,
    @ColumnInfo(name = "reward_type_1") val reward_type_1: Int,
    @ColumnInfo(name = "reward_id_1") val reward_id_1: Int,
    @ColumnInfo(name = "reward_num_1") val reward_num_1: Int,
    @ColumnInfo(name = "odds_1") val odds_1: Int,
    @ColumnInfo(name = "reward_type_2") val reward_type_2: Int,
    @ColumnInfo(name = "reward_id_2") val reward_id_2: Int,
    @ColumnInfo(name = "reward_num_2") val reward_num_2: Int,
    @ColumnInfo(name = "odds_2") val odds_2: Int,
    @ColumnInfo(name = "reward_type_3") val reward_type_3: Int,
    @ColumnInfo(name = "reward_id_3") val reward_id_3: Int,
    @ColumnInfo(name = "reward_num_3") val reward_num_3: Int,
    @ColumnInfo(name = "odds_3") val odds_3: Int,
    @ColumnInfo(name = "reward_type_4") val reward_type_4: Int,
    @ColumnInfo(name = "reward_id_4") val reward_id_4: Int,
    @ColumnInfo(name = "reward_num_4") val reward_num_4: Int,
    @ColumnInfo(name = "odds_4") val odds_4: Int,
    @ColumnInfo(name = "reward_type_5") val reward_type_5: Int,
    @ColumnInfo(name = "reward_id_5") val reward_id_5: Int,
    @ColumnInfo(name = "reward_num_5") val reward_num_5: Int,
    @ColumnInfo(name = "odds_5") val odds_5: Int
)


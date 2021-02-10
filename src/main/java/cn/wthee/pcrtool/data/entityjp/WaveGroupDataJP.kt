package cn.wthee.pcrtool.data.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//关卡波次掉落信息
@Entity(tableName = "wave_group_data")
class WaveGroupDataJP(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "wave_group_id") val wave_group_id: Int,
    @ColumnInfo(name = "odds") val odds: Int,
    @ColumnInfo(name = "enemy_id_1") val enemy_id_1: Int,
    @ColumnInfo(name = "drop_gold_1") val drop_gold_1: Int,
    @ColumnInfo(name = "drop_reward_id_1") val drop_reward_id_1: Int,
    @ColumnInfo(name = "enemy_id_2") val enemy_id_2: Int,
    @ColumnInfo(name = "drop_gold_2") val drop_gold_2: Int,
    @ColumnInfo(name = "drop_reward_id_2") val drop_reward_id_2: Int,
    @ColumnInfo(name = "enemy_id_3") val enemy_id_3: Int,
    @ColumnInfo(name = "drop_gold_3") val drop_gold_3: Int,
    @ColumnInfo(name = "drop_reward_id_3") val drop_reward_id_3: Int,
    @ColumnInfo(name = "enemy_id_4") val enemy_id_4: Int,
    @ColumnInfo(name = "drop_gold_4") val drop_gold_4: Int,
    @ColumnInfo(name = "drop_reward_id_4") val drop_reward_id_4: Int,
    @ColumnInfo(name = "enemy_id_5") val enemy_id_5: Int,
    @ColumnInfo(name = "drop_gold_5") val drop_gold_5: Int,
    @ColumnInfo(name = "drop_reward_id_5") val drop_reward_id_5: Int,
    @ColumnInfo(name = "guest_enemy_id") val guest_enemy_id: Int,
)
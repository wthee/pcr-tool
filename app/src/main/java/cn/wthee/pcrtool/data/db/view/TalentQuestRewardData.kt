package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 深域关卡奖励
 */
data class TalentQuestRewardData(
    @ColumnInfo(name = "quest_id") var questId: Int = -1,
    @ColumnInfo(name = "quest_name") var questName: String = "",
    @ColumnInfo(name = "reward_id_2") val rewardId2: Int = 0,
    @ColumnInfo(name = "reward_num_2") val rewardNum2: Int = 0,
    @ColumnInfo(name = "reward_id_3") val rewardId3: Int = 0,
    @ColumnInfo(name = "reward_num_3") val rewardNum3: Int = 0,
)
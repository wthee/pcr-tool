package cn.wthee.pcrtool.data.model

import androidx.room.ColumnInfo


//最终信息
data class EquipmentDropInfo(
    val questId: Int,
    val eid: Int,
    val questName: String,
    val odds: List<EquipmentIdWithOdd>
) {
    fun getNum() = questName.split(" ")[1]

    fun getName() = questName.split(" ")[0]
}

//装备掉落关卡信息
data class EquipmentDropWaveID(
    @ColumnInfo(name = "quest_id") val questId: Int,
    @ColumnInfo(name = "quest_name") val questName: String,
    @ColumnInfo(name = "wave_group_id_1") val wave_group_id_1: Int,
    @ColumnInfo(name = "wave_group_id_2") val wave_group_id_2: Int,
    @ColumnInfo(name = "wave_group_id_3") val wave_group_id_3: Int
) {

    fun getWaveIds(): ArrayList<Int> {
        return arrayListOf(wave_group_id_1, wave_group_id_2, wave_group_id_3)
    }
}

data class EquipmentDropRewardID(
    @ColumnInfo(name = "drop_reward_id_1") val drop_reward_id_1: Int,
    @ColumnInfo(name = "drop_reward_id_2") val drop_reward_id_2: Int,
    @ColumnInfo(name = "drop_reward_id_3") val drop_reward_id_3: Int,
    @ColumnInfo(name = "drop_reward_id_4") val drop_reward_id_4: Int,
    @ColumnInfo(name = "drop_reward_id_5") val drop_reward_id_5: Int
) {

    fun getRewardIds(): ArrayList<Int> {
        val data = arrayListOf<Int>()
        if (drop_reward_id_1 != 0) data.add(drop_reward_id_1)
        if (drop_reward_id_2 != 0) data.add(drop_reward_id_2)
        if (drop_reward_id_3 != 0) data.add(drop_reward_id_3)
        if (drop_reward_id_4 != 0) data.add(drop_reward_id_4)
        if (drop_reward_id_5 != 0) data.add(drop_reward_id_5)
        return data
    }
}

data class EquipmentDropOdd(
    @ColumnInfo(name = "reward_id_1") val reward_id_1: Int,
    @ColumnInfo(name = "odds_1") val odds_1: Int,
    @ColumnInfo(name = "reward_id_2") val reward_id_2: Int,
    @ColumnInfo(name = "odds_2") val odds_2: Int,
    @ColumnInfo(name = "reward_id_3") val reward_id_3: Int,
    @ColumnInfo(name = "odds_3") val odds_3: Int,
    @ColumnInfo(name = "reward_id_4") val reward_id_4: Int,
    @ColumnInfo(name = "odds_4") val odds_4: Int,
    @ColumnInfo(name = "reward_id_5") val reward_id_5: Int,
    @ColumnInfo(name = "odds_5") val odds_5: Int
) {

    fun getOdds(): ArrayList<EquipmentIdWithOdd> {
        val odds = arrayListOf<EquipmentIdWithOdd>()
        if (odds_1 != 0 && reward_id_1 != 0) odds.add(EquipmentIdWithOdd(reward_id_1, odds_1))
        if (odds_2 != 0 && reward_id_2 != 0) odds.add(EquipmentIdWithOdd(reward_id_2, odds_2))
        if (odds_3 != 0 && reward_id_3 != 0) odds.add(EquipmentIdWithOdd(reward_id_3, odds_3))
        if (odds_4 != 0 && reward_id_4 != 0) odds.add(EquipmentIdWithOdd(reward_id_4, odds_4))
        if (odds_5 != 0 && reward_id_5 != 0) odds.add(EquipmentIdWithOdd(reward_id_5, odds_5))
        return odds
    }
}

data class EquipmentIdWithOdd(
    val eid: Int,
    val odd: Int
)

//合成信息
data class EquipmentMaterial(
    var id: Int,
    var name: String,
    var count: Int
)
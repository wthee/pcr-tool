package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.model.EquipmentDropOdd
import cn.wthee.pcrtool.data.model.EquipmentDropRewardID
import cn.wthee.pcrtool.data.model.EquipmentDropWaveID
import cn.wthee.pcrtool.data.model.entity.EquipmentCraft
import cn.wthee.pcrtool.data.model.entity.EquipmentData
import cn.wthee.pcrtool.data.model.entity.EquipmentEnhanceRate


//角色数据DAO
@Dao
interface EquipmentDao {

    //角色Rank所需装备信息
    @Query("SELECT * FROM equipment_data WHERE equipment_data.equipment_id IN (:eids) ")
    suspend fun getEquipmentDatas(eids: List<Int>): List<EquipmentData>

    //获取装备具体属性
    @Query("SELECT * FROM equipment_data WHERE equipment_data.equipment_id =:eid ")
    suspend fun getEquipmentData(eid: Int): EquipmentData

    //角色所有装备信息
    @Query("SELECT * FROM equipment_data WHERE equipment_data.equipment_name like '%' || :name || '%' AND equipment_name NOT LIKE '%公主之心%' ORDER BY promotion_level ASC")
    suspend fun getAllEquipments(name: String): List<EquipmentData>

    //装备提升属性
    @Query("SELECT * FROM equipment_enhance_rate WHERE equipment_enhance_rate.equipment_id = :eid ")
    suspend fun getEquipmentEnhanceData(eid: Int): EquipmentEnhanceRate

    //装备碎片信息
    @Query("SELECT * FROM equipment_craft WHERE equipment_craft.equipment_id = :eid ")
    suspend fun getEquipmentCraft(eid: Int): EquipmentCraft


    //关卡信息
    @Transaction
    @Query(
        "SELECT DISTINCT " +
                "quest_data.quest_id, " +
                "quest_data.quest_name, " +
                "quest_data.wave_group_id_1, " +
                "quest_data.wave_group_id_2, " +
                "quest_data.wave_group_id_3 " +
                "FROM " +
                "equipment_data " +
                "INNER JOIN wave_group_data ON enemy_reward_data.drop_reward_id = wave_group_data.drop_reward_id_1  " +
                "OR enemy_reward_data.drop_reward_id = wave_group_data.drop_reward_id_2  " +
                "OR enemy_reward_data.drop_reward_id = wave_group_data.drop_reward_id_3  " +
                "OR enemy_reward_data.drop_reward_id = wave_group_data.drop_reward_id_4  " +
                "OR enemy_reward_data.drop_reward_id = wave_group_data.drop_reward_id_5 " +
                "INNER JOIN quest_data ON wave_group_data.wave_group_id = quest_data.wave_group_id_1  " +
                "OR wave_group_data.wave_group_id = quest_data.wave_group_id_2  " +
                "OR wave_group_data.wave_group_id = quest_data.wave_group_id_3 " +
                "INNER JOIN enemy_reward_data ON equipment_data.equipment_id = enemy_reward_data.reward_id_1  " +
                "OR equipment_data.equipment_id = enemy_reward_data.reward_id_2  " +
                "OR equipment_data.equipment_id = enemy_reward_data.reward_id_3  " +
                "OR equipment_data.equipment_id = enemy_reward_data.reward_id_4  " +
                "OR equipment_data.equipment_id = enemy_reward_data.reward_id_5  " +
                "WHERE " +
                "equipment_data.equipment_id = :eid  " +
                "AND quest_data.quest_id < 18000000  " +
                "ORDER BY " +
                "quest_id ASC, " +
                "quest_data.quest_name ASC, " +
                "quest_data.quest_name ASC "
    )
    suspend fun getDropWaveID(eid: Int): List<EquipmentDropWaveID>

    //查找关卡掉落奖励id
    @Query(
        "SELECT " +
                "wave_group_data.drop_reward_id_1, " +
                "wave_group_data.drop_reward_id_2, " +
                "wave_group_data.drop_reward_id_3, " +
                "wave_group_data.drop_reward_id_4, " +
                "wave_group_data.drop_reward_id_5  " +
                "FROM " +
                "wave_group_data  " +
                "WHERE " +
                "wave_group_data.wave_group_id IN (:waveIds)"
    )
    suspend fun getDropRewardID(waveIds: List<Int>): List<EquipmentDropRewardID>

    //关卡道具掉落率
    @Query(
        "SELECT " +
                "enemy_reward_data.reward_id_1, " +
                "enemy_reward_data.odds_1, " +
                "enemy_reward_data.reward_id_2, " +
                "enemy_reward_data.odds_2, " +
                "enemy_reward_data.reward_id_3, " +
                "enemy_reward_data.odds_3, " +
                "enemy_reward_data.reward_id_4, " +
                "enemy_reward_data.odds_4, " +
                "enemy_reward_data.reward_id_5, " +
                "enemy_reward_data.odds_5  " +
                "FROM " +
                "enemy_reward_data  " +
                "WHERE " +
                "drop_reward_id IN (:rids)  " +
                "AND reward_id_1 > 100000"
    )
    suspend fun getOdds(rids: List<Int>): List<EquipmentDropOdd>
}
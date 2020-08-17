package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.model.entity.*


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

    @Query("SELECT * FROM enemy_reward_data WHERE drop_reward_id IN (:rids) AND reward_id_1 > 100000")
    suspend fun getRewardDatas(rids: List<Int>): List<EnemyRewardData>

    @Transaction
    @Query(
        "SELECT " +
                "quest_id, " +
                "quest_name, " +
                ":eid as eid, " +
                "reward_1 || '-' || reward_2 || '-' || reward_3 || '-' || reward_4 || '-' || reward_5 AS rewards, " +
                "odd_1 || '-' || odd_2 || '-' || odd_3 || '-' || odd_4 || '-' || odd_5 AS odds  " +
                "FROM " +
                "(SELECT DISTINCT " +
                "a.quest_id, " +
                "a.quest_name, " +
                "GROUP_CONCAT( c.reward_id_1, '-' ) AS reward_1, " +
                "GROUP_CONCAT( c.odds_1, '-' ) AS odd_1, " +
                "GROUP_CONCAT( c.reward_id_2, '-' ) AS reward_2, " +
                "GROUP_CONCAT( c.odds_2, '-' ) AS odd_2, " +
                "GROUP_CONCAT( c.reward_id_3, '-' ) AS reward_3, " +
                "GROUP_CONCAT( c.odds_3, '-' ) AS odd_3, " +
                "GROUP_CONCAT( c.reward_id_4, '-' ) AS reward_4, " +
                "GROUP_CONCAT( c.odds_4, '-' ) AS odd_4, " +
                "GROUP_CONCAT( c.reward_id_5, '-' ) AS reward_5, " +
                "GROUP_CONCAT( c.odds_5, '-' ) AS odd_5  " +
                "FROM " +
                "quest_data a " +
                "LEFT JOIN wave_group_data b ON b.wave_group_id IN ( a.wave_group_id_1, a.wave_group_id_2, a.wave_group_id_3 ) " +
                "LEFT JOIN enemy_reward_data c ON c.drop_reward_id IN ( b.drop_reward_id_1, b.drop_reward_id_2, b.drop_reward_id_3, b.drop_reward_id_4, b.drop_reward_id_5 )  " +
                "AND c.reward_id_1 > 100000  " +
                "WHERE " +
                "a.quest_id < 18000000  " +
                "GROUP BY " +
                "a.quest_id  " +
                "ORDER BY " +
                "a.quest_id ASC, " +
                "a.quest_name ASC )  " +
                "WHERE " +
                "rewards LIKE '%' || :eid || '%'"
    )
    suspend fun getEquipDropAreas(eid: Int): List<EquipmentDropInfo>

}
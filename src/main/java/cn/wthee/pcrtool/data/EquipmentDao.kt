package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.model.entity.*

//装备满属性视图
const val viewEquipmentMaxData = """
        SELECT
	a.equipment_id,
	a.equipment_name,
	COALESCE(b.description, "") AS type,
    a.promotion_level,
	a.description,
	a.craft_flg,
	COALESCE(( a.hp + b.hp * COALESCE(d.equipment_enhance_level, 0)), 0) AS hp ,
	COALESCE(( a.atk + b.atk * COALESCE(d.equipment_enhance_level, 0)), 0) AS atk ,
	COALESCE(( a.magic_str + b.magic_str * COALESCE(d.equipment_enhance_level, 0)), 0) AS magic_str ,
	COALESCE(( a.def + b.def * COALESCE(d.equipment_enhance_level, 0)), 0) AS def ,
	COALESCE(( a.magic_def + b.magic_def * COALESCE(d.equipment_enhance_level, 0)), 0) AS magic_def ,
	COALESCE(( a.physical_critical + b.physical_critical * COALESCE(d.equipment_enhance_level, 0)), 0) AS physical_critical ,
	COALESCE(( a.magic_critical + b.magic_critical * COALESCE(d.equipment_enhance_level, 0)), 0) AS magic_critical ,
	COALESCE(( a.wave_hp_recovery + b.wave_hp_recovery * COALESCE(d.equipment_enhance_level, 0)), 0) AS wave_hp_recovery ,
	COALESCE(( a.wave_energy_recovery + b.wave_energy_recovery * COALESCE(d.equipment_enhance_level, 0)), 0) AS wave_energy_recovery ,
	COALESCE(( a.dodge + b.dodge * COALESCE(d.equipment_enhance_level, 0)), 0) AS dodge ,
	COALESCE(( a.physical_penetrate + b.physical_penetrate * COALESCE(d.equipment_enhance_level, 0)), 0) AS physical_penetrate ,
	COALESCE(( a.magic_penetrate + b.magic_penetrate * COALESCE(d.equipment_enhance_level, 0)), 0) AS magic_penetrate ,
	COALESCE(( a.life_steal + b.life_steal * COALESCE(d.equipment_enhance_level, 0)), 0) AS life_steal ,
	COALESCE(( a.hp_recovery_rate + b.hp_recovery_rate * COALESCE(d.equipment_enhance_level, 0)), 0) AS hp_recovery_rate ,
	COALESCE(( a.energy_recovery_rate + b.energy_recovery_rate * COALESCE(d.equipment_enhance_level, 0)), 0) AS energy_recovery_rate ,
	COALESCE(( a.energy_reduce_rate + b.energy_reduce_rate * COALESCE(d.equipment_enhance_level, 0)), 0) AS energy_reduce_rate ,
	COALESCE(( a.accuracy + b.accuracy * COALESCE(d.equipment_enhance_level, 0)), 0) AS accuracy 
FROM
	equipment_data AS a
	LEFT OUTER JOIN equipment_enhance_rate AS b ON a.equipment_id = b.equipment_id
	LEFT OUTER JOIN equipment_enhance_data AS d ON b.promotion_level = d.promotion_level
    """


//角色数据DAO
@Dao
interface EquipmentDao {

    //角色Rank所需装备信息
    @Query("SELECT * FROM equipment_data WHERE equipment_data.equipment_id IN (:eids) ")
    suspend fun getEquipmentDatas(eids: List<Int>): List<EquipmentData>

    //所有装备信息
    @Query(viewEquipmentMaxData + "WHERE a.equipment_name like '%' || :name || '%' AND a.equipment_name NOT LIKE '%公主之心%' ORDER BY a.promotion_level DESC, a.equipment_id DESC")
    suspend fun getAllEquipments(name: String): List<EquipmentMaxData>

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
        """
        SELECT 
            quest_id, 
            quest_name, 
            :eid as eid, 
            reward_1 || '-' || reward_2 || '-' || reward_3 || '-' || reward_4 || '-' || reward_5 AS rewards, 
            odd_1 || '-' || odd_2 || '-' || odd_3 || '-' || odd_4 || '-' || odd_5 AS odds  
        FROM 
            (SELECT DISTINCT 
                a.quest_id, 
                a.quest_name, 
                GROUP_CONCAT( c.reward_id_1, '-' ) AS reward_1, 
                GROUP_CONCAT( c.odds_1, '-' ) AS odd_1, 
                GROUP_CONCAT( c.reward_id_2, '-' ) AS reward_2, 
                GROUP_CONCAT( c.odds_2, '-' ) AS odd_2, 
                GROUP_CONCAT( c.reward_id_3, '-' ) AS reward_3, 
                GROUP_CONCAT( c.odds_3, '-' ) AS odd_3, 
                GROUP_CONCAT( c.reward_id_4, '-' ) AS reward_4, 
                GROUP_CONCAT( c.odds_4, '-' ) AS odd_4, 
                GROUP_CONCAT( c.reward_id_5, '-' ) AS reward_5, 
                GROUP_CONCAT( c.odds_5, '-' ) AS odd_5  
            FROM 
                quest_data a 
            LEFT JOIN wave_group_data b ON b.wave_group_id IN ( a.wave_group_id_1, a.wave_group_id_2, a.wave_group_id_3 ) 
            LEFT JOIN enemy_reward_data c ON c.drop_reward_id IN ( b.drop_reward_id_1, b.drop_reward_id_2, b.drop_reward_id_3, b.drop_reward_id_4, b.drop_reward_id_5 )  
            AND c.reward_id_1 > 100000  
            WHERE 
                a.quest_id < 18000000  
                GROUP BY 
                a.quest_id  
                ORDER BY 
                a.quest_id ASC, 
                a.quest_name ASC )  
        WHERE 
            rewards LIKE '%' || :eid || '%'"""
    )
    suspend fun getEquipDropAreas(eid: Int): List<EquipmentDropInfo>

    @Transaction
    @Query(viewEquipmentMaxData + " WHERE a.equipment_id =:eid")
    suspend fun getEquipInfos(eid: Int): EquipmentMaxData
}
package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.*

//装备满属性视图
const val viewEquipmentMaxData = """
        SELECT
	a.equipment_id,
	a.equipment_name,
	COALESCE( b.description, "" ) AS type,
	a.promotion_level,
	a.description,
	a.craft_flg,
	a.require_level,
	COALESCE(( a.hp + b.hp * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS hp,
	COALESCE(( a.atk + b.atk * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS atk,
	COALESCE(( a.magic_str + b.magic_str * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS magic_str,
	COALESCE(( a.def + b.def * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS def,
	COALESCE(( a.magic_def + b.magic_def * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS magic_def,
	COALESCE(( a.physical_critical + b.physical_critical * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS physical_critical,
	COALESCE(( a.magic_critical + b.magic_critical * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS magic_critical,
	COALESCE(( a.wave_hp_recovery + b.wave_hp_recovery * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS wave_hp_recovery,
	COALESCE(( a.wave_energy_recovery + b.wave_energy_recovery * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS wave_energy_recovery,
	COALESCE(( a.dodge + b.dodge * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS dodge,
	COALESCE(( a.physical_penetrate + b.physical_penetrate * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS physical_penetrate,
	COALESCE(( a.magic_penetrate + b.magic_penetrate * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS magic_penetrate,
	COALESCE(( a.life_steal + b.life_steal * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS life_steal,
	COALESCE(( a.hp_recovery_rate + b.hp_recovery_rate * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS hp_recovery_rate,
	COALESCE(( a.energy_recovery_rate + b.energy_recovery_rate * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS energy_recovery_rate,
	COALESCE(( a.energy_reduce_rate + b.energy_reduce_rate * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS energy_reduce_rate,
	COALESCE(( a.accuracy + b.accuracy * COALESCE( d.equipment_enhance_level, 0 )), 0 ) AS accuracy 
FROM
	equipment_data AS a
	LEFT OUTER JOIN equipment_enhance_rate AS b ON a.equipment_id = b.equipment_id
	LEFT OUTER JOIN (SELECT e.promotion_level, MAX( e.equipment_enhance_level ) AS equipment_enhance_level FROM equipment_enhance_data AS e GROUP BY promotion_level) AS d ON a.promotion_level = d.promotion_level
 """

//装备筛选
const val equipWhere = """
    WHERE a.equipment_name like '%' || :name || '%' 
        AND (
            (a.equipment_id IN (:starIds) AND  1 = CASE WHEN  0 = :showAll  THEN 1 END) 
            OR 
            (1 = CASE WHEN  1 = :showAll  THEN 1 END)
        )
        AND a.equipment_id < 140000 
        AND 1 = CASE
            WHEN  '全部' = :type  THEN 1 
            WHEN  b.description = :type  THEN 1 
        END
        AND 1 = CASE
            WHEN  a.craft_flg = :craft  THEN 1 
        END
    ORDER BY  a.require_level DESC
    LIMIT :limit
    """

/**
 * 装备数据DAO
 */
@Dao
interface EquipmentDao {

    /**
     * 获取装备所有类型列表
     */
    @SkipQueryVerification
    @Query("""SELECT description as type FROM equipment_enhance_rate GROUP BY description""")
    suspend fun getEquipTypes(): List<String>

    /**
     * 根据筛选条件获取所有装备分页信息 [EquipmentMaxData]
     * @param craft -1：全部，0：素材：1：装备
     * @param type 装备类型
     * @param name 装备名称
     * @param showAll 0: 仅收藏，1：全部
     * @param starIds 收藏的装备编号
     */
    @SkipQueryVerification
    @Transaction
    @Query("""$viewEquipmentMaxData  $equipWhere""")
    suspend fun getEquipments(
        craft: Int,
        type: String,
        name: String,
        showAll: Int,
        starIds: List<Int>,
        limit: Int
    ): List<EquipmentMaxData>

    /**
     * 获取数量
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            COUNT(*)
        FROM
            equipment_data AS a
        LEFT OUTER JOIN equipment_enhance_rate AS b ON a.equipment_id = b.equipment_id
        LEFT OUTER JOIN (SELECT e.promotion_level, MAX( e.equipment_enhance_level ) AS equipment_enhance_level FROM equipment_enhance_data AS e GROUP BY promotion_level) AS d ON a.promotion_level = d.promotion_level
        WHERE a.equipment_id < 140000 AND a.craft_flg = 1
    """
    )
    suspend fun getCount(): Int

    /**
     * 获取装备提升属性
     * @param equipId 装备编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM equipment_enhance_rate WHERE equipment_enhance_rate.equipment_id = :equipId ")
    suspend fun getEquipmentEnhanceData(equipId: Int): EquipmentEnhanceRate

    /**
     * 获取装备合成信息
     * @param equipId 装备编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM equipment_craft WHERE equipment_craft.equipment_id = :equipId ")
    suspend fun getEquipmentCraft(equipId: Int): EquipmentCraft

    /**
     * 获取装备掉落区域信息
     * @param equipId 装备编号
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT 
            quest_id, 
            quest_name, 
            :equipId as equip_id, 
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
            LEFT JOIN enemy_reward_data c ON c.drop_reward_id IN ( b.drop_reward_id_1, b.drop_reward_id_2, b.drop_reward_id_3, b.drop_reward_id_4, b.drop_reward_id_5 ) AND c.reward_id_1 > 100000  
            WHERE 
                a.quest_id < 18000000
            GROUP BY 
                a.quest_id  
            ORDER BY 
                a.quest_id ASC, a.quest_name ASC 
            )  
        WHERE 
            rewards LIKE '%' || :equipId || '%'"""
    )
    suspend fun getEquipDropAreas(equipId: Int): List<EquipmentDropInfo>

    /**
     * 获取装备数值信息
     * @param equipId 装备编号
     */
    @SkipQueryVerification
    @Transaction
    @Query("$viewEquipmentMaxData WHERE a.equipment_id =:equipId")
    suspend fun getEquipInfos(equipId: Int): EquipmentMaxData

    /**
     * 获取专武信息
     * @param unitId 角色编号
     * @param lv 装备等级
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            c.unit_id,
            a.equipment_id,
            a.equipment_name,
            a.description,
            (a.hp + b.hp * COALESCE( :lv - 1, 0 )) AS hp,
            ( a.atk + b.atk * COALESCE( :lv - 1, 0 ) ) AS atk,
            ( a.magic_str + b.magic_str * COALESCE( :lv - 1, 0 ) ) AS magic_str,
            ( a.def + b.def * COALESCE( :lv - 1, 0 ) ) AS def,
            ( a.magic_def + b.magic_def * COALESCE( :lv - 1, 0 ) ) AS magic_def,
            ( a.physical_critical + b.physical_critical * COALESCE( :lv - 1, 0 ) ) AS physical_critical,
            ( a.magic_critical + b.magic_critical * COALESCE( :lv - 1, 0 ) ) AS magic_critical,
            ( a.wave_hp_recovery + b.wave_hp_recovery * COALESCE( :lv - 1, 0 ) ) AS wave_hp_recovery,
            ( a.wave_energy_recovery + b.wave_energy_recovery * COALESCE( :lv - 1, 0 ) ) AS wave_energy_recovery,
            ( a.dodge + b.dodge * COALESCE( :lv - 1, 0 ) ) AS dodge,
            ( a.physical_penetrate + b.physical_penetrate * COALESCE( :lv - 1, 0 ) ) AS physical_penetrate,
            ( a.magic_penetrate + b.magic_penetrate * COALESCE( :lv - 1, 0 ) ) AS magic_penetrate,
            ( a.life_steal + b.life_steal * COALESCE( :lv - 1, 0 ) ) AS life_steal,
            ( a.hp_recovery_rate + b.hp_recovery_rate * COALESCE( :lv - 1, 0 ) ) AS hp_recovery_rate,
            ( a.energy_recovery_rate + b.energy_recovery_rate * COALESCE( :lv - 1, 0 ) ) AS energy_recovery_rate,
            ( a.energy_reduce_rate + b.energy_reduce_rate * COALESCE( :lv - 1, 0 ) ) AS energy_reduce_rate,
            ( a.accuracy + b.accuracy * COALESCE( :lv - 1, 0 ) ) AS accuracy 
        FROM
            unit_data AS c
            LEFT OUTER JOIN unique_equipment_data AS a ON c.unit_id < 200000 
            AND SUBSTR( c.unit_id, 2, 3 ) = SUBSTR( a.equipment_id, 3, 3 )
            LEFT OUTER JOIN unique_equipment_enhance_rate AS b ON a.equipment_id = b.equipment_id
        WHERE
            a.equipment_id IS NOT NULL AND unit_id =:unitId
    """
    )
    suspend fun getUniqueEquipInfos(unitId: Int, lv: Int): UniqueEquipmentMaxData?

    /**
     * 根获取专武最大强化等级
     */
    @SkipQueryVerification
    @Transaction
    @Query(" SELECT MAX( unique_equipment_enhance_data.enhance_level ) FROM unique_equipment_enhance_data")
    suspend fun getUniqueEquipMaxLv(): Int

    /**
     * 获取角色  Rank 范围所需的装备
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            GROUP_CONCAT( equip_slot_1, '-' ) AS equip_1,
            GROUP_CONCAT( equip_slot_2, '-' ) AS equip_2,
            GROUP_CONCAT( equip_slot_3, '-' ) AS equip_3,
            GROUP_CONCAT( equip_slot_4, '-' ) AS equip_4,
            GROUP_CONCAT( equip_slot_5, '-' ) AS equip_5,
            GROUP_CONCAT( equip_slot_6, '-' ) AS equip_6 
        FROM
            unit_promotion 
        WHERE
            unit_id =  :unitId AND promotion_level >= :startRank AND promotion_level <= :endRank
        GROUP BY
            unit_id
    """
    )
    suspend fun getEquipByRank(unitId: Int, startRank: Int, endRank: Int): CharacterPromotionEquip

    /**
     * 获取所有角色  Rank 范围所需的装备
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            GROUP_CONCAT( equip_slot_1, '-' ) AS equip_1,
            GROUP_CONCAT( equip_slot_2, '-' ) AS equip_2,
            GROUP_CONCAT( equip_slot_3, '-' ) AS equip_3,
            GROUP_CONCAT( equip_slot_4, '-' ) AS equip_4,
            GROUP_CONCAT( equip_slot_5, '-' ) AS equip_5,
            GROUP_CONCAT( equip_slot_6, '-' ) AS equip_6 
        FROM
            unit_promotion 
        WHERE
            promotion_level >= 1 AND equip_slot_1 != 0
        GROUP BY
            unit_id
    """
    )
    suspend fun getAllEquip(): List<CharacterPromotionEquip>


    /**
     * 获取角色各 RANK 装备信息
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM unit_promotion WHERE unit_id = :unitId ORDER BY promotion_level DESC")
    suspend fun getAllRankEquip(unitId: Int): List<UnitPromotion>

    /**
     * 获取已开放的最新区域
     */
    @SkipQueryVerification
    @Query("SELECT MAX(area_id) FROM quest_data WHERE area_id < 12000")
    suspend fun getMaxArea(): Int
}
package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.*

//装备满属性视图
private const val viewEquipmentMaxData = """
        SELECT
	a.equipment_id,
	a.equipment_name,
	COALESCE( b.description, '') AS type,
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

/**
 * 装备数据DAO
 */
@Dao
interface EquipmentDao {

    /**
     * 根据筛选条件获取所有装备分页信息 [EquipmentMaxData]
     * @param craft -1：全部，0：素材：1：装备
     * @param colorType 装备品级
     * @param name 装备名称
     * @param showAll 0: 仅收藏，1：全部
     * @param starIds 收藏的装备编号
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
         SELECT
            a.equipment_id,
            a.equipment_name,
            a.craft_flg,
            a.promotion_level,
            a.require_level
        FROM
            equipment_data AS a
        WHERE a.equipment_name like '%' || :name || '%' 
            AND (
                (a.equipment_id IN (:starIds) AND  1 = CASE WHEN  0 = :showAll  THEN 1 END) 
                OR 
                (1 = CASE WHEN  1 = :showAll  THEN 1 END)
            )
            AND a.equipment_id < 140000
            AND 1 = CASE
                WHEN  0 = :colorType  THEN 1 
                WHEN  a.promotion_level = :colorType  THEN 1 
            END
            AND 1 = CASE
                WHEN  a.craft_flg = :craft THEN 1 
                WHEN  a.craft_flg <> :craft AND a.craft_flg = 0 AND a.promotion_level = 1 THEN 1 
            END
        ORDER BY a.promotion_level DESC, a.require_level DESC
        LIMIT :limit
    """
    )
    suspend fun getEquipments(
        craft: Int,
        colorType: Int,
        name: String,
        showAll: Int,
        starIds: List<Int>,
        limit: Int
    ): List<EquipmentBasicInfo>

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
        WHERE a.equipment_id < 140000 AND (a.craft_flg = 1 OR (a.craft_flg = 0 AND a.promotion_level = 1))
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
     * 获取装备基本信息
     * @param equipId 装备编号
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            a.equipment_id,
            a.equipment_name,
            a.craft_flg,
            a.promotion_level,
            a.require_level
        FROM
            equipment_data AS a
        WHERE a.equipment_id =:equipId
    """
    )
    suspend fun getEquipBasicInfo(equipId: Int): EquipmentBasicInfo

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
            r.unit_id,
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
            ( a.accuracy + b.accuracy * COALESCE( :lv - 1, 0 ) ) AS accuracy ,
            0 AS isTpLimitAction
        FROM
            unit_unique_equip AS r
            LEFT OUTER JOIN unique_equipment_data AS a ON r.equip_id = a.equipment_id
            LEFT OUTER JOIN unique_equipment_enhance_rate AS b ON a.equipment_id = b.equipment_id
        WHERE
            a.equipment_id IS NOT NULL AND r.unit_id = :unitId
    """
    )
    suspend fun getUniqueEquipInfos(unitId: Int, lv: Int): UniqueEquipmentMaxData?

    /**
     * 获取专武信息V2，日服专武提升表已更新：unique_equipment_enhance_rate -> unique_equip_enhance_rate
     * @param unitId 角色编号
     * @param lv 装备等级
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            r.unit_id,
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
            ( a.accuracy + b.accuracy * COALESCE( :lv - 1, 0 ) ) AS accuracy,
            0 AS isTpLimitAction
        FROM
            unit_unique_equip AS r
            LEFT OUTER JOIN unique_equipment_data AS a ON r.equip_id = a.equipment_id
            LEFT OUTER JOIN unique_equip_enhance_rate AS b ON a.equipment_id = b.equipment_id
        WHERE
            a.equipment_id IS NOT NULL AND r.unit_id = :unitId AND b.min_lv = 2
    """
    )
    suspend fun getUniqueEquipInfosV2(unitId: Int, lv: Int): UniqueEquipmentMaxData?

    /**
     * 获取专武信息（等级大于260）
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            ( a.hp * COALESCE( :lv, 0 ) ) AS hp,
            ( a.atk * COALESCE( :lv, 0 ) ) AS atk,
            ( a.magic_str * COALESCE( :lv, 0 ) ) AS magic_str,
            ( a.def * COALESCE( :lv, 0 ) ) AS def,
            ( a.magic_def * COALESCE( :lv, 0 ) ) AS magic_def,
            ( a.physical_critical * COALESCE( :lv, 0 ) ) AS physical_critical,
            ( a.magic_critical * COALESCE( :lv, 0 ) ) AS magic_critical,
            ( a.wave_hp_recovery * COALESCE( :lv, 0 ) ) AS wave_hp_recovery,
            ( a.wave_energy_recovery * COALESCE( :lv, 0 ) ) AS wave_energy_recovery,
            ( a.dodge * COALESCE( :lv, 0 ) ) AS dodge,
            ( a.physical_penetrate * COALESCE( :lv, 0 ) ) AS physical_penetrate,
            ( a.magic_penetrate * COALESCE( :lv, 0 ) ) AS magic_penetrate,
            ( a.life_steal * COALESCE( :lv, 0 ) ) AS life_steal,
            ( a.hp_recovery_rate * COALESCE( :lv, 0 ) ) AS hp_recovery_rate,
            ( a.energy_recovery_rate * COALESCE( :lv, 0 ) ) AS energy_recovery_rate,
            ( a.energy_reduce_rate * COALESCE( :lv, 0 ) ) AS energy_reduce_rate,
            ( a.accuracy * COALESCE( :lv, 0 ) ) AS accuracy
        FROM
            unique_equip_enhance_rate AS a
            LEFT JOIN unit_unique_equip AS r ON r.equip_id = a.equipment_id
        WHERE r.unit_id = :unitId AND a.min_lv = 261
    """
    )
    suspend fun getUniqueEquipBonus(unitId: Int, lv: Int): Attr?

    /**
     * 根获取专武最大强化等级
     */
    @SkipQueryVerification
    @Transaction
    @Query(" SELECT MAX( unique_equipment_enhance_data.enhance_level ) FROM unique_equipment_enhance_data")
    suspend fun getUniqueEquipMaxLv(): Int

    /**
     * 获取所有角色所需的装备统计
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            equip_slot AS equip_id,
            COUNT( equip_slot ) AS equip_count
        FROM
            (
            SELECT
                unit_id,
                promotion_level,
                equip_slot_1 AS equip_slot 
            FROM
                unit_promotion 
            WHERE
                promotion_level >= 1 
                AND unit_id < $maxUnitId
                AND equip_slot_1 != 0 UNION
            SELECT
                unit_id,
                promotion_level,
                equip_slot_2 AS equip_slot 
            FROM
                unit_promotion 
            WHERE
                promotion_level >= 1 
                AND unit_id < $maxUnitId
                AND equip_slot_2 != 0 UNION
            SELECT
                unit_id,
                promotion_level,
                equip_slot_3 AS equip_slot 
            FROM
                unit_promotion 
            WHERE
                promotion_level >= 1 
                AND unit_id < $maxUnitId
                AND equip_slot_3 != 0 UNION
            SELECT
                unit_id,
                promotion_level,
                equip_slot_4 AS equip_slot 
            FROM
                unit_promotion 
            WHERE
                promotion_level >= 1 
                AND unit_id < $maxUnitId
                AND equip_slot_4 != 0 UNION
            SELECT
                unit_id,
                promotion_level,
                equip_slot_5 AS equip_slot 
            FROM
                unit_promotion 
            WHERE
                promotion_level >= 1 
                AND unit_id < $maxUnitId
                AND equip_slot_5 != 0 UNION
            SELECT
                unit_id,
                promotion_level,
                equip_slot_6 AS equip_slot 
            FROM
                unit_promotion 
            WHERE
                promotion_level >= 1 
                AND equip_slot_6 != 0 
                AND unit_id < $maxUnitId
            ) 
        WHERE 
            1 = CASE
                WHEN 0 = :unitId THEN 1
                WHEN unit_id = :unitId THEN 1
            END
            AND promotion_level >= :startRank AND promotion_level <= :endRank
        GROUP BY
            equip_slot
    """
    )
    suspend fun getEquipByRank(
        unitId: Int,
        startRank: Int,
        endRank: Int
    ): List<CharacterPromotionEquipCount>


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

    /**
     * 获取rank颜色数
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            MAX( promotion_level ) AS maxTypeNum 
        FROM
            equipment_data 
        WHERE
            equipment_id < 140000
    """
    )
    suspend fun getEquipColorNum(): Int

    /**
     * 获取最大rank
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            MAX( promotion_level ) AS maxRank
        FROM
            unit_promotion 
        WHERE
            unit_id = 100101
    """
    )
    suspend fun getMaxRank(): Int

    /**
     * 装备适用角色
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            unit_id
        FROM
            unit_promotion 
        WHERE
            (equip_slot_1 = :equipId 
            OR equip_slot_2 = :equipId 
            OR equip_slot_3 = :equipId 
            OR equip_slot_4 = :equipId 
            OR equip_slot_5 = :equipId 
            OR equip_slot_6 = :equipId)
            AND unit_id < 400000
            GROUP BY unit_id
        """
    )
    suspend fun getEquipUnitList(equipId: Int): List<Int>

}
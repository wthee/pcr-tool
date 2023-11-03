package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.CharacterExtraEquipData
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.ExtraEquipCategoryData
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipSubRewardData
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentBasicInfo
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentData
import cn.wthee.pcrtool.data.db.view.ExtraTravelData

/**
 * ex装备数据DAO
 */
@Dao
interface ExtraEquipmentDao {

    /**
     * 获取装备详情信息
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            a.ex_equipment_id AS equipment_id,
            a.name AS equipment_name,
            a.category,
            b.category_name,
            a.description,
            a.rarity,
            a.clan_battle_equip_flag,
            a.max_hp AS hp,
            a.max_atk AS atk,
            a.max_magic_str AS magic_str,
            a.max_def AS def,
            a.max_magic_def AS magic_def,
            a.max_physical_critical AS physical_critical,
            a.max_magic_critical AS magic_critical,
            a.max_wave_hp_recovery AS wave_hp_recovery,
            a.max_wave_energy_recovery AS wave_energy_recovery,
            a.max_dodge AS dodge,
            a.max_physical_penetrate AS physical_penetrate,
            a.max_magic_penetrate AS magic_penetrate,
            a.max_life_steal AS life_steal,
            a.max_hp_recovery_rate AS hp_recovery_rate,
            a.max_energy_recovery_rate AS energy_recovery_rate,
            a.max_energy_reduce_rate AS energy_reduce_rate,
            a.max_accuracy AS accuracy,
            a.default_hp,
            a.default_atk,
            a.default_magic_str,
            a.default_def,
            a.default_magic_def,
            a.default_physical_critical,
            a.default_magic_critical,
            a.default_wave_hp_recovery,
            a.default_wave_energy_recovery,
            a.default_dodge,
            a.default_physical_penetrate,
            a.default_magic_penetrate,
            a.default_life_steal,
            a.default_hp_recovery_rate,
            a.default_energy_recovery_rate,
            a.default_energy_reduce_rate,
            a.default_accuracy,
            a.passive_skill_id_1,
            a.passive_skill_id_2,
            a.passive_skill_power 
        FROM
            ex_equipment_data AS a
            LEFT JOIN ex_equipment_category AS b ON a.category = b.category
        WHERE a.ex_equipment_id = :equipId
    """
    )
    suspend fun getEquipInfo(equipId: Int): ExtraEquipmentData

    /**
     * 根据筛选条件获取所有装备分页信息 [EquipmentMaxData]
     * @param flag 0：全部，1：普通：2：会战
     * @param rarity 装备品级
     * @param name 装备名称
     * @param showAll 0: 仅收藏，1：全部
     * @param starIds 收藏的装备编号
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
         SELECT
            a.ex_equipment_id,
            a.name,
            a.clan_battle_equip_flag,
            a.rarity,
            b.category,
            b.category_name
        FROM
            ex_equipment_data AS a
            LEFT JOIN ex_equipment_category AS b ON a.category = b.category
        WHERE a.name like '%' || :name || '%'
            AND 1 = CASE
                WHEN  0 = :category  THEN 1 
                WHEN  a.category = :category  THEN 1 
            END
            AND 1 = CASE
                WHEN  0 = :rarity  THEN 1 
                WHEN  a.rarity = :rarity  THEN 1 
            END
            AND 1 = CASE
                WHEN  0 = :flag  THEN 1 
                WHEN  a.clan_battle_equip_flag + 1 = :flag THEN 1 
            END
        ORDER BY a.rarity DESC, a.category ASC
        LIMIT :limit
    """
    )
    suspend fun getEquipments(
        flag: Int,
        rarity: Int,
        name: String,
        category: Int,
        limit: Int
    ): List<ExtraEquipmentBasicInfo>

    /**
     * 获取装备颜色种类数
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            MAX( rarity ) AS maxTypeNum 
        FROM
            ex_equipment_data 
    """
    )
    suspend fun getEquipColorNum(): Int

    /**
     * 获取装备类别
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            category,
            category_name
        FROM
            ex_equipment_category 
        ORDER BY category
    """
    )
    suspend fun getEquipCategoryList(): List<ExtraEquipCategoryData>


    /**
     * 获取可使用装备的角色列表
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            b.unit_id
        FROM
            unit_ex_equipment_slot AS a
            LEFT JOIN unit_data AS b ON a.unit_id = b.unit_id 
        WHERE
            a.slot_category_1 = :category 
            OR a.slot_category_2 = :category 
            OR a.slot_category_3 = :category
        ORDER BY b.unit_name
    """
    )
    suspend fun getEquipUnitList(category: Int): List<Int>

    /**
     * 装备掉落信息
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            a.travel_quest_id,
            a.travel_area_id,
            a.travel_quest_name,
            a.travel_time,
            a.travel_time_decrease_limit,
            a.travel_decrease_flag,
            a.need_power,
            a.icon_id,
            a.limit_unit_num
        FROM
            travel_quest_data AS a
            LEFT JOIN travel_quest_sub_reward AS b ON a.travel_quest_id = b.travel_quest_id 
        WHERE
            b.reward_id = :equipId 
        GROUP BY
            a.travel_quest_id
        """
    )
    suspend fun getDropQuestList(equipId: Int): List<ExtraEquipQuestData>

    /**
     * 次要掉落信息
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            a.travel_quest_id,
            b.category,
            c.category_name,
            GROUP_CONCAT( b.ex_equipment_id, '-' ) AS sub_reward_ids,
            GROUP_CONCAT( a.disp_order, '-' ) AS sub_reward_drops 
        FROM
            travel_quest_sub_reward AS a
            LEFT JOIN ex_equipment_data AS b ON a.reward_id = b.ex_equipment_id
            LEFT JOIN ex_equipment_category AS c ON b.category = c.category 
        WHERE
            a.reward_type = 18 
            AND a.travel_quest_id = :questId 
        GROUP BY
            b.category
        """
    )
    suspend fun getSubRewardList(questId: Int): List<ExtraEquipSubRewardData>

    /**
     * ex冒险区域
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            a.travel_area_id,
            a.travel_area_name,
            COUNT( b.travel_quest_id ) AS quest_count,
            GROUP_CONCAT( b.travel_quest_id, '-' ) AS quest_ids,
            GROUP_CONCAT( b.travel_quest_name, '-' ) AS quest_names 
        FROM
            travel_area_data AS a
            LEFT JOIN travel_quest_data AS b ON a.travel_area_id = b.travel_area_id 
        GROUP BY
            a.travel_area_id
        """
    )
    suspend fun getTravelAreaList(): List<ExtraTravelData>


    /**
     * 冒险区域详情
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            a.travel_quest_id,
            a.travel_area_id,
            a.travel_quest_name,
            a.travel_time,
            a.travel_time_decrease_limit,
            a.travel_decrease_flag,
            a.need_power,
            a.icon_id,
            a.limit_unit_num
        FROM
            travel_quest_data AS a
        WHERE
            a.travel_quest_id = :questId
        """
    )
    suspend fun getTravelQuest(questId: Int): ExtraEquipQuestData

    /**
     * 冒险区域详情列表
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            a.travel_quest_id,
            a.travel_area_id,
            a.travel_quest_name,
            a.travel_time,
            a.travel_time_decrease_limit,
            a.travel_decrease_flag,
            a.need_power,
            a.icon_id,
            a.limit_unit_num
        FROM
            travel_quest_data AS a
        WHERE
            a.travel_area_id = :areaId
        """
    )
    suspend fun getTravelQuestList(areaId: Int): List<ExtraEquipQuestData>

    /**
     * 获取角色可使用的ex装备列表
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            a.unit_id,
            b.category,
            c.category_name,
            GROUP_CONCAT( b.ex_equipment_id, '-' ) AS ex_equipment_ids 
        FROM
            unit_ex_equipment_slot AS a
            LEFT JOIN ex_equipment_data AS b ON b.category IN ( a.slot_category_1, a.slot_category_2, a.slot_category_3 )
            LEFT JOIN ex_equipment_category AS c ON b.category = c.category 
        WHERE
            a.unit_id = :unitId 
        GROUP BY
            b.category
    """
    )
    suspend fun getCharacterExtraEquipList(unitId: Int): List<CharacterExtraEquipData>

    /**
     * 获取所有装备技能id
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            a.passive_skill_id_1 AS skill_id 
        FROM
            ex_equipment_data AS a 
        WHERE
            a.passive_skill_id_1 <> 0 
        GROUP BY
            a.passive_skill_id_1 UNION
        SELECT
            a.passive_skill_id_2 AS skill_id 
        FROM
            ex_equipment_data AS a 
        WHERE
            a.passive_skill_id_2 <> 0 
        GROUP BY
            a.passive_skill_id_2
    """
    )
    suspend fun getAllEquipSkillIdList(): List<Int>
}
package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentBasicInfo

/**
 * ex装备数据DAO
 */
@Dao
interface ExtraEquipmentDao {

    /**
     * 根据筛选条件获取所有装备分页信息 [EquipmentMaxData]
     * @param flag -1：全部，0：普通：1：会战
     * @param type 装备类型
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
            b.category_name
        FROM
            ex_equipment_data AS a
        WHERE a.name like '%' || :name || '%' 
            AND (
                (a.ex_equipment_id IN (:starIds) AND  1 = CASE WHEN  0 = :showAll  THEN 1 END) 
                OR 
                (1 = CASE WHEN  1 = :showAll  THEN 1 END)
            )
            AND a.ex_equipment_id < 140000
            AND 1 = CASE
                WHEN  0 = :type  THEN 1 
                WHEN  a.category = :type  THEN 1 
            END
            AND 1 = CASE
                WHEN  0 = :flag  THEN 1 
                WHEN  a.clan_battle_equip_flag = :flag THEN 1 
            END
        ORDER BY a.rarity DESC, a.category ASC
        LIMIT :limit
    """
    )
    suspend fun getEquipments(
        flag: Int,
        type: Int,
        name: String,
        showAll: Int,
        starIds: List<Int>,
        limit: Int
    ): List<ExtraEquipmentBasicInfo>

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
}
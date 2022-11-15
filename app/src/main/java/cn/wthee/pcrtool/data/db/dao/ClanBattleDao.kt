package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.ClanBattleTargetCountData
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro


/**
 * 公会战 DAO
 */
@Dao
interface ClanBattleDao {

    /**
     * 获取怪物基本参数
     * @param enemyId 怪物编号
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            enemy_parameter.*,
            unit_enemy_data.normal_atk_cast_time,
            unit_enemy_data.comment 
        FROM
            enemy_parameter
            LEFT JOIN unit_enemy_data ON enemy_parameter.unit_id = unit_enemy_data.unit_id
        WHERE enemy_id = :enemyId"""
    )
    suspend fun getBossAttr(enemyId: Int): EnemyParameterPro

    /**
     * 获取所有 Boss 信息，测试用
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            enemy_parameter.*,
            unit_enemy_data.normal_atk_cast_time,
            COALESCE(unit_enemy_data.comment, "") AS comment
        FROM
            enemy_parameter
            LEFT JOIN unit_enemy_data ON enemy_parameter.unit_id = unit_enemy_data.unit_id
        WHERE enemy_id > 400000000
        """
    )
    suspend fun getAllBossAttr(): List<EnemyParameterPro>


    /**
     * 获取所有公会战信息
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            a.clan_battle_id,
            b.release_month,
            b.start_time,
            COUNT(a.lap_num_from) / 5 as max_phase,
            GROUP_CONCAT(e.enemy_id, '-') as enemy_ids,
            GROUP_CONCAT(f.unit_id, '-') as unit_ids
        FROM
            clan_battle_2_map_data AS a
            LEFT JOIN clan_battle_schedule AS b ON b.clan_battle_id = a.clan_battle_id
            LEFT JOIN wave_group_data AS c ON c.wave_group_id IN ( a.wave_group_id_1, a.wave_group_id_2, a.wave_group_id_3, a.wave_group_id_4, a.wave_group_id_5 )
            LEFT JOIN enemy_parameter as e on c.enemy_id_1 = e.enemy_id
            LEFT JOIN unit_enemy_data as f on e.unit_id = f.unit_id
        WHERE
            (a.lap_num_from > 1 OR a.clan_battle_id < 1011)
            AND b.release_month IS NOT NULL
            AND 1 = CASE
            WHEN  0 = :clanBattleId  THEN 1 
            WHEN  a.clan_battle_id = :clanBattleId  THEN 1 
            END
        GROUP BY 
            a.clan_battle_id 
        ORDER BY
            a.clan_battle_id DESC,
            a.lap_num_from
    """
    )
    suspend fun getAllClanBattleData(clanBattleId: Int): List<ClanBattleInfo>


    /**
     * 获取所有公会战多目标
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            a.clan_battle_id,
            c.enemy_id_1 as multi_enemy_id,
            (
                d.child_enemy_parameter_1 || '-' || d.child_enemy_parameter_2 || '-' || d.child_enemy_parameter_3 || '-' || d.child_enemy_parameter_4 || '-' || d.child_enemy_parameter_5 
            ) AS enemy_part_ids 
        FROM
            clan_battle_2_map_data AS a
            LEFT JOIN clan_battle_schedule AS b ON b.clan_battle_id = a.clan_battle_id
            LEFT JOIN wave_group_data AS c ON c.wave_group_id IN ( a.wave_group_id_1, a.wave_group_id_2, a.wave_group_id_3, a.wave_group_id_4, a.wave_group_id_5 )
            LEFT JOIN enemy_m_parts AS d ON c.enemy_id_1 = d.enemy_id 
        WHERE
            ( a.lap_num_from > 1 OR a.clan_battle_id < 1011 ) 
            AND enemy_part_ids IS NOT NULL 
            AND phase = :phase
        GROUP BY
            a.clan_battle_id 
        ORDER BY
            a.clan_battle_id DESC
    """
    )
    suspend fun getAllClanBattleTargetCount(phase: Int): List<ClanBattleTargetCountData>

}
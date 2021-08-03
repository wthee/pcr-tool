package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro

const val query = """
    SELECT
        a.clan_battle_id,
        a.release_month,
        a.start_time,
        COALESCE(GROUP_CONCAT( b.enemy_id, '-' ), '000000' ) AS enemyIds,
        COALESCE(GROUP_CONCAT( b.unit_id, '-' ), '000000' ) AS unitIds
    FROM
        clan_battle_schedule AS a
        LEFT JOIN enemy_parameter AS b ON b.level > 30 AND (
            (b.enemy_id / 100000 >= 4013 AND a.clan_battle_id % 100 >= 26 AND a.clan_battle_id % 100 - ((b.enemy_id / 100000 % 100 - 12) * 12 + 10 ) = b.enemy_id / 1000 % 100 AND b.enemy_id % 1000 / 100 < 6) 
            OR
            (b.enemy_id / 100000 = 4010 AND a.clan_battle_id % 100 < 26 AND  a.clan_battle_id % 100 >= 12 AND a.clan_battle_id % 100 = b.enemy_id / 100 % 100 AND b.enemy_id % 100000 / 10000 > 1)
            OR
            (b.enemy_id / 100000 = 4010 AND a.clan_battle_id % 100 < 12 AND  a.clan_battle_id % 100 >= 10 AND a.clan_battle_id % 100 = b.enemy_id / 100 % 100)
            OR
            (b.enemy_id / 100000 = 4010 AND a.clan_battle_id % 100 == 4  AND (b.enemy_id = 401010401 OR b.enemy_id = 401011402 OR b.enemy_id = 401010402 OR b.enemy_id = 401011403  OR b.enemy_id = 401010403 OR b.enemy_id = 401011404 OR b.enemy_id = 401010404 OR b.enemy_id = 401011405  OR b.enemy_id = 401011401 OR b.enemy_id = 401011406))
            OR
            (b.enemy_id / 100000 = 4010 AND a.clan_battle_id % 100 < 10  AND  a.clan_battle_id % 100 >= 2  AND a.clan_battle_id % 10 = b.enemy_id / 100 % 10  AND b.enemy_id % 100000 / 10000 = 1)
            OR
            (b.enemy_id / 100000 = 4010 AND a.clan_battle_id % 100 = 1 AND b.enemy_id /100 % 1000 = 101 )
        ) 
        
"""

/**
 * 团队战 DAO
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
     * 获取所有团队战信息
     */
    @Query("$query GROUP BY a.clan_battle_id ORDER BY a.start_time DESC")
    suspend fun getAllClanBattleData(): List<ClanBattleInfo>

    /**
     * 获取单期团队战信息
     * @param clanId 团队战编号
     */
    @Query(" $query  WHERE a.clan_battle_id = :clanId GROUP BY a.clan_battle_id ORDER BY a.start_time DESC")
    suspend fun getClanInfo(clanId: Int): ClanBattleInfo
}
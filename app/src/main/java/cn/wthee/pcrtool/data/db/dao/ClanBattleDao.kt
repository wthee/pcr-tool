package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import cn.wthee.pcrtool.data.entity.EnemyParameter
import cn.wthee.pcrtool.data.view.ClanBattleInfo

const val join = """
 b.enemy_id LIKE '401%' 
AND
CASE WHEN a.clan_battle_id % 100 =37  THEN a.clan_battle_id % 100 - 34 = b.enemy_id / 1000 % 100 AND b.enemy_id % 1000 / 100 < 6 AND b.enemy_id like '4014%' AND ((a.boss_id % 100 <5 AND a.boss_id % 100 = b.enemy_id % 100) OR (a.boss_id % 100 = 5 AND b.enemy_id % 100 = 8))
ELSE 
CASE WHEN a.clan_battle_id % 100 > 34  THEN a.clan_battle_id % 100 - 34 = b.enemy_id / 1000 % 100 AND b.enemy_id % 1000 / 100 < 6 AND b.enemy_id like '4014%' AND a.boss_id % 100 = b.enemy_id % 100
ELSE 
CASE WHEN a.clan_battle_id % 100 = 34  THEN a.clan_battle_id % 100 - 22 = b.enemy_id / 1000 % 100 AND b.enemy_id % 1000 / 100 < 6 AND b.enemy_id like '4013%' AND ((a.boss_id % 100 <5 AND a.boss_id % 100 = b.enemy_id % 100) OR (a.boss_id % 100 = 5 AND b.enemy_id % 100 = 8))
ELSE 
CASE WHEN a.clan_battle_id % 100 = 32  THEN a.clan_battle_id % 100 - 22 = b.enemy_id / 1000 % 100 AND b.enemy_id % 1000 / 100 < 5 AND b.enemy_id like '4013%' AND ((a.boss_id % 100 <5 AND a.boss_id % 100 = b.enemy_id % 100) OR (a.boss_id % 100 = 5 AND b.enemy_id % 100 = 8))
ELSE 
CASE WHEN a.clan_battle_id % 100 > 31 THEN a.clan_battle_id % 100 - 22 = b.enemy_id / 1000 % 100 AND b.enemy_id % 1000 / 100 < 5 AND b.enemy_id like '4013%' AND a.boss_id % 100 = b.enemy_id % 100
ELSE 
CASE WHEN a.clan_battle_id % 100 >= 26 THEN a.clan_battle_id % 100 - 22 = b.enemy_id / 1000 % 100 AND b.enemy_id % 1000 / 100 < 5 AND b.enemy_id like '4013%' AND a.boss_id % 100 = b.enemy_id % 100 
ELSE 
CASE WHEN a.clan_battle_id % 100 > 11 THEN a.clan_battle_id % 100 = b.enemy_id / 100 % 100 AND b.enemy_id % 100000 / 10000 > 1  AND b.enemy_id like '4010%' AND a.boss_id % 100 = b.enemy_id % 100
ELSE 
CASE WHEN  a.clan_battle_id % 100 > 9 THEN a.clan_battle_id % 100 = b.enemy_id / 100 % 100 AND b.enemy_id % 100000 / 10000 > 0  AND b.enemy_id like '4010%' AND a.boss_id % 100 = b.enemy_id % 100
ELSE 
CASE WHEN a.clan_battle_id % 100 = 4 THEN (a.boss_id = 10040101 AND (b.enemy_id = 401010401 OR b.enemy_id = 401011402)) OR (a.boss_id = 10040102 AND (b.enemy_id = 401010402 OR b.enemy_id = 401011403))  OR (a.boss_id = 10040103 AND (b.enemy_id = 401010403 OR b.enemy_id = 401011404))  OR (a.boss_id = 10040104 AND (b.enemy_id = 401010404 OR b.enemy_id = 401011405))  OR (a.boss_id = 10040105 AND (b.enemy_id = 401011401 OR b.enemy_id = 401011406))
ELSE 
CASE WHEN a.clan_battle_id % 100 <= 9 AND a.clan_battle_id % 100 > 1 THEN a.clan_battle_id % 10 = b.enemy_id / 100 % 10  AND b.enemy_id % 100000 / 10000 = 1  AND b.enemy_id like '4010%' AND a.boss_id % 100 = b.enemy_id % 100  AND a.clan_battle_id % 100 > 1
ELSE 
a.clan_battle_id % 10 = b.enemy_id / 100 % 10 AND b.enemy_id % 100000 / 10000 = 1  AND b.enemy_id % 10000 / 1000 = 0 AND a.boss_id % 100 = b.enemy_id % 100   AND b.enemy_id like '4010%'
END
END
END
END
END
END
END
END
END
END
"""

const val basicSelect = """
    SELECT
        c.start_time,
        c.release_month,
        a.clan_battle_id,
        COALESCE(GROUP_CONCAT( b.enemy_id, '-' ), '000000' ) AS enemyIds,
        COALESCE(GROUP_CONCAT( b.unit_id, '-' ), '000000' ) AS unitIds,
        COUNT(b.unit_id) / 5 AS section
"""

/**
 * 团队战 DAO
 */
@Dao
interface ClanBattleDao {

    @SkipQueryVerification
    @Query(
        """
        $basicSelect
        FROM
            clan_battle_schedule AS c
            LEFT JOIN clan_battle_boss_data AS a ON c.clan_battle_id = a.clan_battle_id
            LEFT JOIN enemy_parameter AS b ON $join
        GROUP BY
            c.start_time
        ORDER BY
            c.start_time DESC
    """
    )
    suspend fun getAllClanBattleData(): List<ClanBattleInfo>

    @SkipQueryVerification
    @Query(
        """
        $basicSelect
        FROM
            clan_battle_schedule AS c
            LEFT JOIN clan_battle_2_boss_data AS a ON c.clan_battle_id = a.clan_battle_id
            LEFT JOIN enemy_parameter AS b ON $join
        GROUP BY
            c.start_time
        ORDER BY
            c.start_time DESC
    """
    )
    suspend fun getAllClanBattleDataJP(): List<ClanBattleInfo>


    @SkipQueryVerification
    @Query("""SELECT * FROM enemy_parameter WHERE enemy_id = :enemyId""")
    suspend fun getBossAttr(enemyId: Int): EnemyParameter

}
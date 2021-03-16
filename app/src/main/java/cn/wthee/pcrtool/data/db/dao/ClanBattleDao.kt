package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.data.view.ClanBattleInfoPro

const val join = """
    b.enemy_id % 100 = a.clan_battle_id % 100 
            AND b.enemy_id LIKE '401%' 
"""

/**
 * 团队战 DAO
 */
@Dao
interface ClanBattleDao {

    @SkipQueryVerification
    @Query(
        """
        SELECT
            c.start_time,
            c.release_month,
            a.clan_battle_id,
            COALESCE(GROUP_CONCAT( b.unit_id, '-' ), '000000' ) AS enemyIds
        FROM
            clan_battle_schedule AS c
            LEFT JOIN clan_battle_boss_data AS a ON c.clan_battle_id = a.clan_battle_id
            LEFT JOIN enemy_parameter AS b ON $join
        GROUP BY
            c.start_time
    """
    )
    suspend fun getAllClanBattleData(): List<ClanBattleInfo>

    //fixme 替换查询
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            c.start_time,
            c.release_month,
            a.boss_id,
            a.clan_battle_id,
            a.order_num,
            b.enemy_id,
            b.unit_id,
            b.name,
            b.level,
            b.hp,
            b.atk,
            b.magic_str,
            b.def,
            b.magic_def,
            b.physical_critical,
            b.magic_critical,
            b.wave_hp_recovery,
            b.wave_energy_recovery,
            b.dodge,
            b.physical_penetrate,
            b.magic_penetrate,
            b.life_steal,
            b.hp_recovery_rate,
            b.energy_recovery_rate,
            b.energy_reduce_rate,
            b.accuracy,
            b.union_burst_level,
            b.main_skill_lv_1,
            b.main_skill_lv_2,
            b.main_skill_lv_3,
            b.main_skill_lv_4,
            b.main_skill_lv_5,
            b.main_skill_lv_6,
            b.main_skill_lv_7,
            b.main_skill_lv_8,
            b.main_skill_lv_9,
            b.main_skill_lv_10,
            b.ex_skill_lv_1,
            b.ex_skill_lv_2,
            b.ex_skill_lv_3,
            b.ex_skill_lv_4,
            b.ex_skill_lv_5
        
        FROM
            clan_battle_schedule AS c
            LEFT JOIN clan_battle_boss_data AS a ON c.clan_battle_id = a.clan_battle_id
            LEFT JOIN enemy_parameter AS b ON $join
        WHERE
            b.enemy_id IS NOT NULL
    """
    )
    suspend fun getAllClanBattleDataPro(): List<ClanBattleInfoPro>

}
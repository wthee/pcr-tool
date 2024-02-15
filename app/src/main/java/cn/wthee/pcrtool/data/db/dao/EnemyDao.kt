package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.db.view.MultiTargetEnemyInfo


/**
 * 怪物信息 DAO
 */
@Dao
interface EnemyDao {

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
            unit_enemy_data.comment,
            unit_enemy_data.prefab_id
        FROM
            enemy_parameter
            LEFT JOIN unit_enemy_data ON enemy_parameter.unit_id = unit_enemy_data.unit_id
        WHERE enemy_id = :enemyId"""
    )
    suspend fun getEnemyAttr(enemyId: Int): EnemyParameterPro

    /**
     * 获取多目标
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            :enemyId AS multi_enemy_id,
            (
                d.child_enemy_parameter_1 || '-' || d.child_enemy_parameter_2 || '-' || d.child_enemy_parameter_3 || '-' || d.child_enemy_parameter_4 || '-' || d.child_enemy_parameter_5 
            ) AS enemy_part_ids 
        FROM
            enemy_m_parts AS d
        WHERE
            enemy_id = :enemyId
    """
    )
    suspend fun getMultiTargetEnemyInfo(enemyId: Int): MultiTargetEnemyInfo?

    /**
     * 获取普通攻击时间
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            normal_atk_cast_time 
        FROM
            unit_enemy_data 
        WHERE
            unit_id = :unitId
    """
    )
    suspend fun getAtkCastTime(unitId: Int): Double?
}
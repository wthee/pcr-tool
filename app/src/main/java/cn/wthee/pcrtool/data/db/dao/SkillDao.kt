package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import cn.wthee.pcrtool.data.entity.AttackPattern
import cn.wthee.pcrtool.data.entity.SkillData
import cn.wthee.pcrtool.data.entity.UnitSkillData
import cn.wthee.pcrtool.data.view.SkillActionPro

/**
 * 技能数据 DAO
 */
@Dao
interface SkillDao {
    /**
     * 根据 [unitId]，获取角色技能基本信息 [UnitSkillData]
     */
    @SkipQueryVerification
    @Query("SELECT * FROM unit_skill_data  WHERE unit_id = :unitId")
    suspend fun getUnitSkill(unitId: Int): UnitSkillData

    @SkipQueryVerification
    @Query("SELECT * FROM unit_skill_data ")
    suspend fun getAllUnitSkill(): List<UnitSkillData>

    /**
     * 根据 [sid]，获取技能数据 [SkillData]
     */
    @Query("SELECT * FROM skill_data  WHERE skill_id = :sid")
    suspend fun getSkillData(sid: Int): SkillData?

    /**
     * 根据技能效果id列表 [aid]，获取角色技能效果列表 [SkillActionPro]
     * fixme 测试用
     */
    @Query(
        """
        SELECT
            :lv as lv,
            :atk as atk,
            a.*,
           COALESCE( b.ailment_name,"") as ailment_name
        FROM
            skill_action AS a
            LEFT JOIN ailment_data as b ON a.action_type = b.ailment_action AND (a.action_detail_1 = b.ailment_detail_1 OR b.ailment_detail_1 = -1)
         WHERE action_id IN (:aid)
         AND CASE WHEN 0 = :type THEN a.action_type <> 0 ELSE a.action_type = :type END
    """
    )
    suspend fun getSkillActions(type: Int, lv: Int, atk: Int, aid: List<Int>): List<SkillActionPro>

    /**
     * 根据 [unitId]，获取角色动作循环列表 [AttackPattern]
     */
    @Query("SELECT * FROM unit_attack_pattern where unit_id = :unitId")
    suspend fun getAttackPattern(unitId: Int): List<AttackPattern>
}
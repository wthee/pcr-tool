package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import cn.wthee.pcrtool.data.db.entity.AttackPattern
import cn.wthee.pcrtool.data.db.entity.UnitSkillData
import cn.wthee.pcrtool.data.db.entityjp.SkillDataJP
import cn.wthee.pcrtool.data.db.view.SkillActionPro

/**
 * 技能数据 DAO
 */
@Dao
interface SkillDao {
    /**
     * 获取角色技能基本信息
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM unit_skill_data  WHERE unit_id = :unitId")
    suspend fun getUnitSkill(unitId: Int): UnitSkillData

    /**
     * 获取技能数值数据
     * @param skillId 技能编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM skill_data  WHERE skill_id = :skillId")
    suspend fun getSkillData(skillId: Int): SkillDataJP?

    /**
     * 获取角色技能动作效果列表
     * @param lv 技能等级
     * @param atk 角色攻击力
     * @param actionIds 技能动作编号
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
         WHERE action_id IN (:actionIds)
    """
    )
    suspend fun getSkillActions(lv: Int, atk: Int, actionIds: List<Int>): List<SkillActionPro>

    /**
     * 获取角色动作循环列表
     * @param unitId 角色编号
     */
    @Query("SELECT * FROM unit_attack_pattern where unit_id = :unitId")
    suspend fun getAttackPattern(unitId: Int): List<AttackPattern>
}
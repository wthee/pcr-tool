package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import cn.wthee.pcrtool.data.db.view.*

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
    suspend fun getUnitSkill(unitId: Int): UnitSkillData?

    /**
     * 获取技能动作等数据
     * @param skillId 技能编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM skill_data  WHERE skill_id = :skillId")
    suspend fun getSkillData(skillId: Int): SkillData?

    /**
     * 获取角色技能动作效果列表
     * @param lv 技能等级
     * @param atk 角色攻击力
     * @param actionIds 技能动作编号
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            :lv AS lv,
            :atk AS atk,
            a.*,
           COALESCE( b.ailment_name,"") as ailment_name,
           :isRfSkill AS isRfSkill
        FROM
            skill_action AS a
            LEFT JOIN ailment_data as b ON a.action_type = b.ailment_action AND (a.action_detail_1 = b.ailment_detail_1 OR b.ailment_detail_1 = -1)
         WHERE action_id IN (:actionIds)
    """
    )
    suspend fun getSkillActions(
        lv: Int,
        atk: Int,
        actionIds: List<Int>,
        isRfSkill: Boolean
    ): List<SkillActionDetail>

    /**
     * 获取角色动作循环列表
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM unit_attack_pattern where unit_id = :unitId")
    suspend fun getAttackPattern(unitId: Int): List<AttackPattern>

    /**
     * 获取角色特殊技能标签
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM spskill_label_data where unit_id = :unitId")
    suspend fun getSpSkillLabel(unitId: Int): SpSkillLabelData

    /**
     * 获取受tp限制的技能信息
     * @param skillId 技能编号
     */
    @SkipQueryVerification
    @Query("SELECT rf_skill_id FROM unit_skill_data_rf WHERE min_lv > 260 AND skill_id = :skillId")
    suspend fun getRfSkillId(skillId: Int): Int?

}
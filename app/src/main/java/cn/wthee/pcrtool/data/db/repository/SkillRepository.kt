package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.SkillDao
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.db.view.SkillData
import cn.wthee.pcrtool.data.enums.SkillType
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * 角色技能 Repository
 *
 * @param skillDao
 */
class SkillRepository @Inject constructor(private val skillDao: SkillDao) {

    suspend fun getUnitSkill(unitId: Int) = skillDao.getUnitSkill(unitId)

    private suspend fun getSkillData(skillId: Int, lv: Int): SkillData? {
        val skillData = skillDao.getSkillData(skillId)
        //等级大于300时，查询新技能信息
        if (lv > Constants.OTHER_LIMIT_LEVEL) {
            try {
                val otherRfSkillId = skillDao.getRfSkillId(
                    skillId = skillId,
                    minLv = Constants.OTHER_LIMIT_LEVEL
                )
                otherRfSkillId?.let { id ->
                    val rfSkillData = skillDao.getSkillData(id)
                    rfSkillData?.isOtherRfSkill = true
                    return rfSkillData
                }
            } catch (_: Exception) {

            }
        } else if (lv > Constants.TP_LIMIT_LEVEL) {
            //等级大于260时，查询新技能信息
            try {
                val tpRfSkillId = skillDao.getRfSkillId(
                    skillId = skillId,
                    minLv = Constants.TP_LIMIT_LEVEL
                )
                tpRfSkillId?.let { id ->
                    val rfSkillData = skillDao.getSkillData(id)
                    rfSkillData?.isRfSkill = true
                    return rfSkillData
                }
            } catch (_: Exception) {

            }
        }
        return skillData
    }

    suspend fun getSkillIconType(skillId: Int) = skillDao.getSkillIconType(skillId)

    private suspend fun getSkillActions(
        lv: Int,
        atk: Int,
        actionIds: List<Int>,
        isRfSkill: Boolean,
        isOtherRfSkill: Boolean
    ) = try {
        skillDao.getSkillActions(
            lv = lv,
            atk = atk,
            actionIds = actionIds,
            isRfSkill = isRfSkill,
            isOtherRfSkill = isOtherRfSkill
        )
    } catch (e: Exception) {
        LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "getSkillActions#actionIds:$actionIds")
        emptyList()
    }

    suspend fun getAttackPattern(unitId: Int) = skillDao.getAttackPattern(unitId)

    suspend fun getSpSkillLabel(unitId: Int) = try {
        skillDao.getSpSkillLabel(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "getSpSkillLabel#unitId:$unitId")
        null
    }

    /**
     * 获取技能信息
     *
     * @param skillIds 技能编号列表
     * @param lvs 技能等级列表
     * @param atk 基础攻击力
     * @param unitId 角色编号
     */
    suspend fun getSkills(
        skillIds: List<Int>,
        lvs: List<Int>,
        atk: Int,
        unitId: Int
    ): List<SkillDetail> {
        try {
            val infoList = mutableListOf<SkillDetail>()
            //技能编号信息
            val unitSkillData = skillDao.getUnitSkill(unitId)

            //技能信息
            skillIds.forEachIndexed { index, skillId ->
                val lv = if (lvs.size == 1) lvs[0] else lvs[index]
                if (skillId != 0) {
                    val skill = getSkillData(skillId, lv)
                    if (skill != null) {
                        val info = SkillDetail(
                            skillId = skill.skillId,
                            name = skill.name ?: "",
                            desc = skill.description,
                            iconType = skill.iconType,
                            castTime = skill.skillCastTime,
                            level = lv,
                            atk = atk,
                            bossUbCooltime = skill.bossUbCoolTime,
                            enemySkillIndex = index,
                        )
                        val actions = getSkillActions(
                            lv = lv,
                            atk = atk,
                            actionIds = skill.getAllActionId(),
                            isRfSkill = skill.isRfSkill,
                            isOtherRfSkill = skill.isOtherRfSkill
                        )
                        val dependIds = skill.getSkillDependData()
                        actions.forEachIndexed { i, action ->
                            if (i != 0) {
                                action.dependId = dependIds[action.actionId] ?: 0
                            }
                        }
                        info.actions = actions
                        //获取类型，ub或其它
                        if (unitSkillData != null) {
                            info.skillIndexType = unitSkillData.getSkillIndexType(skillId)
                        }
                        infoList.add(info)
                    }
                }
            }
            return infoList
        } catch (e: Exception) {
            LogReportUtil.upload(
                e,
                Constants.EXCEPTION_SKILL + "getSkills#unit_id:$unitId;skillId:${skillIds}"
            )
            return emptyList()
        }
    }

    /**
     * 获取角色技能 id
     */
    suspend fun getSkillIds(unitId: Int, skillType: SkillType): List<Int> {
        try {
            val data = skillDao.getUnitSkill(unitId)
            val normalList = data!!.getNormalSkillId()
            val spList = data.getSpSkillId()
            return when (skillType) {
                SkillType.ALL -> normalList + spList
                SkillType.NORMAL -> normalList
                SkillType.SP -> spList
            }
        } catch (e: Exception) {
            LogReportUtil.upload(
                e,
                Constants.EXCEPTION_SKILL + "getSkillIds#unit_id:$unitId type:${skillType.name}"
            )
        }
        return arrayListOf()
    }

    /**
     * 获取怪物技能信息
     *
     * @param enemyParameterPro 怪物基本参数
     */
    suspend fun getAllEnemySkill(enemyParameterPro: EnemyParameterPro): List<SkillDetail> {
        val data = getUnitSkill(enemyParameterPro.unitId)
        data?.let {
            return getSkills(
                skillIds = data.getEnemySkillId(),
                lvs = enemyParameterPro.getSkillLv(),
                atk = maxOf(
                    maxOf(enemyParameterPro.attr.atk, enemyParameterPro.attr.magicStr),
                    enemyParameterPro.partAtk
                ),
                unitId = enemyParameterPro.unitId
            )
        }
        return emptyList()
    }
}
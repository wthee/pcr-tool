package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.SkillDao
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

    suspend fun getSkillData(skillId: Int, lv: Int): SkillData? {
        val skillData = skillDao.getSkillData(skillId)
        //等级大于260时，查询新技能信息
        if (lv > Constants.TP_LIMIT_LEVEL) {
            try {
                val rfSkillId = skillDao.getRfSkillId(skillId)
                rfSkillId?.let { id ->
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

    suspend fun getSkillActions(lv: Int, atk: Int, actionIds: List<Int>, isRfSkill: Boolean) =
        skillDao.getSkillActions(lv, atk, actionIds, isRfSkill)

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
    ): MutableList<SkillDetail> {
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
                    val actions = skillDao.getSkillActions(
                        lv,
                        atk,
                        skill.getAllActionId(),
                        isRfSkill = skill.isRfSkill
                    )
                    val dependIds = skill.getSkillDependData()
                    actions.forEachIndexed { i, action ->
                        if (i != 0) {
                            action.dependId = dependIds[action.actionId] ?: 0
                        }
                    }
                    info.actions = actions
                    //获取类型，ub或其它
                    if(unitSkillData != null){
                        info.skillIndexType = unitSkillData.getSkillIndexType(skillId)
                    }
                    infoList.add(info)
                }
            }
        }
        return infoList
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
            LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "getSkillIds#unit_id:$unitId type:${skillType.name}")
        }
        return arrayListOf()
    }
}
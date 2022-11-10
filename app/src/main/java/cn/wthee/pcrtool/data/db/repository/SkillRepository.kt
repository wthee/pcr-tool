package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.SkillDao
import cn.wthee.pcrtool.data.db.view.SkillData
import javax.inject.Inject

/**
 * 角色技能 Repository
 *
 * @param skillDao
 */
class SkillRepository @Inject constructor(private val skillDao: SkillDao) {

    suspend fun getUnitSkill(unitId: Int) = skillDao.getUnitSkill(unitId)

    suspend fun getSkillData(skillId: Int): SkillData? {
        val skillData = skillDao.getSkillData(skillId)
        //TODO 校验逻辑是否正确
        val rfSkillId = skillDao.getRfSkillId(skillId)
        rfSkillId?.let { id ->
            val rfSkillData = skillDao.getSkillData(id)
            rfSkillData?.let {
                // >260 技能等级后的技能信息，添加进原技能action列表
                skillData?.let {
                    skillData.rfActionIdList = rfSkillData.getAllActionId()
                }
            }
        }
        return skillData
    }

    suspend fun getSkillActions(lv: Int, atk: Int, actionIds: List<Int>) =
        skillDao.getSkillActions(lv, atk, actionIds)

    suspend fun getAttackPattern(unitId: Int) = skillDao.getAttackPattern(unitId)

    suspend fun getSpSkillLabel(unitId: Int) = skillDao.getSpSkillLabel(unitId)

}
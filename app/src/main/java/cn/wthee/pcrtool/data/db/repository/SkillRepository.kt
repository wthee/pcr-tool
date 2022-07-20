package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.SkillDao
import javax.inject.Inject

/**
 * 角色技能 Repository
 *
 * @param skillDao
 */
class SkillRepository @Inject constructor(private val skillDao: SkillDao) {

    suspend fun getUnitSkill(unitId: Int) = skillDao.getUnitSkill(unitId)

    suspend fun getSkillData(skillId: Int) = skillDao.getSkillData(skillId)

    suspend fun getSkillActions(lv: Int, atk: Int, actionIds: List<Int>) =
        skillDao.getSkillActions(lv, atk, actionIds)

    suspend fun getAttackPattern(unitId: Int) = skillDao.getAttackPattern(unitId)

    suspend fun getSpSkillLabel(unitId: Int) = skillDao.getSpSkillLabel(unitId)

}
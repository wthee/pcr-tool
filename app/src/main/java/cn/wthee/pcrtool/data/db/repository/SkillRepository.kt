package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.SkillDao
import cn.wthee.pcrtool.data.db.view.SkillData
import cn.wthee.pcrtool.utils.Constants
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
            val rfSkillId = skillDao.getRfSkillId(skillId)
            rfSkillId?.let { id ->
                val rfSkillData = skillDao.getSkillData(id)
                rfSkillData?.isRfSkill = true
                return rfSkillData
            }
        }
        return skillData
    }

    suspend fun getSkillActions(lv: Int, atk: Int, actionIds: List<Int>, isRfSkill: Boolean) =
        skillDao.getSkillActions(lv, atk, actionIds, isRfSkill)

    suspend fun getAttackPattern(unitId: Int) = skillDao.getAttackPattern(unitId)

    suspend fun getSpSkillLabel(unitId: Int) = skillDao.getSpSkillLabel(unitId)

}
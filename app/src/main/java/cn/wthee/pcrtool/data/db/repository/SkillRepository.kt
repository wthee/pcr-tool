package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.SkillDao

/**
 * 角色技能 Repository
 *
 * 数据来源 [skillDao]
 */
class SkillRepository(private val skillDao: SkillDao) {

    suspend fun getUnitSkill(id: Int) = skillDao.getUnitSkill(id)

    suspend fun getSkillData(sid: Int) = skillDao.getSkillData(sid)

    suspend fun getSkillActions(lv: Int, atk: Int, aids: List<Int>) =
        skillDao.getSkillActions(lv, atk, aids)

    suspend fun getAttackPattern(unitId: Int) = skillDao.getAttackPattern(unitId)

    companion object {

        fun getInstance(skillDao: SkillDao) = SkillRepository(skillDao)
    }
}
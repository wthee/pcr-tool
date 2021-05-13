package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.SkillDao
import javax.inject.Inject

/**
 * 角色技能 Repository
 *
 * 数据来源 [skillDao]
 */
class SkillRepository @Inject constructor(private val skillDao: SkillDao) {

    suspend fun getUnitSkill(id: Int) = skillDao.getUnitSkill(id)

    suspend fun getSkillData(sid: Int) = skillDao.getSkillData(sid)

    suspend fun getSkillActions(type: Int, lv: Int, atk: Int, aids: List<Int>) =
        skillDao.getSkillActions(type, lv, atk, aids)

    suspend fun getAttackPattern(unitId: Int) = skillDao.getAttackPattern(unitId)

}
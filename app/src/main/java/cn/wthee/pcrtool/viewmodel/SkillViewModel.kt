package cn.wthee.pcrtool.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.enums.SkillType
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


/**
 * 角色技能 ViewModel
 *
 * @param skillRepository
 */
@HiltViewModel
class SkillViewModel @Inject constructor(
    private val skillRepository: SkillRepository
) : ViewModel() {

    /**
     * 获取角色技能信息
     *
     * @param lv 技能能级
     * @param atk 基础攻击力
     * @param unitId 角色编号
     */
    fun getCharacterSkills(lv: Int, atk: Int, unitId: Int, skillType: SkillType) = flow {
        try {
            val skillList = getSkills(
                getSkillIds(unitId, skillType),
                arrayListOf(lv),
                atk,
            )
            emit(skillList)
        } catch (e: Exception) {
            if (e !is CancellationException) {
                LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "unit_id:$unitId")
            }
        }
    }


    /**
     * 获取ex装备被动技能信息
     *
     * @param skillIds 技能编号列表
     */
    fun getExtraEquipPassiveSkills(skillIds: List<Int>) = flow {
        try {
            val skillList = getSkills(
                skillIds,
                arrayListOf(0),
                0,
            )
            emit(skillList)
        } catch (e: Exception) {
            if (e !is CancellationException) {
                LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "skillId:$skillIds")
            }
        }
    }

    /**
     * 获取角色技能 id
     */
    private suspend fun getSkillIds(unitId: Int, skillType: SkillType): List<Int> {
        try {
            val data = skillRepository.getUnitSkill(unitId)
            val normalList = data!!.getNormalSkillId()
            val spList = data.getSpSkillId()
            return when (skillType) {
                SkillType.ALL -> normalList + spList
                SkillType.NORMAL -> normalList
                SkillType.SP -> spList
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "unit_id:$unitId")
            }
        }
        return arrayListOf()
    }


    /**
     * 测试用，获取所有角色技能
     */
    fun getCharacterSkills(lv: Int, atk: Int, unitIds: List<Int>) = flow {
        try {
            val skillIds = arrayListOf<Int>()
            unitIds.forEach {
                val data = skillRepository.getUnitSkill(it)
                if (data != null) {
                    skillIds.addAll(data.getNormalSkillId())
                    skillIds.addAll(data.getSpSkillId())
                }
            }
            emit(
                getSkills(
                    skillIds.distinct().filter { it / 1000000 != 2 },
                    arrayListOf(lv),
                    atk
                )
            )
        } catch (e: Exception) {
            Log.e("DEBUG", e.message ?: "")
        }
    }

    /**
     * 获取技能信息
     *
     * @param skillIds 技能编号列表
     * @param lvs 技能等级列表
     * @param atk 基础攻击力
     */
    private suspend fun getSkills(
        skillIds: List<Int>,
        lvs: List<Int>,
        atk: Int,
    ): MutableList<SkillDetail> {
        val infos = mutableListOf<SkillDetail>()
        //技能信息
        skillIds.forEachIndexed { index, skillId ->
            val lv = if (lvs.size == 1) lvs[0] else lvs[index]
            if (skillId != 0) {
                val skill = skillRepository.getSkillData(skillId, lv)
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
                        enemySkillIndex = index
                    )
                    val actions = skillRepository.getSkillActions(
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
                    infos.add(info)
                }
            }
        }
        return infos
    }

    /**
     * 获取技能图标信息
     */
    fun getSkillIconTypes(loopIdList: List<Int>, unitId: Int) = flow {
        try {
            val map = hashMapOf<Int, Int>()
            val unitSkill = skillRepository.getUnitSkill(unitId)
            val mLoopIdList = loopIdList.distinct()

            unitSkill?.let {
                //技能id
                val mainSkillIdList = arrayListOf(
                    unitSkill.main_skill_1,
                    unitSkill.main_skill_2,
                    unitSkill.main_skill_3,
                    unitSkill.main_skill_4,
                    unitSkill.main_skill_5,
                    unitSkill.main_skill_6,
                    unitSkill.main_skill_7,
                    unitSkill.main_skill_8,
                    unitSkill.main_skill_9,
                    unitSkill.main_skill_10,
                )
                //sp技能id
                val spSkillIdList = arrayListOf(
                    unitSkill.sp_skill_1,
                    unitSkill.sp_skill_2,
                    unitSkill.sp_skill_3,
                    unitSkill.sp_skill_4,
                    unitSkill.sp_skill_5,
                )

                //处理循环技能id对应的技能图标
                mLoopIdList.forEach { loopId ->
                    val skillId = when {
                        loopId / 1000 == 1 -> mainSkillIdList[loopId % 100 - 1]
                        loopId / 1000 == 2 -> spSkillIdList[loopId % 100 - 1]
                        else -> null
                    }
                    skillId?.let {
                        map[loopId] = skillRepository.getSkillIconType(skillId)
                    }
                }
            }

            emit(map)
        } catch (_: Exception) {

        }
    }

    /**
     * 获取角色技能循环
     *
     * @param unitId 角色编号
     */
    fun getCharacterSkillLoops(unitId: Int) = flow {
        emit(skillRepository.getAttackPattern(unitId))
    }


    /**
     * 获取怪物技能信息
     *
     * @param enemyParameterPro 怪物基本参数
     */
    fun getAllEnemySkill(enemyParameterPro: EnemyParameterPro) = flow {
        try {
            val data = skillRepository.getUnitSkill(enemyParameterPro.unitId)
            data?.let {
                val infoList = getSkills(
                    data.getEnemySkillId(),
                    enemyParameterPro.getSkillLv(),
                    maxOf(enemyParameterPro.attr.atk, enemyParameterPro.attr.magicStr)
                )
                emit(infoList)
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                LogReportUtil.upload(
                    e,
                    Constants.EXCEPTION_SKILL + "enemy:${enemyParameterPro.enemyId}"
                )
            }
        }
    }

    /**
     * 获取全部技能循环
     *
     * @param enemyParameterPro 怪物基本参数
     */
    fun getAllSkillLoops(enemyParameterPro: EnemyParameterPro) = flow {
        //技能循环
        val pattern = skillRepository.getAttackPattern(enemyParameterPro.unitId)
        emit(pattern)
    }

    /**
     * 获取角色特殊技能标签
     *
     * @param unitId 角色编号
     */
    fun getSpSkillLabel(unitId: Int) = flow {
        try {
            val data = skillRepository.getSpSkillLabel(unitId)
            emit(data)
        } catch (_: Exception) {
        }
    }
}

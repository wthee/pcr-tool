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
import kotlinx.coroutines.flow.emitAll
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
        skillIds.forEachIndexed { index, sid ->
            val skill = skillRepository.getSkillData(sid)
            if (skill != null) {
                val lv = if (lvs.size == 1) lvs[0] else lvs[index]

                val info = SkillDetail(
                    skill.skillId,
                    skill.name ?: "",
                    skill.description,
                    skill.iconType,
                    skill.skillCastTime,
                    lv,
                    atk,
                    skill.bossUbCoolTime
                )
                val actions = skillRepository.getSkillActions(lv, atk, skill.getAllActionId())
                val dependIds = skill.getSkillDependData()
                actions.forEachIndexed { i, action ->
                    if (i != 0) {
                        action.dependId = dependIds[action.action_id] ?: 0
                    }
                }
                info.actions = actions
                //TODO 超过tp限制等级的技能动作
                if (skill.rfActionIdList.isNotEmpty()) {
                    val rfActions = skillRepository.getSkillActions(
                        lv - Constants.TP_LIMIT_LEVEL,
                        atk,
                        skill.rfActionIdList
                    )
                    rfActions.forEach {
                        it.level = lv
                        it.isTpLimitAction = true
                    }
                    info.actions = info.actions + rfActions
                }
                infos.add(info)
            }
        }
        return infos
    }

    /**
     * 获取技能图标信息
     */
    fun getskillIconTypes(unitId: Int, ids: ArrayList<Int>? = null) = flow {
        try {
            val skillIds = ids ?: getSkillIds(unitId, SkillType.ALL)
            //保存技能对应的图标类型，用于技能循环页面展示
            val map = hashMapOf<Int, Int>()
            //技能信息
            skillIds.forEachIndexed { _, sid ->
                val skill = skillRepository.getSkillData(sid)
                if (skill != null) {
                    var aid = skill.skillId % 1000
                    if (aid < 100) {
                        aid %= 10
                    }
                    map[aid] = skill.iconType
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
            val data = skillRepository.getUnitSkill(enemyParameterPro.unit_id)
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
                    Constants.EXCEPTION_SKILL + "enemy:${enemyParameterPro.enemy_id}"
                )
            }
        }
    }

    /**
     * 获取怪物技能图标
     *
     * @param enemyParameterPro 怪物基本参数
     */
    fun getAllEnemySkillLoopIcon(enemyParameterPro: EnemyParameterPro) = flow {
        try {
            val data = skillRepository.getUnitSkill(enemyParameterPro.unit_id)
            data?.let {
                emitAll(getskillIconTypes(0, ids = data.getEnemySkillId()))
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                LogReportUtil.upload(
                    e,
                    Constants.EXCEPTION_SKILL + "enemy:${enemyParameterPro.enemy_id}"
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
        val pattern = skillRepository.getAttackPattern(enemyParameterPro.unit_id)
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

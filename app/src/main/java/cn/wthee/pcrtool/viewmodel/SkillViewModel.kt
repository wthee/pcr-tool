package cn.wthee.pcrtool.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
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
     * @param cutinId 角色特殊编号
     */
    fun getCharacterSkills(lv: Int, atk: Int, unitId: Int, cutinId: Int) = flow {
        try {
            emit(
                getSkill(
                    getSkillIds(unitId, cutinId),
                    arrayListOf(lv),
                    atk,
                )
            )
        } catch (e: Exception) {
            if (e !is CancellationException) {
                LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "unit_id:$unitId")
            }
        }
    }

    /**
     * 获取角色技能 id
     */
    private suspend fun getSkillIds(unitId: Int, cutinId: Int): ArrayList<Int> {
        var spUB = 0
        var cutinSpUB = 0
        try {
            spUB = skillRepository.getUnitSpUBSkill(unitId) ?: 0
        } catch (_: Exception) {

        }
        try {
            cutinSpUB = skillRepository.getUnitSpUBSkill(cutinId) ?: 0
        } catch (_: Exception) {

        }

        try {
            val data = skillRepository.getUnitSkill(unitId)
            val spData = skillRepository.getUnitSkill(cutinId)
            return if (data != null && spData != null) {
                data.joinCutinSkillId(spUB, cutinSpUB, spData)
            } else {
                data!!.getAllSkillId(spUB)
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
                val spUB = skillRepository.getUnitSpUBSkill(it) ?: 0
                if (data != null) {
                    skillIds.addAll(data.getAllSkillId(spUB))
                }
            }
            emit(getSkill(skillIds, arrayListOf(lv), atk))
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
    private suspend fun getSkill(
        skillIds: List<Int>,
        lvs: List<Int>,
        atk: Int
    ): MutableList<SkillDetail> {
        val infos = mutableListOf<SkillDetail>()
        //技能信息
        skillIds.forEachIndexed { index, sid ->
            val skill = skillRepository.getSkillData(sid)
            if (skill != null) {
                val lv = if (lvs.size == 1) lvs[0] else lvs[index]

                val info = SkillDetail(
                    skill.skill_id,
                    skill.name ?: "",
                    skill.description,
                    skill.icon_type,
                    skill.skill_cast_time,
                    lv,
                    atk,
                    skill.boss_ub_cool_time
                )
                val actions = skillRepository.getSkillActions(lv, atk, skill.getAllActionId())
                val dependIds = skill.getSkillDependData()
                actions.forEachIndexed { i, action ->
                    if (i != 0) {
                        action.dependId = dependIds[action.action_id] ?: 0
                    }
                }
                info.actions = actions
                infos.add(info)
            }
        }
        return infos
    }

    /**
     * 获取技能信息
     *
     * @param skillIds 技能编号列表
     */
    fun getskillIconTypes(unitId: Int, cutinId: Int, ids: ArrayList<Int>? = null) = flow {
        try {
            val skillIds = ids ?: getSkillIds(unitId, cutinId)
            //保存技能对应的图标类型，用于技能循环页面展示
            val map = hashMapOf<Int, Int>()
            //技能信息
            skillIds.forEachIndexed { index, sid ->
                val skill = skillRepository.getSkillData(sid)
                if (skill != null) {
                    var aid = skill.skill_id % 1000
                    if (aid < 100) {
                        aid %= 10
                    }
                    map[aid] = skill.icon_type
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
                val infos = getSkill(
                    data.getEnemySkillId(),
                    enemyParameterPro.getSkillLv(),
                    maxOf(enemyParameterPro.attr.atk, enemyParameterPro.attr.magicStr)
                )
                emit(infos)
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
                emitAll(getskillIconTypes(0, 0, ids = data.getEnemySkillId()))
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
}

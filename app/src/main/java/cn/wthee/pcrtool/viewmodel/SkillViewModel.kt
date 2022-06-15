package cn.wthee.pcrtool.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.view.AttackPattern
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 角色技能 ViewModel
 *
 * @param skillRepository
 */
@HiltViewModel
class SkillViewModel @Inject constructor(
    private val skillRepository: SkillRepository
) : ViewModel() {


    var skills = MutableLiveData<List<SkillDetail>>()
    var allSkills = MutableLiveData<ArrayList<List<SkillDetail>>>()
    var allAtkPattern = MutableLiveData<ArrayList<List<AttackPattern>>>()
    var iconTypes = MutableLiveData(hashMapOf<Int, Int>())
    var allIconTypes = MutableLiveData<ArrayList<HashMap<Int, Int>>>()

    /**
     * 获取角色技能信息
     *
     * @param lv 技能能级
     * @param atk 基础攻击力
     * @param unitId 角色编号
     * @param cutinId 角色特殊编号
     */
    fun getCharacterSkills(lv: Int, atk: Int, unitId: Int, cutinId: Int) {
        viewModelScope.launch {
            var spUB = 0
            var cutinSpUB = 0
            try {
                spUB = skillRepository.getUnitSpUBSkill(unitId) ?: 0
            } catch (e: Exception) {

            }
            try {
                cutinSpUB = skillRepository.getUnitSpUBSkill(cutinId) ?: 0
            } catch (e: Exception) {

            }

            try {
                val data = skillRepository.getUnitSkill(unitId)
                val spData = skillRepository.getUnitSkill(cutinId)
                if (data != null && spData != null) {
                    getSkillInfo(
                        data.joinCutinSkillId(spUB, cutinSpUB, spData),
                        atk,
                        arrayListOf(lv)
                    )
                } else {
                    data?.let {
                        getSkillInfo(
                            data.getAllSkillId(spUB),
                            atk,
                            arrayListOf(lv)
                        )
                    }
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "unit_id:$unitId")
            }
        }
    }

    /**
     * 测试用，获取所有角色技能
     */
    fun getCharacterSkills(lv: Int, atk: Int, unitIds: List<Int>) {
        viewModelScope.launch {
            try {
                val skillIds = arrayListOf<Int>()
                unitIds.forEach {
                    val data = skillRepository.getUnitSkill(it)
                    val spUB = skillRepository.getUnitSpUBSkill(it) ?: 0
                    if (data != null) {
                        skillIds.addAll(data.getAllSkillId(spUB))
                    }
                }
                getSkillInfo(skillIds, atk, arrayListOf(lv))
            } catch (e: Exception) {
                Log.e("DEBUG", e.message ?: "")
            }
        }
    }

    /**
     * 获取怪物技能信息
     *
     * @param list 怪物基本参数列表
     */
    fun getAllEnemySkill(list: List<EnemyParameterPro>) {
        viewModelScope.launch {
            try {
                val allSkill = arrayListOf<List<SkillDetail>>()
                val allIcon = arrayListOf<HashMap<Int, Int>>()
                list.forEach { enemy ->
                    val data = skillRepository.getUnitSkill(enemy.unit_id)
                    data?.let {
                        val (infos, map) = getSkill(
                            data.getEnemySkillId(),
                            enemy.getSkillLv(),
                            maxOf(enemy.attr.atk, enemy.attr.magicStr)
                        )
                        allSkill.add(infos)
                        allIcon.add(map)
                    }
                }
                allSkills.postValue(allSkill)
                allIconTypes.postValue(allIcon)
            } catch (e: Exception) {
                LogReportUtil.upload(
                    e,
                    Constants.EXCEPTION_SKILL + "enemy:${list[0].enemy_id}"
                )
            }
        }
    }

    /**
     * 获取技能信息
     */
    private fun getSkillInfo(skillIds: List<Int>, atk: Int, lvs: List<Int>) {
        viewModelScope.launch {
            val (infos, map) = getSkill(skillIds, lvs, atk)
            iconTypes.postValue(map)
            skills.postValue(infos)
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
    ): Pair<MutableList<SkillDetail>, HashMap<Int, Int>> {
        val infos = mutableListOf<SkillDetail>()
        //保存技能对应的图标类型，用于技能循环页面展示
        val map = hashMapOf<Int, Int>()
        //技能信息
        skillIds.forEachIndexed { index, sid ->
            val skill = skillRepository.getSkillData(sid)
            if (skill != null) {
                val lv = if (lvs.size == 1) lvs[0] else lvs[index]
                var aid = skill.skill_id % 1000
                if (aid < 100) {
                    aid %= 10
                }
                map[aid] = skill.icon_type
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
        return Pair(infos, map)
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
     * 获取全部技能循环
     *
     * @param list 怪物基本参数列表
     */
    fun getAllSkillLoops(list: List<EnemyParameterPro>) {
        viewModelScope.launch {
            //技能循环
            val allList = arrayListOf<List<AttackPattern>>()
            list.forEach {
                val pattern = skillRepository.getAttackPattern(it.unit_id)
                allList.add(pattern)
            }
            allAtkPattern.postValue(allList)
        }
    }
}

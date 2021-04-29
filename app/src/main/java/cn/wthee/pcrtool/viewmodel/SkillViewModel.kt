package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.entity.AttackPattern
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.utils.Constants
import com.umeng.umcrash.UMCrash
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 角色技能 ViewModel
 *
 * 数据来源 [SkillRepository]
 */
@HiltViewModel
class SkillViewModel @Inject constructor(
    private val repository: SkillRepository
) : ViewModel() {


    var skills = MutableLiveData<List<SkillDetail>>()
    var atkPattern = MutableLiveData<List<AttackPattern>>()
    var iconTypes = MutableLiveData(hashMapOf<Int, Int>())

    /**
     * 根据 [unitId]， 获取角色技能信息
     */
    fun getCharacterSkills(lv: Int, atk: Int, unitId: Int) {
        viewModelScope.launch {
            try {
                val data = repository.getUnitSkill(unitId)
                getSkillInfo(data.getAllSkillId(), atk, arrayListOf(lv))
            } catch (e: Exception) {
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_SKILL + "unit_id:$unitId")
                }
            }
        }
    }

    /**
     * 获取怪物技能信息
     */
    fun getEnemySkill(lvs: List<Int>, atk: Int, unitId: Int) {
        viewModelScope.launch {
            try {
                val data = repository.getUnitSkill(unitId)
                getSkillInfo(data.getEnemySkillId(), atk, lvs)
            } catch (e: Exception) {
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_SKILL + "unit_id:$unitId")
                }
            }
        }
    }

    /**
     * 获取技能信息
     */
    private fun getSkillInfo(skillIds: List<Int>, atk: Int, lvs: List<Int>) {
        viewModelScope.launch {
            val infos = mutableListOf<SkillDetail>()
            val map = hashMapOf<Int, Int>()
            //技能信息
            skillIds.forEachIndexed { index, sid ->
                val skill = repository.getSkillData(sid)
                if (skill != null) {
                    val lv = if (lvs.size == 1) lvs[0] else lvs[index]
                    val aid = skill.skill_id % 1000
                    map[aid] = skill.icon_type
                    val info = SkillDetail(
                        skill.skill_id,
                        skill.name ?: "",
                        skill.description,
                        skill.icon_type,
                        skill.skill_cast_time,
                        lv,
                        atk
                    )
                    val actions = repository.getSkillActions(0, lv, atk, skill.getAllActionId())
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
            iconTypes.postValue(map)
            skills.postValue(infos)
        }

    }

    /**
     * 根据 [unitId]，角色技能循环
     */
    fun getCharacterSkillLoops(unitId: Int) {
        viewModelScope.launch {
            //技能循环
            val pattern = repository.getAttackPattern(unitId)
            atkPattern.postValue(pattern)
        }
    }


}

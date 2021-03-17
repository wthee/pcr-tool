package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.entity.AttackPattern
import cn.wthee.pcrtool.data.model.SkillInfo
import cn.wthee.pcrtool.utils.Constants
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 角色技能 ViewModel
 *
 * 数据来源 [SkillRepository]
 */
class SkillViewModel(
    private val repository: SkillRepository
) : ViewModel() {

    companion object {
        var iconTypes = hashMapOf<Int, Int>()
    }

    var skills = MutableLiveData<List<SkillInfo>>()
    var atlPattern = MutableLiveData<List<AttackPattern>>()
    private var refresh = MutableLiveData<Boolean>()
    private var isLoading = MutableLiveData<Boolean>()

    /**
     * 根据 [unitId]， 获取角色技能信息
     */
    fun getCharacterSkills(lv: Int, atk: Int, unitId: Int) {
        isLoading.postValue(true)
        iconTypes.clear()
        viewModelScope.launch {
            try {
                val infos = mutableListOf<SkillInfo>()
                val data = repository.getUnitSkill(unitId)
                //技能信息
                data.getAllSkillId().forEach { sid ->
                    val skill = repository.getSkillData(sid)
                    val aid = skill.skill_id % 1000
                    iconTypes[aid] = skill.icon_type
                    val info = SkillInfo(
                        skill.skill_id,
                        skill.name ?: "",
                        skill.description,
                        skill.icon_type
                    )
                    info.actions = repository.getSkillActions(lv, atk, skill.getAllActionId())
                    infos.add(info)
                }
                isLoading.postValue(false)
                refresh.postValue(false)
                skills.postValue(infos)
            } catch (e: Exception) {
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_SKILL + "unit_id:$unitId")
                }
            }

        }
    }

    /**
     * 根据 [unitId]，角色技能循环
     */
    fun getCharacterSkillLoops(unitId: Int) {
        isLoading.postValue(true)
        viewModelScope.launch {
            //技能循环
            val pattern = repository.getAttackPattern(unitId)
            atlPattern.postValue(pattern)
        }
    }


}

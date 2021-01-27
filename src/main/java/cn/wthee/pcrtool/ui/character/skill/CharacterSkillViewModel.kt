package cn.wthee.pcrtool.ui.character.skill

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.bean.CharacterSkillInfo
import cn.wthee.pcrtool.data.db.entity.AttackPattern
import cn.wthee.pcrtool.data.db.repository.CharacterRepository
import kotlinx.coroutines.launch

/**
 * 角色技能 ViewModel
 *
 * 数据来源 [CharacterRepository]
 */
class CharacterSkillViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    companion object {
        var iconTypes = hashMapOf<Int, Int>()
    }

    var skills = MutableLiveData<List<CharacterSkillInfo>>()
    var atlPattern = MutableLiveData<List<AttackPattern>>()
    private var refresh = MutableLiveData<Boolean>()
    private var isLoading = MutableLiveData<Boolean>()

    /**
     * 根据 [unitId]， 获取角色技能信息
     */
    fun getCharacterSkills(unitId: Int) {
        isLoading.postValue(true)
        iconTypes.clear()
        viewModelScope.launch {
            try {
                val infos = mutableListOf<CharacterSkillInfo>()
                val data = repository.getCharacterSkill(unitId)
                //技能信息
                data.getAllSkillId().forEach { sid ->
                    val skill = repository.getSkillData(sid)
                    val aid = skill.skill_id % 1000
                    iconTypes[aid] = skill.icon_type
                    val info = CharacterSkillInfo(
                        skill.skill_id,
                        skill.name ?: "",
                        skill.description,
                        skill.icon_type
                    )
                    info.actions = repository.getSkillActions(skill.getAllActionId())
                    infos.add(info)
                }
                isLoading.postValue(false)
                refresh.postValue(false)
                skills.postValue(infos)
            } catch (e: Exception) {

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

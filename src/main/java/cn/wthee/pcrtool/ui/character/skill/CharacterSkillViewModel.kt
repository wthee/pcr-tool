package cn.wthee.pcrtool.ui.character.skill

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.bean.CharacterSkillInfo
import cn.wthee.pcrtool.data.db.entity.AttackPattern
import cn.wthee.pcrtool.data.db.repository.CharacterRepository
import kotlinx.coroutines.launch


class CharacterSkillViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    companion object {
        var iconType1 = 1
        var iconType2 = 1
    }

    var skills = MutableLiveData<List<CharacterSkillInfo>>()
    var atlPattern = MutableLiveData<List<AttackPattern>>()
    private var refresh = MutableLiveData<Boolean>()
    private var isLoading = MutableLiveData<Boolean>()

    //角色技能信息
    fun getCharacterSkills(id: Int) {
        isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val infos = mutableListOf<CharacterSkillInfo>()
                val data = repository.getCharacterSkill(id)

                //技能信息
                data.getAllSkillId().forEach { sid ->
                    val skill = repository.getSkillData(sid)
                    if (skill.skill_id % 10 == 2) iconType1 = skill.icon_type
                    if (skill.skill_id % 10 == 3) iconType2 = skill.icon_type
                    val info = CharacterSkillInfo(
                        skill.skill_id,
                        skill.name ?: "",
                        skill.description,
                        skill.icon_type
                    )
                    info.actions = repository.getSkillActions(skill.getAllActionId())
                        .filter { it.description.isNotEmpty() }
                    infos.add(info)
                }
                //技能循环
                val pattern = repository.getAttackPattern(id)
                isLoading.postValue(false)
                refresh.postValue(false)
                skills.postValue(infos)
                atlPattern.postValue(pattern)
            } catch (e: Exception) {

            }

        }
    }

    //角色技能循环
    fun getCharacterSkillLoops(id: Int) {
        isLoading.postValue(true)
        viewModelScope.launch {
            //技能循环
            val pattern = repository.getAttackPattern(id)
            atlPattern.postValue(pattern)
        }
    }


}

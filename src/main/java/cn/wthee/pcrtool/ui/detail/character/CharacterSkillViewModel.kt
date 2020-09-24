package cn.wthee.pcrtool.ui.detail.character

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.model.CharacterSkillInfo
import cn.wthee.pcrtool.database.entity.AttackPattern
import kotlinx.coroutines.launch


class CharacterSkillViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    companion object {
        var iconType1 = 1
        var iconType2 = 1
    }

    var skills = MutableLiveData<List<CharacterSkillInfo>>()
    var acttackPattern = MutableLiveData<AttackPattern>()
    private var refresh = MutableLiveData<Boolean>()
    private var isLoading = MutableLiveData<Boolean>()

    //角色技能信息
    fun getCharacterSkills(id: Int) {
        isLoading.postValue(true)
        viewModelScope.launch {
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
            acttackPattern.postValue(pattern)
        }
    }


}

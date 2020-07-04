package cn.wthee.pcrtool.ui.detail.character

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.model.CharacterSkillInfo
import kotlinx.coroutines.launch


class CharacterSkillViewModel internal constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    var skills = MutableLiveData<List<CharacterSkillInfo>>()
    var refresh = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()

    //角色基本资料
    fun getCharacterSkills(id: Int) {
        viewModelScope.launch {
            val infos = mutableListOf<CharacterSkillInfo>()
            val data = repository.getCharacterSkill(id)
            data.getAllSkillId().forEach { sid ->
                val skill = repository.getSkillData(sid)
                val info = CharacterSkillInfo(skill.name ?: "", skill.description, skill.icon_type)
                info.actions = repository.getSkillActions(skill.getAllActionId())
                    .filter { it.description.isNotEmpty() }
                infos.add(info)
            }
            loading.postValue(false)
            refresh.postValue(false)
            skills.postValue(infos)
        }
    }


}

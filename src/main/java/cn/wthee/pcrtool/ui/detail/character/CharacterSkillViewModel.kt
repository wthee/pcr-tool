package cn.wthee.pcrtool.ui.detail.character

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.model.CharacterSkillInfo
import kotlinx.coroutines.launch


class CharacterSkillViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    var skills = MutableLiveData<List<CharacterSkillInfo>>()
    private var refresh = MutableLiveData<Boolean>()
    private var isLoading = MutableLiveData<Boolean>()

    //角色基本资料
    fun getCharacterSkills(id: Int) {
        isLoading.postValue(true)
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
            isLoading.postValue(false)
            refresh.postValue(false)
            skills.postValue(infos)
        }
    }


}

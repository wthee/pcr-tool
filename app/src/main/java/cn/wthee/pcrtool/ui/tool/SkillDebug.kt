package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel

@Composable
fun AllSkillList(
    skillViewModel: SkillViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel()
) {
    val allCharacter =
        characterViewModel.getAllCharacter().collectAsState(initial = arrayListOf()).value
    val skills = skillViewModel.skills.observeAsState()

    LazyColumn {
        if (allCharacter.isNotEmpty()) {
            val ids = arrayListOf<Int>()
            allCharacter.forEach {
                ids.add(it.unitId)
            }
            skillViewModel.getCharacterSkills(201, 1000, ids)
        }
        items(skills.value ?: arrayListOf()) {
            var error = false
            it.getActionInfo().forEach { action ->
                if (action.action.contains("?")) {
                    error = true
                    return@forEach
                }
            }
            if (error) {
                SkillItem(level = 201, skillDetail = it)
            }
        }
    }
}
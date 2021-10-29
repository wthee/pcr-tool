package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@Composable
fun AllSkillList(
    skillViewModel: SkillViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel(),
    clanViewModel: ClanViewModel = hiltViewModel()
) {
    val allCharacter =
        characterViewModel.getAllCharacter().collectAsState(initial = arrayListOf()).value
    val skills = skillViewModel.skills.observeAsState()
    val bossIds = clanViewModel.getAllBossIds().collectAsState(initial = arrayListOf()).value

    Box {
        if (allCharacter.isNotEmpty() && bossIds.isNotEmpty()) {
            val ids = arrayListOf<Int>()
            allCharacter.forEach {
                ids.add(it.unitId)
            }
//            ids.addAll(bossIds)
            skillViewModel.getCharacterSkills(201, 1000, ids)
        }
    }
    skills.value?.let { skillValue ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(skillValue) {
                var error = false
                it.getActionInfo().forEach { action ->
                    if (action.action.contains(Constants.UNKNOWN)) {
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

}
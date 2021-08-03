package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalCoilApi
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
    val errorCount = remember {
        mutableStateOf(0)
    }
    skills.value?.let { skillValue ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(text = errorCount.value.toString())
            }
            items(skillValue) {
                var error = false
                it.getActionInfo().forEach { action ->
                    if (action.action.contains("?")) {
                        error = true
                        errorCount.value += 1
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
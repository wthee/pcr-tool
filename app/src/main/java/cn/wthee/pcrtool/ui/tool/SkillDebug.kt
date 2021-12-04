package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@Composable
fun AllSkillList(
    toSummonDetail: ((Int, Int) -> Unit)? = null,
    skillViewModel: SkillViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel(),
    clanViewModel: ClanViewModel = hiltViewModel()
) {
    val allCharacter =
        characterViewModel.getAllCharacter().collectAsState(initial = arrayListOf()).value
    val skills = skillViewModel.skills.observeAsState()
    val bossIds = clanViewModel.getAllBossIds().collectAsState(initial = arrayListOf()).value

    val type = remember {
        mutableStateOf(1)
    }
    val typeChips = arrayListOf(
        ChipData(0, "角色"),
        ChipData(1, "Boss")
    )


    if (allCharacter.isNotEmpty() && bossIds.isNotEmpty()) {
        val ids = arrayListOf<Int>()
        if (type.value == 0) {
            ids.clear()
            allCharacter.forEach {
                ids.add(it.unitId)
            }
            skillViewModel.getCharacterSkills(201, 1000, ids)
        } else {
            ids.clear()
            ids.addAll(bossIds)
            skillViewModel.getCharacterSkills(201, 1000, ids)
        }

    }

    Column(
        modifier = Modifier
            .padding(Dimen.largePadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        skills.value?.let { skillValue ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(skillValue) {
                    if (type.value == 0 || (type.value == 1 && it.skillId > 3000000)) {
                        var error = false
                        it.getActionInfo().forEach { action ->
                            if (action.action.contains("?")) {
                                error = true
                                return@forEach
                            }
                        }
                        if (error) {
                            SkillItem(
                                1,
                                skillDetail = it,
                                unitType = 0,
                                toSummonDetail = toSummonDetail
                            )
                        }
                    }

                }
            }
        }
    }


}
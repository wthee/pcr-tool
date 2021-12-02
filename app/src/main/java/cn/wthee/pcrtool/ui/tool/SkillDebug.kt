package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.ui.common.ChipGroup
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.outlinedTextFieldColors
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@Composable
fun AllSkillList(
    toSummonDetail: ((Int, Boolean) -> Unit)? = null,
    skillViewModel: SkillViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel(),
    clanViewModel: ClanViewModel = hiltViewModel()
) {
    val allCharacter =
        characterViewModel.getAllCharacter().collectAsState(initial = arrayListOf()).value
    val skills = skillViewModel.skills.observeAsState()
    val bossIds = clanViewModel.getAllBossIds().collectAsState(initial = arrayListOf()).value
    val searchTextState = remember {
        mutableStateOf(Constants.UNKNOWN)
    }
    val keyword = remember {
        mutableStateOf(Constants.UNKNOWN)
    }
    val loading = remember {
        mutableStateOf(true)
    }
    val type = remember {
        mutableStateOf(0)
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
        OutlinedTextField(
            value = searchTextState.value,
            colors = outlinedTextFieldColors(),
            onValueChange = {
                searchTextState.value = it
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyword.value = searchTextState.value
                }
            ),
        )

        ChipGroup(items = typeChips, selectIndex = type)
        FadeAnimation(visible = loading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(Dimen.largePadding)
                    .size(Dimen.fabIconSize),
                color = MaterialTheme.colorScheme.primary,
            )
        }
        skills.value?.let { skillValue ->
            loading.value = false
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(skillValue) {
                    var error = false
                    it.getActionInfo().forEach { action ->
                        if (action.action.contains(keyword.value)) {
                            error = true
                            return@forEach
                        }
                    }
                    if (error) {
                        SkillItem(
                            level = 201,
                            skillDetail = it,
                            isEnemy = false,
                            toSummonDetail = toSummonDetail
                        )
                    }
                }
            }
        }
    }


}
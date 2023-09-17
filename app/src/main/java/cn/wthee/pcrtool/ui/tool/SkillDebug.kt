package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.EnemyViewModel
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllSkillList(
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
    skillViewModel: SkillViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel(),
    enemyViewModel: EnemyViewModel = hiltViewModel(),
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel(),
) {
    //所有角色
    val allCharacterFlow = remember {
        characterViewModel.getAllCharacter()
    }
    val allCharacter by allCharacterFlow.collectAsState(initial = arrayListOf())

    //所有boss
    val bossIdsFlow = remember {
        enemyViewModel.getAllBossIds()
    }
    val bossIds by bossIdsFlow.collectAsState(initial = arrayListOf())

    //所有ex装备
    val exEquipSkillIdsFlow = remember {
        extraEquipmentViewModel.getAllEquipSkillIdList()
    }
    val exEquipSkillIds by exEquipSkillIdsFlow.collectAsState(initial = arrayListOf())

    val ids = arrayListOf<Int>()
    allCharacter.forEach {
        ids.add(it.unitId)
    }
    ids.addAll(bossIds)

    //技能
    val skillsFlow = remember(ids) {
        skillViewModel.getCharacterSkills(201, 1000, ids.distinct())
    }
    val skills = skillsFlow.collectAsState(initial = arrayListOf()).value

    //装备技能
    val equipSkillsFlow = remember(exEquipSkillIds) {
        skillViewModel.getExtraEquipPassiveSkills(exEquipSkillIds)
    }
    val equipSkills by equipSkillsFlow.collectAsState(initial = arrayListOf())


    Column(
        modifier = Modifier
            .padding(Dimen.largePadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(state = rememberPagerState { 2 }) { index ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    MainText(text = "$index：${if (index == 0) skills.size else equipSkills.size}")
                }
                items(
                    items = if (index == 0) skills else equipSkills,
                    key = {
                        it.skillId
                    }
                ) { skillDetail ->
                    var error = false
                    skillDetail.getActionInfo().forEach { action ->
                        if (action.action.contains("?")) {
                            error = true
                            return@forEach
                        }
                    }
                    if (error) {
                        SkillItem(
                            skillDetail = skillDetail,
                            unitType = UnitType.CHARACTER,
                            toSummonDetail = toSummonDetail,
                            property = CharacterProperty(100, 1, 1)
                        )
                    }
                }
            }
        }
    }


}
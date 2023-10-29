package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.skill.SkillLoopScreen
import cn.wthee.pcrtool.ui.theme.Dimen

/**
 * 角色技能循环（嵌入或使用bottomSheet跳转）
 *
 * @param scrollable 是否可滚动
 */
@Composable
fun CharacterSkillLoopScreen(
    unitId: Int,
    scrollable: Boolean,
    characterSkillLoopViewModel: CharacterSkillLoopViewModel = hiltViewModel()
) {
    val uiState by characterSkillLoopViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(unitId){
        characterSkillLoopViewModel.loadData(unitId)
    }


    if (!scrollable) {
        //技能循环
        MainText(
            text = stringResource(R.string.skill_loop),
            modifier = Modifier
                .padding(top = Dimen.largePadding)
        )
    }

    MainScaffold(
        hideMainFab = !scrollable,
        fillMaxSize = false
    ) {
        SkillLoopScreen(
            loopData = uiState.attackPatternList,
            modifier = Modifier
                .padding(Dimen.largePadding),
            unitType = UnitType.CHARACTER,
            scrollable = scrollable
        )
    }

}
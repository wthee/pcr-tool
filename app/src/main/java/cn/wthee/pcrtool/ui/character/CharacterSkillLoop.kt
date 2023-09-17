package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.SkillViewModel

/**
 * 角色技能循环
 */
@Composable
fun CharacterSkillLoop(
    unitId: Int,
    scrollable: Boolean,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    //技能循环
    val loopDataFlow = remember(unitId) {
        skillViewModel.getCharacterSkillLoops(unitId)
    }
    val loopData by loopDataFlow.collectAsState(initial = arrayListOf())

    if (!scrollable) {
        //技能循环
        MainText(
            text = stringResource(R.string.skill_loop),
            modifier = Modifier
                .padding(top = Dimen.largePadding)
        )
    }

    SkillLoopList(
        loopData,
        modifier = Modifier
            .padding(Dimen.largePadding),
        unitType = UnitType.CHARACTER,
        scrollable = scrollable
    )
}
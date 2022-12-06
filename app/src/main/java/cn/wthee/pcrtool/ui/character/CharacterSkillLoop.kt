package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.SkillViewModel

/**
 * 角色技能循环
 */
@Composable
fun CharacterSkillLoop(
    unitId: Int,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    //技能循环
    val loopData =
        skillViewModel.getCharacterSkillLoops(unitId).collectAsState(initial = arrayListOf()).value

    SkillLoopList(
        loopData,
        modifier = Modifier.padding(Dimen.largePadding),
        unitType = UnitType.CHARACTER
    )
}
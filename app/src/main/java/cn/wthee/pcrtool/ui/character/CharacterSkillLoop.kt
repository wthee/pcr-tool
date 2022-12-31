package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.SlideAnimation
import cn.wthee.pcrtool.utils.spanCount
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import kotlin.math.ceil

/**
 * 角色技能循环
 * fixme bottomsheet 无法正常弹出问题，官方预计23年1月份发布更新
 * @see <a href="https://github.com/google/accompanist/issues/772">临时解决办法：设置初始高度</a>
 */
@Composable
fun CharacterSkillLoop(
    unitId: Int,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    //技能循环
    val loopData =
        skillViewModel.getCharacterSkillLoops(unitId).collectAsState(initial = arrayListOf()).value

    SlideAnimation(loopData.isNotEmpty()) {
        val spanCount = (Dimen.iconSize + Dimen.largePadding * 2).spanCount
        var rowNum = 0
        loopData.forEach {
            rowNum += ceil(it.getBefore().size * 1.0 / spanCount).toInt()
            rowNum += ceil(it.getLoop().size * 1.0 / spanCount).toInt()
        }
        val height =
            (Dimen.iconSize + Dimen.largePadding * 3 + Dimen.mediumPadding * 2) * rowNum + Dimen.fabSize + Dimen.fabMargin + Dimen.mediumPadding

        SkillLoopList(
            loopData,
            modifier = Modifier
                .padding(Dimen.largePadding)
                .height(height),
            unitType = UnitType.CHARACTER
        )
    }
}
package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.AttackPattern
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.SkillLoop
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.MainTitleText
import cn.wthee.pcrtool.ui.common.VerticalGrid
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper


/**
 * 技能循环
 */
@Composable
fun SkillLoopList(
    loopData: List<AttackPattern>,
    iconTypes: HashMap<Int, Int>,
    modifier: Modifier = Modifier,
    unitType: UnitType
) {
    val loops = arrayListOf<SkillLoop>()
    loopData.forEach { attackPattern ->
        if (attackPattern.getBefore().size > 0) {
            loops.add(SkillLoop(stringResource(R.string.before_loop), attackPattern.getBefore()))
        }
        if (attackPattern.getLoop().size > 0) {
            loops.add(SkillLoop(stringResource(R.string.looping), attackPattern.getLoop()))
        }
    }
    Column(
        modifier = if (unitType == UnitType.CHARACTER) modifier.verticalScroll(rememberScrollState()) else modifier
    ) {
        if (loops.isNotEmpty()) {
            loops.forEach {
                SkillLoopItem(loop = it, iconTypes)
            }
        }
        if (unitType == UnitType.CHARACTER) {
            CommonSpacer()
        }
    }
}

/**
 * 技能循环 item
 */
@Composable
private fun SkillLoopItem(loop: SkillLoop, iconTypes: HashMap<Int, Int>) {
    Column {
        MainTitleText(text = loop.loopTitle)
        SkillLoopIconList(loop.loopList, iconTypes)
    }
}

/**
 * 技能循环图标列表
 */
@Composable
private fun SkillLoopIconList(
    iconList: List<Int>,
    iconTypes: HashMap<Int, Int>
) {
    VerticalGrid(
        modifier = Modifier.padding(top = Dimen.mediumPadding),
        maxColumnWidth = Dimen.iconSize + Dimen.largePadding * 2
    ) {
        iconList.forEach {
            val type: String
            val url: Any
            if (it == 1) {
                type = "普攻"
                url = R.drawable.unknown_item
            } else {
                type = when (it / 1000) {
                    1 -> "技能 ${it % 10}"
                    2 -> "SP技能 ${it % 10}"
                    else -> ""
                }
                val iconType = when (it) {
                    1001 -> iconTypes[2]
                    1002 -> iconTypes[3]
                    1003 -> iconTypes[1]
                    in 1004..1100 -> iconTypes[it % 10]
                    in 2001..2100 -> iconTypes[100 + it % 10]
                    else -> null
                }
                url = if (iconType == null) {
                    R.drawable.unknown_item
                } else {
                    ImageResourceHelper.getInstance()
                        .getUrl(ImageResourceHelper.ICON_SKILL, iconType)
                }
            }

            Column(
                modifier = Modifier
                    .padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCompose(data = url)
                Text(
                    text = type,
                    color = getSkillColor(type = type),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = Dimen.smallPadding)
                )
            }
        }
    }
}
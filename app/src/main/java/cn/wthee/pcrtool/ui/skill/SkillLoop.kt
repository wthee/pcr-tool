package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.AttackPattern
import cn.wthee.pcrtool.data.db.view.SkillBasicData
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.SkillLoop
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.EnemyViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel


/**
 * 技能循环
 */
@Composable
fun SkillLoopList(
    loopData: List<AttackPattern>,
    modifier: Modifier = Modifier,
    unitType: UnitType,
    skillViewModel: SkillViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel(),
    enemyViewModel: EnemyViewModel = hiltViewModel(),
) {
    val loops = arrayListOf<SkillLoop>()
    val loopList = arrayListOf<Int>()
    var unitId = 0

    loopData.forEach { attackPattern ->
        if (attackPattern.getBefore().size > 0) {
            loopList.addAll(attackPattern.getBefore())
            loops.add(
                SkillLoop(
                    attackPattern.unitId,
                    attackPattern.patternId,
                    stringResource(R.string.before_loop),
                    attackPattern.getBefore()
                )
            )
        }
        if (attackPattern.getLoop().size > 0) {
            loopList.addAll(attackPattern.getLoop())
            loops.add(
                SkillLoop(
                    attackPattern.unitId,
                    attackPattern.patternId,
                    stringResource(R.string.looping),
                    attackPattern.getLoop()
                )
            )
        }
        unitId = attackPattern.unitId
    }
    //获取循环对应的图标
    val skillLoopList =
        skillViewModel.getSkillIconTypes(loopList, unitId).collectAsState(initial = hashMapOf())
            .value
    //获取普攻时间
    val atkCastTime = if (unitType == UnitType.CHARACTER || unitType == UnitType.CHARACTER_SUMMON) {
        characterViewModel.getAtkCastTime(unitId).collectAsState(initial = 0.0).value
    } else {
        enemyViewModel.getAtkCastTime(unitId).collectAsState(initial = 0.0).value
    }

    Column(
        modifier = if (unitType == UnitType.CHARACTER) {
            modifier.verticalScroll(rememberScrollState())
        } else {
            modifier
        }
    ) {
        if (loops.isNotEmpty()) {
            loops.forEach {
                SkillLoopItem(loop = it, skillLoopList, atkCastTime)
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
private fun SkillLoopItem(
    loop: SkillLoop,
    skillMap: HashMap<Int, SkillBasicData>,
    atkCastTime: Double
) {
    MainTitleText(text = loop.loopTitle)
    SkillLoopIconList(loop.loopList.filter { it != 0 }, skillMap, atkCastTime)
}

/**
 * 技能循环图标列表V2
 */
@Composable
private fun SkillLoopIconList(
    loopList: List<Int>,
    skillMap: HashMap<Int, SkillBasicData>,
    atkCastTime: Double
) {

    VerticalGrid(
        modifier = Modifier.padding(top = Dimen.mediumPadding),
        itemWidth = Dimen.iconSize,
        contentPadding = Dimen.mediumPadding
    ) {
        loopList.forEach {
            val type: String
            val url: Any
            val skillBasicData = skillMap[it]
            //准备时间
            val castTime =
                (skillBasicData?.skillCastTime ?: atkCastTime).toBigDecimal().stripTrailingZeros()
                    .toPlainString()

            if (it == 1) {
                type = stringResource(id = R.string.normal_attack)
                url = R.drawable.unknown_item
            } else {
                type = when (it / 1000) {
                    1 -> stringResource(id = R.string.skill_index, it % 10)
                    2 -> "SP" + stringResource(id = R.string.skill_index, it % 10)
                    else -> ""
                }

                url = if (skillBasicData == null) {
                    R.drawable.unknown_item
                } else {
                    ImageRequestHelper.getInstance()
                        .getUrl(ImageRequestHelper.ICON_SKILL, skillBasicData.iconType)
                }
            }

            Column(
                modifier = Modifier
                    .padding(bottom = Dimen.mediumPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainIcon(data = url)
                Text(
                    text = type,
                    color = getSkillColor(type = type),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = Dimen.smallPadding)
                )

                //准备时间
                Text(
                    text = castTime,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = Dimen.smallPadding)
                )
            }
        }
    }
}


@CombinedPreviews
@Composable
private fun SkillLoopItemPreview() {
    PreviewLayout {
        SkillLoopItem(
            loop = SkillLoop(
                0,
                1,
                stringResource(R.string.before_loop),
                arrayListOf(1001, 1002)
            ),
            hashMapOf(),
            0.0
        )
    }
}
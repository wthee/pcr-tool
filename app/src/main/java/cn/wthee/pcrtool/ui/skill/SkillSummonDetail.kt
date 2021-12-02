package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.BossSkillList
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import cn.wthee.pcrtool.viewmodel.SummonViewModel
import kotlin.math.max

/**
 * 技能召唤物信息
 */
@Composable
fun SummonDetail(
    unitId: Int,
    isEnemy: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (isEnemy) {
            EnemySummonDetail(unitId)
        } else {
            CharacterSummonDetail(unitId)
        }
    }

}

/**
 * 角色召唤物信息
 */
@Composable
private fun CharacterSummonDetail(
    unitId: Int,
    attrViewModel: CharacterAttrViewModel = hiltViewModel(),
    summonViewModel: SummonViewModel = hiltViewModel(),
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    //基本信息
    val basicInfo = summonViewModel.getSummonData(unitId).collectAsState(initial = null).value

    //数值信息
    val currValue = navViewModel.currentValue.observeAsState().value
    val attrs =
        attrViewModel.getCharacterInfo(unitId, currValue).collectAsState(initial = null).value

    //技能信息
    val loopData =
        skillViewModel.getCharacterSkillLoops(unitId)
            .collectAsState(initial = arrayListOf()).value
    val iconTypes = skillViewModel.iconTypes.observeAsState().value ?: hashMapOf()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        basicInfo?.let {
            //名称
            MainText(
                text = it.unitName,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.mediumPadding),
                selectable = true
            )
            //等级等属性
            CaptionText(
                text = "等级：${currValue?.level ?: "0"} / Rank：${currValue?.rank ?: "0"} / 星级：${currValue?.rarity ?: "0"}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            //位置
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(Dimen.mediumPadding)
            ) {
                PositionIcon(position = it.position)
                MainText(
                    text = it.position.toString(),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
        }
        attrs?.let {
            Column {
                AttrList(attrs = it.sumAttr.summonAttr())
                //技能循环
                SkillLoopList(
                    loopData,
                    iconTypes,
                    isEnemy = true,
                    modifier = Modifier.padding(Dimen.mediumPadding * 2)
                )
                //技能信息
                SkillCompose(
                    unitId = unitId,
                    cutinId = 0,
                    level = currValue?.level ?: 0,
                    atk = max(it.sumAttr.atk, it.sumAttr.magicStr).int,
                    isEnemy = false
                )
                CommonSpacer()
            }
        }
    }
}

/**
 * 敌人召唤物信息
 */
@Composable
private fun EnemySummonDetail(
    unitId: Int,
    clanViewModel: ClanViewModel = hiltViewModel(),
) {
    val enemyInfo = clanViewModel.getEnemyAttr(unitId).collectAsState(initial = null).value

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        enemyInfo?.let { enemyData ->
            //名称
            MainText(
                text = enemyData.name,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.mediumPadding),
                selectable = true
            )
            //等级
            CaptionText(
                text = enemyData.level.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            //描述
            val desc = enemyData.getDesc()
            Subtitle2(
                text = desc,
                modifier = Modifier.padding(
                    horizontal = Dimen.largePadding,
                    vertical = Dimen.mediumPadding
                )
            )
            //属性
            val attr = enemyData.attr.enemy()
            AttrList(attrs = attr)
            BossSkillList(0, arrayListOf(enemyData))
            CommonSpacer()
        }
    }


}

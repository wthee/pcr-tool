package cn.wthee.pcrtool.ui.tool.enemy

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.copyText
import cn.wthee.pcrtool.viewmodel.EnemyViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel


/**
 * 怪物信息详情
 */
@Composable
fun EnemyDetail(
    enemyId: Int,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
    enemyViewModel: EnemyViewModel = hiltViewModel()
) {
    val enemyData = enemyViewModel.getEnemyAttr(enemyId).collectAsState(initial = null).value
    val partEnemyList =
        enemyViewModel.getMutiTargetEnemyInfo(enemyId).collectAsState(initial = null).value

    Box(modifier = Modifier.fillMaxSize()) {
        enemyData?.let {
            EnemyAllInfo(
                enemyData,
                partEnemyList != null,
                partEnemyList,
                toSummonDetail
            )
        }
    }

}

/**
 * Boss 信息详情
 */
@Composable
fun EnemyAllInfo(
    enemyData: EnemyParameterPro,
    isMultiEnemy: Boolean,
    partEnemyList: List<EnemyParameterPro>?,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
) {
    val context = LocalContext.current
    val expanded = remember {
        mutableStateOf(false)
    }
    val attr = if (isMultiEnemy) {
        enemyData.attr.multiplePartEnemy()
    } else {
        enemyData.attr.enemy()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        //图标，非会战boss
        if (enemyData.enemyId > 600000000) {
            IconCompose(
                data = ImageResourceHelper.getInstance()
                    .getUrl(
                        ImageResourceHelper.ICON_UNIT,
                        enemyData.unitIconId
                    ),
                modifier = Modifier
                    .padding(vertical = Dimen.mediumPadding)
                    .align(Alignment.CenterHorizontally)
            )
        }
        if (BuildConfig.DEBUG) {
            MainText(text = "${enemyData.enemyId}/${enemyData.unitId}/${enemyData.prefabId}/${enemyData.unitIconId}")
        }
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
        //模型预览
        IconTextButton(
            icon = MainIconType.PREVIEW_UNIT_SPINE,
            text = stringResource(id = R.string.spine_preview),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            BrowserUtil.open(
                context,
                Constants.PREVIEW_ENEMY_URL + enemyData.prefabId
            )
        }
        //描述
        Text(
            text = enemyData.getDesc(),
            maxLines = if (expanded.value) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .animateContentSize()
                .padding(Dimen.mediumPadding)
                .clickable {
                    expanded.value = !expanded.value
                }
        )
        if (expanded.value) {
            IconTextButton(
                icon = MainIconType.COPY,
                text = stringResource(id = R.string.copy),
                modifier = Modifier
                    .padding(bottom = Dimen.mediumPadding)
                    .align(Alignment.CenterHorizontally)
            ) {
                copyText(context, enemyData.getDesc())
            }
        }
        //属性
        AttrList(attrs = attr)
        //多目标部位属性
        partEnemyList?.forEach {
            //名称
            MainText(
                text = it.name,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.largePadding),
                selectable = true
            )
            //属性
            AttrList(attrs = it.attr.enemy())
        }
        MainText(
            text = stringResource(R.string.skill),
            modifier = Modifier
                .padding(top = Dimen.largePadding + Dimen.mediumPadding)
                .align(Alignment.CenterHorizontally)
        )
        //技能
        EnemySkillList(enemyData, UnitType.ENEMY, toSummonDetail)
        CommonSpacer()
    }

}


/**
 * Boss 技能信息
 */
@Composable
fun EnemySkillList(
    enemyData: EnemyParameterPro,
    unitType: UnitType,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    val allSkillList =
        skillViewModel.getAllEnemySkill(enemyData).collectAsState(initial = null).value
    val allLoopData =
        skillViewModel.getAllSkillLoops(enemyData).collectAsState(initial = null).value
    val allIcon = skillViewModel.getAllEnemySkillLoopIcon(enemyData)
        .collectAsState(initial = null).value


    Column(
        modifier = Modifier
            .padding(Dimen.largePadding)
            .fillMaxSize()
    ) {
        if (allLoopData != null && allIcon != null) {
            SkillLoopList(
                allLoopData,
                allIcon,
                unitType = unitType
            )
        }
        Spacer(modifier = Modifier.padding(top = Dimen.largePadding))
        allSkillList?.let {
            it.forEachIndexed { index, skillDetail ->
                SkillItem(
                    skillIndex = index,
                    skillDetail = skillDetail,
                    unitType = unitType,
                    toSummonDetail = toSummonDetail
                )
            }
        }
    }
}
package cn.wthee.pcrtool.ui.tool.enemy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import cn.wthee.pcrtool.utils.*
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
        //图标，仅剧情活动boss显示
        if (enemyData.enemyId.toString()[0] == '6') {
            IconCompose(
                data = ImageResourceHelper.getInstance()
                    .getUrl(
                        ImageResourceHelper.ICON_UNIT,
                        enemyData.prefabId
                    ),
                modifier = Modifier
                    .padding(vertical = Dimen.mediumPadding)
                    .align(Alignment.CenterHorizontally)
            )
        }
        if (BuildConfig.DEBUG) {
            Subtitle2(
                text = "${enemyData.enemyId}/${enemyData.unitId}/${enemyData.prefabId}",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
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
        MainContentText(
            text = enemyData.getDesc(),
            maxLines = 2,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(Dimen.mediumPadding)
                .clickable {
                    VibrateUtil(context).single()
                    expanded.value = !expanded.value
                }
        )
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
        //技能
        EnemySkillList(enemyData, UnitType.ENEMY, toSummonDetail)
        CommonSpacer()
    }

    if (expanded.value) {
        AlertDialog(
            title = {
                Column(
                    modifier = Modifier
                        .heightIn(max = Dimen.minSheetHeight)
                        .verticalScroll(rememberScrollState())
                ) {
                    MainContentText(
                        text = enemyData.getDesc(),
                        textAlign = TextAlign.Start,
                        selectable = true
                    )
                }
            },
            modifier = Modifier.padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding),
            onDismissRequest = {
                expanded.value = false
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
            confirmButton = {
                //确认下载
                MainButton(text = stringResource(R.string.copy_all)) {
                    copyText(context, enemyData.getDesc())
                    expanded.value = false
                }
            },
            dismissButton = {
                //取消
                SubButton(
                    text = stringResource(id = R.string.cancel)
                ) {
                    expanded.value = false
                }
            })
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

    Column(
        modifier = Modifier
            .padding(Dimen.largePadding)
            .fillMaxSize()
    ) {
        if (allSkillList?.isNotEmpty() == true || allLoopData?.isNotEmpty() == true) {
            MainText(
                text = stringResource(R.string.skill),
                modifier = Modifier
                    .padding(top = Dimen.largePadding + Dimen.mediumPadding)
                    .align(Alignment.CenterHorizontally)
            )
        }
        if (allLoopData != null) {
            SkillLoopList(
                allLoopData,
                unitType = unitType
            )
        }
        Spacer(modifier = Modifier.padding(top = Dimen.largePadding))
        allSkillList?.let { skillList ->
            skillList.forEach { skillDetail ->
                SkillItem(
                    skillDetail = skillDetail,
                    unitType = unitType,
                    toSummonDetail = toSummonDetail
                )
            }
        }
    }
}
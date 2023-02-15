package cn.wthee.pcrtool.ui.tool.enemy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
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
    val openDialog = remember {
        mutableStateOf(false)
    }
    val attr = if (isMultiEnemy) {
        enemyData.attr.multiplePartEnemy(isPreview = LocalInspectionMode.current)
    } else {
        enemyData.attr.enemy(isPreview = LocalInspectionMode.current)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        //图标，仅剧情活动boss显示
        if (enemyData.enemyId.toString()[0] == '6') {
            IconCompose(
                data = ImageRequestHelper.getInstance()
                    .getUrl(
                        ImageRequestHelper.ICON_UNIT,
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
            text = stringResource(id = R.string.unit_level, enemyData.level),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        //模型预览
        IconTextButton(
            icon = MainIconType.PREVIEW_UNIT_SPINE,
            text = stringResource(id = R.string.spine_preview),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            BrowserUtil.open(Constants.PREVIEW_ENEMY_URL + enemyData.prefabId)
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
                    openDialog.value = !openDialog.value
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
        //技能，预览时隐藏
        if (!LocalInspectionMode.current) {
            EnemySkillList(enemyData, UnitType.ENEMY, toSummonDetail)
        }
        CommonSpacer()
    }

    if (openDialog.value) {
        AlertDialog(
            title = {
                Column(
                    modifier = Modifier
                        .heightIn(max = ScreenUtil.getHeight().px2dp.dp * 0.618f)
                        .verticalScroll(rememberScrollState())
                ) {
                    MainContentText(
                        text = enemyData.getDesc(),
                        textAlign = TextAlign.Start,
                        selectable = true
                    )
                }
            },
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                //复制
                MainButton(text = stringResource(R.string.copy_all)) {
                    copyText(context, enemyData.getDesc())
                    openDialog.value = false
                }
            },
            dismissButton = {
                //取消
                SubButton(
                    text = stringResource(id = R.string.cancel)
                ) {
                    openDialog.value = false
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


@CombinedPreviews
@Composable
private fun EnemyAllInfoPreview() {
    PreviewLayout {
        EnemyAllInfo(
            EnemyParameterPro(
                name = stringResource(id = R.string.debug_short_text),
                comment = stringResource(id = R.string.debug_long_text),
                level = 100
            ),
            false,
            null,
            null
        )
    }
}
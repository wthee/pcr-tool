package cn.wthee.pcrtool.ui.tool.enemy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.AttackPattern
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainButton
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.SubButton
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.skill.SkillItemContent
import cn.wthee.pcrtool.ui.skill.SkillLoopScreen
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.copyText
import cn.wthee.pcrtool.utils.px2dp


/**
 * 怪物信息详情
 */
@Composable
fun EnemyDetailScreen(
    enemyId: Int,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
    enemyDetailViewModel: EnemyDetailViewModel = hiltViewModel()
) {
    val uiState by enemyDetailViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(enemyId) {
        enemyDetailViewModel.loadData(enemyId)
    }


    MainScaffold {
        uiState.enemyInfo?.let {
            EnemyDetailContent(
                enemyData = it,
                isMultiEnemy = uiState.partInfoList.isNotEmpty(),
                partEnemyList = uiState.partInfoList,
                skillList = uiState.skillList,
                attackPatternList = uiState.attackPatternList,
                toSummonDetail = toSummonDetail
            )
        }
    }

}

/**
 * Boss 信息详情
 */
@Composable
fun EnemyDetailContent(
    enemyData: EnemyParameterPro,
    isMultiEnemy: Boolean,
    partEnemyList: List<EnemyParameterPro>?,
    skillList: List<SkillDetail>?,
    attackPatternList: List<AttackPattern>?,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
) {
    val context = LocalContext.current
    val openDialog = remember {
        mutableStateOf(false)
    }
    //基础或部位信息
    val attrList = if (isMultiEnemy) {
        enemyData.attr.multiplePartEnemy(isPreview = LocalInspectionMode.current)
    } else {
        enemyData.attr.enemy(isPreview = LocalInspectionMode.current)
    }
    //部位最大攻击力
    var partAtk = 0
    partEnemyList?.forEach {
        partAtk = maxOf(partAtk, maxOf(it.attr.atk, it.attr.magicStr))
    }
    enemyData.partAtk = partAtk


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        //图标，仅剧情活动boss显示
        if (enemyData.enemyId.toString()[0] == '6') {
            MainIcon(
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
        AttrList(attrs = attrList)
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
            EnemySkillList(
                skillList = skillList,
                attackPatternList = attackPatternList,
                unitType = UnitType.ENEMY,
                toSummonDetail = toSummonDetail
            )
        }
        CommonSpacer()
    }

    //描述文本弹窗
    if (openDialog.value) {
        AlertDialog(
            title = {
                Column(
                    modifier = Modifier
                        .heightIn(max = ScreenUtil.getHeight().px2dp.dp * RATIO_GOLDEN)
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
    skillList: List<SkillDetail>?,
    attackPatternList: List<AttackPattern>?,
    unitType: UnitType,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
) {

    Column(
        modifier = Modifier
            .padding(Dimen.largePadding)
            .fillMaxSize()
    ) {
        //技能循环
        attackPatternList?.let {
            MainText(
                text = stringResource(R.string.skill_loop),
                modifier = Modifier
                    .padding(top = Dimen.largePadding)
                    .align(Alignment.CenterHorizontally)
            )
            SkillLoopScreen(
                attackPatternList = it,
                unitType = unitType,
                modifier = Modifier
                    .padding(top = Dimen.mediumPadding)
            )
        }

        //技能信息
        if (skillList?.isNotEmpty() == true || attackPatternList?.isNotEmpty() == true) {
            MainText(
                text = stringResource(R.string.skill),
                modifier = Modifier
                    .padding(top = Dimen.largePadding + Dimen.mediumPadding)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.padding(top = Dimen.largePadding))

        skillList?.let {
            skillList.filter { it.level > 0 }.forEach { skillDetail ->
                SkillItemContent(
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
private fun EnemyDetailContentPreview() {
    PreviewLayout {
        EnemyDetailContent(
            EnemyParameterPro(
                name = stringResource(id = R.string.debug_short_text),
                comment = stringResource(id = R.string.debug_long_text),
                level = 100
            ),
            false,
            null,
            skillList = arrayListOf(),
            attackPatternList = arrayListOf(),
            toSummonDetail = { _, _, _, _, _ -> }
        )
    }
}
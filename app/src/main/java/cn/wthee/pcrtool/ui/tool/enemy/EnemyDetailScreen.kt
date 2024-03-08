package cn.wthee.pcrtool.ui.tool.enemy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.AttackPattern
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.db.view.EnemyTalentWeaknessData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.TalentType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainAlertDialog
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.Tag
import cn.wthee.pcrtool.ui.skill.SkillItemContent
import cn.wthee.pcrtool.ui.skill.loop.SkillLoopScreen
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
import kotlin.math.abs


/**
 * 怪物信息详情
 */
@Composable
fun EnemyDetailScreen(
    enemyId: Int,
    toSummonDetail: ((String) -> Unit)? = null,
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
                partEnemyList = uiState.partInfoList,
                weaknessData = uiState.weaknessData,
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
    partEnemyList: List<EnemyParameterPro>,
    weaknessData: EnemyTalentWeaknessData?,
    skillList: List<SkillDetail>?,
    attackPatternList: List<AttackPattern>?,
    toSummonDetail: ((String) -> Unit)? = null,
) {
    val context = LocalContext.current
    val openDialog = remember {
        mutableStateOf(false)
    }

    //是否多目标
    val isMultiEnemy = partEnemyList.isNotEmpty()

    //基础或部位信息
    val attrList = if (isMultiEnemy) {
        enemyData.attr.multiplePartEnemy(context)
    } else {
        enemyData.attr.enemy(context)
    }
    //部位最大攻击力
    var partAtk = 0
    partEnemyList.forEach {
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

        //弱点属性
        weaknessData?.let {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnemyWeaknessContent(weaknessData)
            }
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
                .align(Alignment.CenterHorizontally),
            onClick = {
                BrowserUtil.open(Constants.PREVIEW_ENEMY_URL + enemyData.prefabId)
            }
        )
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
        partEnemyList.forEach {
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
    MainAlertDialog(
        modifier = Modifier
            .heightIn(max = px2dp(context, ScreenUtil.getHeight()) * RATIO_GOLDEN),
        openDialog = openDialog,
        title = stringResource(id = R.string.description),
        content = {
            SelectionContainer(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = enemyData.getDesc()
                )
            }
        },
        confirmText = stringResource(R.string.copy_all),
        dismissText = stringResource(R.string.cancel),
        onConfirm = {
            copyText(context, enemyData.getDesc())
        }
    )
}


/**
 * Boss 技能信息
 */
@Composable
fun EnemySkillList(
    skillList: List<SkillDetail>?,
    attackPatternList: List<AttackPattern>?,
    unitType: UnitType,
    toSummonDetail: ((String) -> Unit)? = null,
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

/**
 * 弱点属性
 *
 * @param showText 是否显示具体数值或圆点
 */
@Composable
fun EnemyWeaknessContent(
    weaknessData: EnemyTalentWeaknessData,
    showText: Boolean = true
) {
    val weaknessList = weaknessData.getWeaknessList()

    TalentType.entries.forEachIndexed { index, talentType ->
        if (index != 0) {
            val value = weaknessList[index - 1] - 100
            if (value != 0) {
                if (showText) {
                    //显示文本数值
                    val valueText = (if (value > 0) {
                        "+"
                    } else {
                        "-"
                    }) + abs(value) + "%"

                    Tag(
                        text = stringResource(id = talentType.typeNameId) + valueText,
                        backgroundColor = talentType.color,
                        modifier = Modifier.padding(start = Dimen.exSmallPadding)
                    )
                } else {
                    //显示圆点
                    Box(
                        modifier = Modifier
                            .padding(
                                start = Dimen.exSmallPadding,
                                end = Dimen.exSmallPadding,
                                top = Dimen.smallPadding
                            )
                            .background(
                                color = talentType.color,
                                shape = CircleShape
                            )
                            .size(Dimen.indicatorSize)
                    )
                }

            }
        }
    }
    if (showText) {
        val openDialog = remember {
            mutableStateOf(false)
        }
        IconTextButton(
            text = "",
            icon = MainIconType.HELP,
            onClick = {
                openDialog.value = true
            }
        )

        MainAlertDialog(
            openDialog = openDialog,
            title = stringResource(id = R.string.talent_weakness),
            text = stringResource(id = R.string.talent_weakness_tip)
        )
    }
}

/**
 * @see [SkillLoopScreen] 技能循环预览
 * @see [cn.wthee.pcrtool.ui.skill.SkillListScreen] 技能列表预览
 */
@CombinedPreviews
@Composable
private fun EnemyDetailContentPreview() {
    PreviewLayout {
        EnemyDetailContent(
            enemyData = EnemyParameterPro(
                name = stringResource(id = R.string.debug_short_text),
                comment = stringResource(id = R.string.debug_long_text),
                level = 100
            ),
            partEnemyList = arrayListOf(),
            skillList = arrayListOf(),
            attackPatternList = arrayListOf(),
            weaknessData = EnemyTalentWeaknessData(),
            toSummonDetail = {}
        )
    }
}
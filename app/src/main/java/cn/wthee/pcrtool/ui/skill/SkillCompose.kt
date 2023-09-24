package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.db.view.SpSkillLabelData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SkillIndexType
import cn.wthee.pcrtool.data.enums.SkillType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.SkillActionText
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.ICON_SKILL
import cn.wthee.pcrtool.viewmodel.SkillViewModel

/**
 * 角色技能列表
 *
 * @param unitId 角色编号
 * @param atk 攻击力
 * @param property 角色属性
 * @param unitType
 * @param isFilterSkill 仅显示专武技能
 * @param filterSkillCount 显示专武技能数量
 */
@Composable
fun SkillCompose(
    unitId: Int,
    atk: Int,
    property: CharacterProperty,
    unitType: UnitType,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
    isFilterSkill: Boolean = false,
    filterSkillCount: Int = 0,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    //普通技能
    val normalSkillFlow = remember(property.level, atk) {
        skillViewModel.getCharacterSkills(property.level, atk, unitId, SkillType.NORMAL)
    }
    val normalSkillData by normalSkillFlow.collectAsState(initial = arrayListOf())

    //sp技能
    val spSkillFlow = remember(property.level, atk) {
        skillViewModel.getCharacterSkills(property.level, atk, unitId, SkillType.SP)
    }
    val spSkillData by spSkillFlow.collectAsState(initial = arrayListOf())

    // sp技能标签
    val spLabelFlow = remember(unitId) {
        skillViewModel.getSpSkillLabel(unitId)
    }
    val spLabel by spLabelFlow.collectAsState(initial = null)


    SkillLayout(
        normalSkillData,
        spSkillData,
        spLabel,
        isFilterSkill,
        filterSkillCount,
        unitType,
        property,
        toSummonDetail
    )
}

/**
 * 技能列表布局
 */
@Composable
fun SkillLayout(
    normalSkillData: MutableList<SkillDetail>,
    spSkillData: MutableList<SkillDetail>,
    spLabel: SpSkillLabelData?,
    isFilterSkill: Boolean,
    filterSkillCount: Int,
    unitType: UnitType,
    property: CharacterProperty,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.largePadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //技能信息标题
        if (normalSkillData.isNotEmpty() || spSkillData.isNotEmpty()) {
            MainText(
                text = stringResource(R.string.skill)
            )
        }
        //普通技能
        (if (isFilterSkill) {
            //过滤专用装备影响的技能
            normalSkillData.filter {
                val skill1 = it.skillIndexType == SkillIndexType.MAIN_SKILL_1_PLUS
                        || it.skillIndexType == SkillIndexType.MAIN_SKILL_1
                val skill2 = it.skillIndexType == SkillIndexType.MAIN_SKILL_2_PLUS
                        || it.skillIndexType == SkillIndexType.MAIN_SKILL_2
                if (filterSkillCount == 1) {
                    skill1
                } else {
                    skill1 || skill2
                }
            }
        } else {
            normalSkillData
        }).forEach { skillDetail ->
            SkillItem(
                skillDetail = skillDetail,
                unitType = unitType,
                property = property,
                toSummonDetail = toSummonDetail
            )
        }
        //特殊技能
        if (spSkillData.isNotEmpty()) {
            MainText(
                text = stringResource(R.string.sp_skill),
                modifier = Modifier
                    .padding(top = Dimen.largePadding)
            )
            spLabel?.spLabelText?.let {
                CaptionText(text = it)
            }
        }
        (if (isFilterSkill) {
            //过滤专用装备影响的技能
            spSkillData.filter {
                val skill1 = it.skillIndexType == SkillIndexType.SP_SKILL_1_PLUS
                        || it.skillIndexType == SkillIndexType.SP_SKILL_1
                val skill2 = it.skillIndexType == SkillIndexType.SP_SKILL_2_PLUS
                        || it.skillIndexType == SkillIndexType.SP_SKILL_2
                if (filterSkillCount == 1) {
                    skill1
                } else {
                    skill1 || skill2
                }
            }
        } else {
            spSkillData
        }).forEach { skillDetail ->
            SkillItem(
                skillDetail = skillDetail,
                unitType = unitType,
                property = property,
                toSummonDetail = toSummonDetail
            )
        }
    }
}


/**
 * 技能
 * @param property 角色属性，怪物技能不需要该参数
 */
@OptIn(ExperimentalLayoutApi::class)
@Suppress("RegExpRedundantEscape")
@Composable
fun SkillItem(
    skillDetail: SkillDetail,
    unitType: UnitType,
    property: CharacterProperty = CharacterProperty(),
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
    isExtraEquipSKill: Boolean = false
) {
    //是否显示参数判断
    val actionData = remember(skillDetail.skillId, skillDetail.level, skillDetail.atk) {
        skillDetail.getActionInfo()
    }

    try {
        val showCoeIndex = skillDetail.getActionIndexWithCoe()
        actionData.mapIndexed { index, skillActionText ->
            val s = showCoeIndex.filter {
                it.actionIndex == index
            }
            val show = s.isNotEmpty()
            val str = skillActionText.action
            if (show) {
                //系数表达式开始位置
                val startIndex = str.indexOfFirst { ch -> ch == '<' }
                if (startIndex != -1) {
                    var coeExpr = str.substring(startIndex, str.length)
                    Regex("\\{.*?\\}").findAll(skillActionText.action).forEach {
                        if (s[0].type == 0) {
                            coeExpr = coeExpr.replace(it.value, "")
                        } else if (s[0].coe != it.value) {
                            coeExpr = coeExpr.replace(it.value, "")
                        }
                    }
                    skillActionText.action =
                        str.substring(0, startIndex) + coeExpr
                }
            } else {
                skillActionText.action =
                    str.replace(Regex("\\{.*?\\}"), "")
            }
        }
    } catch (_: Exception) {

    }

    //技能类型名
    var isNormalSkill = true
    val type = when (skillDetail.skillIndexType) {
        SkillIndexType.UB -> {
            isNormalSkill = false
            stringResource(id = R.string.union_burst)
        }

        SkillIndexType.UB_PLUS -> {
            isNormalSkill = false
            stringResource(id = R.string.union_burst) + "+"
        }

        SkillIndexType.MAIN_SKILL_1,
        SkillIndexType.MAIN_SKILL_2,
        SkillIndexType.MAIN_SKILL_3,
        SkillIndexType.MAIN_SKILL_4,
        SkillIndexType.MAIN_SKILL_5,
        SkillIndexType.MAIN_SKILL_6,
        SkillIndexType.MAIN_SKILL_7,
        SkillIndexType.MAIN_SKILL_8,
        SkillIndexType.MAIN_SKILL_9,
        SkillIndexType.MAIN_SKILL_10 -> {
            stringResource(id = R.string.skill_index, skillDetail.skillIndexType.index)
        }

        SkillIndexType.MAIN_SKILL_1_PLUS,
        SkillIndexType.MAIN_SKILL_2_PLUS -> {
            stringResource(id = R.string.skill_index, skillDetail.skillIndexType.index) + "+"
        }

        SkillIndexType.EX_1,
        SkillIndexType.EX_2,
        SkillIndexType.EX_3,
        SkillIndexType.EX_4,
        SkillIndexType.EX_5 -> {
            isNormalSkill = false
            stringResource(id = R.string.ex_skill)
        }

        SkillIndexType.EX_1_PLUS,
        SkillIndexType.EX_2_PLUS,
        SkillIndexType.EX_3_PLUS,
        SkillIndexType.EX_4_PLUS,
        SkillIndexType.EX_5_PLUS -> {
            isNormalSkill = false
            stringResource(id = R.string.ex_skill) + "+"
        }

        SkillIndexType.SP_UB -> {
            isNormalSkill = false
            "SP" + stringResource(id = R.string.union_burst)
        }

        SkillIndexType.SP_SKILL_1,
        SkillIndexType.SP_SKILL_2,
        SkillIndexType.SP_SKILL_3,
        SkillIndexType.SP_SKILL_4,
        SkillIndexType.SP_SKILL_5 -> {
            "SP" + stringResource(id = R.string.skill_index, skillDetail.skillIndexType.index)
        }

        SkillIndexType.SP_SKILL_1_PLUS,
        SkillIndexType.SP_SKILL_2_PLUS -> {
            "SP" + stringResource(id = R.string.skill_index, skillDetail.skillIndexType.index) + "+"
        }

        else -> ""
    }
    val color = getSkillColor(type)
    val name =
        if (unitType == UnitType.ENEMY || unitType == UnitType.ENEMY_SUMMON) type else skillDetail.name
    val url = ImageRequestHelper.getInstance().getUrl(ICON_SKILL, skillDetail.iconType)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.largePadding)
    ) {

        Row {
            //技能图标
            MainIcon(data = url)
            Column(
                modifier = Modifier
                    .padding(horizontal = Dimen.mediumPadding)
                    .heightIn(min = Dimen.iconSize),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                //技能名
                MainText(
                    text = name,
                    color = color,
                    selectable = true,
                    textAlign = TextAlign.Start,
                    style = if (!isExtraEquipSKill) {
                        MaterialTheme.typography.titleMedium
                    } else {
                        MaterialTheme.typography.titleSmall
                    }
                )

                if (BuildConfig.DEBUG) {
                    CaptionText(text = skillDetail.skillId.toString())
                }

                //非装备技能时显示
                if (!isExtraEquipSKill) {
                    FlowRow {
                        //技能类型
                        CaptionText(
                            text = type,
                            modifier = Modifier.padding(end = Dimen.largePadding)
                        )
                        //技能等级
                        if (unitType == UnitType.ENEMY || unitType == UnitType.ENEMY_SUMMON) {
                            CaptionText(
                                text = stringResource(id = R.string.skill_level, skillDetail.level),
                                modifier = Modifier.padding(end = Dimen.largePadding)
                            )
                        }
                        //冷却时间
                        if ((unitType == UnitType.ENEMY || unitType == UnitType.ENEMY_SUMMON) && skillDetail.bossUbCooltime > 0.0) {
                            CaptionText(
                                text = stringResource(
                                    id = R.string.skill_cooltime,
                                    skillDetail.bossUbCooltime.toBigDecimal().stripTrailingZeros()
                                        .toPlainString()
                                ),
                                modifier = Modifier.padding(end = Dimen.largePadding)
                            )
                        }
                        //准备时间，显示：时间大于 0 或 角色1、2技能
                        if (skillDetail.castTime > 0 || (unitType == UnitType.CHARACTER && isNormalSkill)) {
                            CaptionText(
                                text = stringResource(
                                    id = R.string.skill_cast_time,
                                    skillDetail.castTime.toBigDecimal().stripTrailingZeros()
                                        .toPlainString()
                                )
                            )
                        }
                    }
                }
            }
        }

        //标签
        val tags = getTags(actionData)
        FlowRow {
            tags.forEach {
                SkillActionTag(it)
            }
        }

        //描述
        if (skillDetail.desc.isNotBlank()) {
            Subtitle1(
                text = skillDetail.desc,
                selectable = true,
                modifier = Modifier.padding(top = Dimen.mediumPadding)
            )
        }

        //动作
        actionData.forEach {
            SkillActionItem(
                skillAction = it,
                unitType = unitType,
                property = property,
                toSummonDetail = toSummonDetail
            )
        }
    }
}

/**
 * 技能动作标签
 */
@Composable
fun SkillActionTag(skillTag: String) {
    MainTitleText(
        text = skillTag,
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(end = Dimen.smallPadding, top = Dimen.mediumPadding)
    )
}

/**
 * 技能动作
 */
@Composable
fun SkillActionItem(
    skillAction: SkillActionText,
    unitType: UnitType,
    property: CharacterProperty,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
) {
    //调试用
    val expand = remember {
        mutableStateOf(false)
    }

    //详细描述
    val mark0 = arrayListOf<ColorTextIndex>()
    val mark1 = arrayListOf<ColorTextIndex>()
    val mark2 = arrayListOf<ColorTextIndex>()
    val mark3 = arrayListOf<ColorTextIndex>()
    val colors =
        arrayListOf(
            colorGreen,
            if (isSystemInDarkTheme()) colorWhite else Color.Black,
            colorPurple,
            MaterialTheme.colorScheme.primary
        )
    skillAction.action.forEachIndexed { index, c ->
        if (c == '[') {
            mark0.add(ColorTextIndex(start = index))
        }
        if (c == ']') {
            mark0[mark0.size - 1].end = index
        }
        if (c == '(') {
            mark1.add(ColorTextIndex(start = index))
        }
        if (c == ')') {
            mark1[mark1.size - 1].end = index
        }
        if (c == '{') {
            mark2.add(ColorTextIndex(start = index))
        }
        if (c == '}') {
            mark2[mark2.size - 1].end = index
        }
        if (c == '<') {
            mark3.add(ColorTextIndex(start = index))
        }
        if (c == '>') {
            mark3[mark3.size - 1].end = index
        }
    }
    val map = hashMapOf<Int, ArrayList<ColorTextIndex>>()
    map[0] = mark0
    map[1] = mark1
    map[2] = mark2
    map[3] = mark3


    Column(
        modifier = Modifier
            .padding(vertical = Dimen.smallPadding)
            .fillMaxWidth()
            .heightIn(min = Dimen.skillActionMinHeight)
            .padding(Dimen.smallPadding)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            )
            .clickable(BuildConfig.DEBUG) { expand.value = !expand.value }
    ) {
        //设置字体
        Text(
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(Dimen.smallPadding),
            color = MaterialTheme.colorScheme.onSurface,
            text = buildAnnotatedString {
                skillAction.action.forEachIndexed { index, char ->
                    //替换括号及括号内字体颜色
                    for (i in 0..3) {
                        map[i]?.forEach {
                            if (index >= it.start && index <= it.end) {
                                withStyle(style = SpanStyle(color = colors[i])) {
                                    append(char)
                                }
                                return@forEachIndexed
                            }
                        }
                    }
                    //添加非括号标记的参数
                    append(char)
                }
            }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            //查看召唤物
            if (skillAction.summonUnitId != 0 && toSummonDetail != null) {
                IconTextButton(
                    icon = MainIconType.SUMMON,
                    text = stringResource(R.string.to_summon)
                ) {
                    toSummonDetail(
                        skillAction.summonUnitId,
                        unitType.type,
                        property.level,
                        property.rank,
                        property.rarity
                    )
                }
            }
            //技能等级超过tp限制等级的，添加标识
            if (skillAction.isTpLimitAction) {
                IconTextButton(
                    icon = MainIconType.INFO,
                    text = stringResource(R.string.tp_limit_level_action_desc)
                )
            }
        }

        //调试用
        if (BuildConfig.DEBUG) {
            if (expand.value) {
                CaptionText(text = skillAction.debugText, textAlign = TextAlign.Start)
            }
        }
    }
}


/**
 * 获取技能动作标签
 */
private fun getTags(data: ArrayList<SkillActionText>): ArrayList<String> {
    val list = arrayListOf<String>()
    data.forEach {
        if (it.tag.isNotEmpty() && !list.contains(it.tag)) {
            list.add(it.tag)
        }
    }
    return list
}

/**
 * 获取技能名称颜色
 */
@Composable
fun getSkillColor(type: String): Color {
    return when {
        type.contains(stringResource(id = R.string.union_burst)) -> colorGold
        type.contains("EX") -> colorCopper
        type.contains("1") -> colorPurple
        type.contains("2") -> colorRed
        else -> MaterialTheme.colorScheme.onSurface
    }
}

data class ColorTextIndex(
    var start: Int = 0,
    var end: Int = 0
)


@CombinedPreviews
@Composable
private fun SkillItemPreview() {
    val skill = SkillDetail(
        name = stringResource(id = R.string.debug_short_text),
        desc = stringResource(id = R.string.debug_long_text),
        castTime = 10.0,
    )
    skill.actions = arrayListOf(SkillActionDetail(), SkillActionDetail())

    PreviewLayout {
        SkillItem(
            skillDetail = skill,
            unitType = UnitType.CHARACTER,
            property = CharacterProperty(),
            toSummonDetail = { _, _, _, _, _ -> }
        )
    }
}
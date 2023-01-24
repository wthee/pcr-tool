package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import cn.wthee.pcrtool.data.db.view.SkillActionText
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SkillType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_SKILL
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.flowlayout.FlowRow

/**
 * 角色技能列表
 *
 * @param unitId 角色编号
 * @param atk 攻击力
 * @param property 角色属性
 * @param unitType
 */
@Composable
fun SkillCompose(
    unitId: Int,
    atk: Int,
    property: CharacterProperty,
    unitType: UnitType,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)? = null,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    //普通技能
    val normalSkillData =
        skillViewModel.getCharacterSkills(property.level, atk, unitId, SkillType.NORMAL)
            .collectAsState(
                initial = arrayListOf()
            ).value
    //sp技能
    val spSkillData =
        skillViewModel.getCharacterSkills(property.level, atk, unitId, SkillType.SP).collectAsState(
            initial = arrayListOf()
        ).value
    // sp技能标签
    val spLabel = skillViewModel.getSpSkillLabel(unitId).collectAsState(initial = null).value


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.largePadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //技能信息标题
        if (normalSkillData.isNotEmpty() or spSkillData.isNotEmpty()) {
            MainText(
                text = stringResource(R.string.skill),
                modifier = Modifier
                    .padding(top = Dimen.largePadding)
            )
        }
        //普通技能
        normalSkillData.forEach { skillDetail ->
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

        spSkillData.forEach { skillDetail ->
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
    val actionData = skillDetail.getActionInfo()
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
    val type = when (unitType) {
        UnitType.CHARACTER, UnitType.CHARACTER_SUMMON -> {
            when (skillDetail.skillId % 1000) {
                501 -> {
                    isNormalSkill = false
                    stringResource(id = R.string.ex_skill)
                }
                511 -> {
                    isNormalSkill = false
                    stringResource(id = R.string.ex_skill) + "+"
                }
                100 -> {
                    isNormalSkill = false
                    "SP" + stringResource(id = R.string.union_burst)
                }
                //sp 技能
                101, 111, 102, 112, 103, 113 -> {
                    "SP" + stringResource(
                        id = R.string.skill_index,
                        skillDetail.skillId % 10
                    ) + if (skillDetail.skillId % 100 / 10 == 1) {
                        "+"
                    } else {
                        ""
                    }
                }
                1, 21 -> {
                    isNormalSkill = false
                    stringResource(id = R.string.union_burst)
                }
                11 -> {
                    isNormalSkill = false
                    stringResource(id = R.string.union_burst) + "+"
                }
                else -> {
                    val skillIndex = skillDetail.skillId % 10 - 1
                    stringResource(
                        id = R.string.skill_index,
                        skillIndex
                    ) + if (skillDetail.skillId % 1000 / 10 == 1) {
                        "+"
                    } else {
                        ""
                    }
                }
            }
        }
        UnitType.ENEMY, UnitType.ENEMY_SUMMON -> {
            if (skillDetail.enemySkillIndex == 0) {
                stringResource(id = R.string.union_burst)
            } else {
                stringResource(id = R.string.skill_index, skillDetail.enemySkillIndex)
            }
        }
    }
    val color = getSkillColor(type)
    val name =
        if (unitType == UnitType.ENEMY || unitType == UnitType.ENEMY_SUMMON) type else skillDetail.name
    val url = ImageResourceHelper.getInstance().getUrl(ICON_SKILL, skillDetail.iconType)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.largePadding)
    ) {

        Row {
            //技能图标
            IconCompose(data = url)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = Dimen.smallPadding)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Column(
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .fillMaxWidth()
                .heightIn(min = Dimen.skillActionMinHeight)
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
                        icon = MainIconType.HELP,
                        text = stringResource(R.string.tp_limit_level_action_desc)
                    )
                }
            }

            //调试用
            if (BuildConfig.DEBUG) {
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
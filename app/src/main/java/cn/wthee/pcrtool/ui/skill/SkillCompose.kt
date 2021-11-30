package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.AttackPattern
import cn.wthee.pcrtool.data.db.view.SkillActionText
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.data.model.SkillLoop
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_EQUIPMENT
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_SKILL
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.viewmodel.SkillViewModel

/**
 * 角色技能列表
 *
 * @param unitId 角色编号
 * @param cutinId 角色特殊编号
 * @param level 等级
 * @param atk 攻击力
 */
@Composable
fun SkillCompose(
    unitId: Int,
    cutinId: Int,
    level: Int = 0,
    atk: Int,
    toSummonDetail: ((Int, Int) -> Unit)? = null,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    skillViewModel.getCharacterSkills(level, atk, unitId, cutinId)
    val skillList = skillViewModel.skills.observeAsState().value ?: listOf()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.largePadding)
    ) {
        skillList.forEach {
            SkillItem(level = level, skillDetail = it, toSummonDetail = toSummonDetail)
        }
    }
}

/**
 * 技能
 */
@Suppress("RegExpRedundantEscape")
@Composable
fun SkillItem(
    level: Int,
    skillDetail: SkillDetail,
    toSummonDetail: ((Int, Int) -> Unit)? = null,
    isClanBoss: Boolean = false
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
    } catch (e: Exception) {

    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimen.mediumPadding, bottom = Dimen.largePadding)
    ) {
        //技能名
        val type = getSkillType(skillDetail.skillId)
        val color = getSkillColor(type)
        val name = if (isClanBoss) type else skillDetail.name
        MainText(
            text = name,
            color = color,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = Dimen.largePadding),
            selectable = true
        )
        //技能类型
        if (!isClanBoss) {
            CaptionText(
                text = type + if (skillDetail.isCutin) "(六星)" else "",
                color = color,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.smallPadding)
            )
        }
        //冷却时间
        if (isClanBoss && skillDetail.bossUbCooltime > 0.0) {
            CaptionText(
                text = skillDetail.bossUbCooltime.toString(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.smallPadding)
            )
        }
        //图标、等级、描述
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.smallPadding)
        ) {
            val url = ImageResourceHelper.getInstance().getUrl(ICON_SKILL, skillDetail.iconType)
            //技能图标
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconCompose(data = url)
                if (skillDetail.castTime > 0) {
                    CaptionText(text = "${skillDetail.castTime}秒")
                }
            }
            Column(modifier = Modifier.padding(start = Dimen.mediumPadding)) {
                //等级
                if (isClanBoss) {
                    Text(
                        text = stringResource(id = R.string.skill_level, level),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                //描述
                Subtitle2(text = skillDetail.desc, selectable = true)
            }

        }
        val tags = getTags(skillDetail.getActionInfo())
        //标签
        Row {
            tags.forEach {
                SkillActionTag(it)
            }
        }
        //动作
        actionData.forEach {
            if (BuildConfig.DEBUG) {
                Text(it.actionId.toString())
            }
            SkillActionItem(skillAction = it, toSummonDetail = toSummonDetail)
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
        textStyle = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(end = Dimen.smallPadding, top = Dimen.mediumPadding)
    )
}

/**
 * 技能动作
 */
@Composable
fun SkillActionItem(
    skillAction: SkillActionText,
    toSummonDetail: ((Int, Int) -> Unit)? = null,
) {
    //详细描述
    val mark0 = arrayListOf<SkillIndex>()
    val mark1 = arrayListOf<SkillIndex>()
    val mark2 = arrayListOf<SkillIndex>()
    val mark3 = arrayListOf<SkillIndex>()
    val colors =
        arrayListOf(
            colorResource(R.color.color_rank_21),
            colorResource(if (isSystemInDarkTheme()) R.color.alpha_white else R.color.black),
            colorResource(R.color.color_rank_11_17),
            MaterialTheme.colorScheme.primary
        )
    skillAction.action.forEachIndexed { index, c ->
        if (c == '[') {
            mark0.add(SkillIndex(start = index))
        }
        if (c == ']') {
            mark0[mark0.size - 1].end = index
        }
        if (c == '(') {
            mark1.add(SkillIndex(start = index))
        }
        if (c == ')') {
            mark1[mark1.size - 1].end = index
        }
        if (c == '{') {
            mark2.add(SkillIndex(start = index))
        }
        if (c == '}') {
            mark2[mark2.size - 1].end = index
        }
        if (c == '<') {
            mark3.add(SkillIndex(start = index))
        }
        if (c == '>') {
            mark3[mark3.size - 1].end = index
        }
    }
    val map = hashMapOf<Int, ArrayList<SkillIndex>>()
    map[0] = mark0
    map[1] = mark1
    map[2] = mark2
    map[3] = mark3
    Column {

        //设置字体
        Text(
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            ),
            color = colorResource(id = R.color.gray),
            modifier = Modifier.padding(
                top = Dimen.mediumPadding,
                start = Dimen.mediumPadding,
                end = Dimen.mediumPadding
            ),
            text = buildAnnotatedString {
                skillAction.action.forEachIndexed { index, c ->
                    var added = false
                    for (i in 0..3) {
                        map[i]?.forEach {
                            if (index >= it.start && index <= it.end) {
                                added = true
                                withStyle(style = SpanStyle(color = colors[i])) {
                                    append(c)
                                }
                                return@forEachIndexed
                            }
                        }
                    }
                    if (!added) {
                        append(c)
                    }
                }

            }
        )
        if (skillAction.summonUnitId != 0 && toSummonDetail != null) {
            //查看召唤物
            MainTexButton(
                text = stringResource(R.string.to_summon),
                color = MaterialTheme.colorScheme.primary,
                textStyle = MaterialTheme.typography.bodySmall
            ) {
                toSummonDetail(skillAction.summonUnitId, skillAction.level)
            }
        }
    }
}

/**
 * 技能循环
 */
@Composable
fun SkillLoopList(
    loopData: List<AttackPattern>,
    iconTypes: HashMap<Int, Int>,
    modifier: Modifier = Modifier,
    isClanBoss: Boolean = false
) {
    val loops = arrayListOf<SkillLoop>()
    loopData.forEach { ap ->
        if (ap.getBefore().size > 0) {
            loops.add(SkillLoop(stringResource(R.string.before_loop), ap.getBefore()))
        }
        if (ap.getLoop().size > 0) {
            loops.add(SkillLoop(stringResource(R.string.looping), ap.getLoop()))
        }
    }
    Column(
        modifier = if (!isClanBoss) modifier.verticalScroll(rememberScrollState()) else modifier
    ) {
        if (loops.isNotEmpty()) {
            loops.forEach {
                SkillLoopItem(loop = it, iconTypes)
            }
        }
        if (!isClanBoss) {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        bottom = Dimen.mediumPadding
                    ), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val type: String
                val url: String
                if (it == 1) {
                    type = "普攻"
                    url = ImageResourceHelper.getInstance().getUrl(ICON_EQUIPMENT, UNKNOWN_EQUIP_ID)
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
                        2001 -> iconTypes[101]
                        2002 -> iconTypes[102]
                        2003 -> iconTypes[103]
                        else -> null
                    }
                    url = ImageResourceHelper.getInstance().getUrl(ICON_SKILL, iconType ?: 1001)
                }
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

/**
 * 获取标签状态
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
 * 获取技能
 */
private fun getSkillType(skillId: Int) = when (skillId % 1000) {
    501 -> "EX技能"
    511 -> "EX技能+"
    100 -> "SP连结爆发"
    101 -> "SP技能 1"
    102 -> "SP技能 2"
    103 -> "SP技能 3"
    1, 21 -> "连结爆发"
    11 -> "连结爆发+"
    else -> {
        val skillIndex = skillId % 10 - 1
        if (skillId % 1000 / 10 == 1) {
            "技能 ${skillIndex}+"
        } else {
            "技能 $skillIndex"
        }
    }
}


/**
 * 获取技能名称颜色
 */
@Composable
private fun getSkillColor(type: String): Color {
    return when {
        type.contains("连结") -> colorResource(R.color.color_rank_7_10)
        type.contains("EX") -> colorResource(R.color.color_rank_2_3)
        type.contains("1") -> colorResource(R.color.color_rank_11_17)
        type.contains("2") -> colorResource(R.color.color_rank_18_20)
        else -> MaterialTheme.colorScheme.primary
    }
}

private data class SkillIndex(
    var start: Int = 0,
    var end: Int = 0
)
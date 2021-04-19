package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.data.view.SkillActionText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.SkillViewModel

/**
 * 角色技能列表
 */
@Composable
fun SkillCompose(
    level: Int,
    atk: Int,
    id: Int,
    skillViewModel: SkillViewModel = hiltNavGraphViewModel()
) {
    skillViewModel.getCharacterSkills(level, atk, id)
    val skillList = skillViewModel.skills.observeAsState().value ?: listOf()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(skillList) {
            SkillItem(level = level, skillDetail = it)
        }
    }
}

/**
 * 技能
 */
@Composable
fun SkillItem(level: Int, skillDetail: SkillDetail) {

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
            .padding(
                start = Dimen.mediuPadding,
                end = Dimen.mediuPadding,
                top = Dimen.largePadding,
                bottom = Dimen.largePadding
            )
    ) {
        //名称
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val type = getSkillType(skillDetail.skillId)
            //技能名
            Text(
                text = skillDetail.name,
                color = colorResource(
                    id = when {
                        type.contains("连结") -> R.color.color_rank_7_10
                        type.contains("EX") -> R.color.color_rank_2_3
                        else -> R.color.color_rank_4_6
                    }
                ),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.weight(1f)
            )
            //技能类型
            MainTitleText(text = type)
        }
        //图标、等级、描述
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.smallPadding)
        ) {
            val url = Constants.SKILL_ICON_URL + skillDetail.iconType + Constants.WEBP
            //技能图标
            IconCompose(data = url, modifier = Modifier.size(Dimen.iconSize))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Dimen.mediuPadding)
            ) {
                //等级
                Text(
                    text = stringResource(id = R.string.skill_level) + level,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.caption
                )
                //描述
                Text(
                    text = skillDetail.desc,
                    style = MaterialTheme.typography.subtitle2
                )
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
            SkillActionItem(skillAction = it)
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
        small = true,
        modifier = Modifier.padding(end = Dimen.smallPadding, top = Dimen.mediuPadding)
    )
}

/**
 * 技能动作
 */
@Composable
fun SkillActionItem(skillAction: SkillActionText) {
    //详细描述
    val mark0 = arrayListOf<SkillIndex>()
    val mark1 = arrayListOf<SkillIndex>()
    val mark2 = arrayListOf<SkillIndex>()
    val mark3 = arrayListOf<SkillIndex>()
    val colors =
        arrayListOf(R.color.cool_apk, R.color.black, R.color.color_rank_11_17, R.color.colorPrimary)
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
    //设置字体
    Text(
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        ),
        color = colorResource(id = R.color.gray),
        modifier = Modifier.padding(top = Dimen.mediuPadding, start = Dimen.smallPadding),
        text = buildAnnotatedString {
            skillAction.action.forEachIndexed { index, c ->
                var added = false
                for (i in 0..3) {
                    map[i]?.forEach {
                        if (index >= it.start && index <= it.end) {
                            added = true
                            withStyle(style = SpanStyle(color = colorResource(id = colors[i]))) {
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

        })

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
        val skillIndex = skillId % 1000 % 10 - 1
        if (skillId % 1000 / 10 == 1) {
            "技能 ${skillIndex}+"
        } else {
            "技能 $skillIndex"
        }
    }
}

private data class SkillIndex(
    var start: Int = 0,
    var end: Int = 0
)

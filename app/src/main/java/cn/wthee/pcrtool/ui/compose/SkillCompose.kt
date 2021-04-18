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
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.SkillDetail
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

@Composable
fun SkillItem(level: Int, skillDetail: SkillDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.mediuPadding)
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
    }
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

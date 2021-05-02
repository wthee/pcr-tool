package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.ui.skill.SkillCompose
import cn.wthee.pcrtool.ui.theme.Dimen

/**
 * 角色技能列表
 */
@Composable
fun CharacterSkill(
    id: Int,
    level: Int,
    atk: Int,
) {
    Column() {
        SkillCompose(level = level, atk = atk, id = id)
        Spacer(modifier = Modifier.height(Dimen.sheetMarginBottom))
    }
}
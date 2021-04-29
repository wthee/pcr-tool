package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Composable
import cn.wthee.pcrtool.ui.skill.SkillCompose

/**
 * 角色技能列表
 */
@Composable
fun CharacterSkill(
    id: Int,
    level: Int,
    atk: Int,
) {
    SkillCompose(level = level, atk = atk, id = id)
}
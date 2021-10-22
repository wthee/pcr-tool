package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Composable
import cn.wthee.pcrtool.ui.skill.SkillCompose
import coil.annotation.ExperimentalCoilApi

/**
 * 角色技能列表
 *
 * @param unitId 角色编号
 * @param unitId 角色特殊编号
 * @param level 角色等级
 * @param atk 角色攻击力
 */
@ExperimentalCoilApi
@Composable
fun CharacterSkill(
    unitId: Int,
    cutinId: Int = 0,
    level: Int,
    atk: Int,
) {
    SkillCompose(unitId = unitId, cutinId = cutinId, level = level, atk = atk)
}
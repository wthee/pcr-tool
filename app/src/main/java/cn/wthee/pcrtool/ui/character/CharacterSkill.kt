package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.skill.SkillCompose
import cn.wthee.pcrtool.ui.theme.Dimen
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
    Column {
        SkillCompose(unitId = unitId, cutinId = cutinId, level = level, atk = atk)
        CommonSpacer()
        Spacer(modifier = Modifier.height(Dimen.fabSize + Dimen.fabMargin))
    }
}
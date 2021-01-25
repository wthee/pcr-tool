package cn.wthee.pcrtool.data.bean

import cn.wthee.pcrtool.data.db.entity.SkillAction

/**
 * 角色技能信息
 */
data class CharacterSkillInfo(
    val skillId: Int,
    val name: String,
    val desc: String,
    val icon_type: Int
) {
    //角色技能效果
    var actions = listOf<SkillAction>()
}


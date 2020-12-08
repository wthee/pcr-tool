package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.db.entity.SkillAction


data class CharacterSkillInfo(
    val skillId: Int,
    val name: String,
    val desc: String,
    val icon_type: Int
) {
    var actions = listOf<SkillAction>()
}


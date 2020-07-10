package cn.wthee.pcrtool.data.model


data class CharacterSkillInfo(
    val name: String,
    val desc: String,
    val icon_type: Int
) {
    var actions = listOf<SkillAction>()
}


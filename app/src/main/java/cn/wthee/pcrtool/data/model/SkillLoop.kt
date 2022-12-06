package cn.wthee.pcrtool.data.model

/**
 * 技能循环数据
 */
data class SkillLoop(
    val unitId: Int,
    val patternId: Int,
    val loopTitle: String,
    val loopList: List<Int>
)
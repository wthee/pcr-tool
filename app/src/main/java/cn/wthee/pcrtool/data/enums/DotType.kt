package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

enum class DotType(val type: Int, val typeNameId: Int) {
    DOT_0(0, R.string.skill_dot_0),
    DOT_1(1, R.string.skill_dot_1_7),
    DOT_2(2, R.string.skill_dot_2),
    DOT_3(3, R.string.skill_dot_3_8),
    DOT_4(4, R.string.skill_dot_4),
    DOT_5(5, R.string.skill_dot_5),
    DOT_7(7, R.string.skill_dot_1_7),
    DOT_8(8, R.string.skill_dot_3_8),
    DOT_9(8, R.string.skill_dot_9),
    DOT_11(11, R.string.skill_dot_11),
    UNKNOWN(-99, R.string.unknown);


    companion object {
        fun getByType(type: Int) = DotType.entries
            .find { it.type == type } ?: UNKNOWN

    }
}
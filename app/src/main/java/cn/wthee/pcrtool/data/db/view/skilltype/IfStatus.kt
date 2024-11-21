package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getStatus
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget

// 23：判定对象状态
fun SkillActionDetail.ifStatus(): String {
    val status = getStatus(actionDetail1)
    var trueClause = UNKNOWN
    var falseClause = UNKNOWN
    if (actionDetail2 != 0) {
        trueClause = if (status != UNKNOWN) {
            getString(
                R.string.skill_action_if_status,
                getTarget(),
                status,
                actionDetail2 % 100
            )
        } else {
            when (actionDetail1) {
                in 600..699, 710, 6145 -> {
                    getString(
                        R.string.skill_action_if_mark,
                        getTarget(),
                        actionDetail2 % 100
                    )
                }

                6194 -> {
                    getString(
                        R.string.skill_action_if_mark_count,
                        getTarget(),
                        actionValue3.toInt(),
                        actionDetail2 % 100
                    )
                }

                700 -> {
                    getString(
                        R.string.skill_action_if_alone,
                        getTarget(),
                        actionDetail2 % 100
                    )
                }

                in 901..999 -> {
                    getString(
                        R.string.skill_action_if_hp_below,
                        getTarget(),
                        actionDetail1 - 900,
                        actionDetail2 % 100
                    )
                }

                2000, 1300 -> {
                    getString(
                        R.string.skill_action_if_unit_atk_type,
                        getTarget(),
                        getString(R.string.skill_status_physical_atk),
                        actionDetail2 % 100
                    )
                }

                2001 -> {
                    getString(
                        R.string.skill_action_if_unit_atk_type,
                        getTarget(),
                        getString(R.string.skill_status_magic_atk),
                        actionDetail2 % 100
                    )
                }

                else -> UNKNOWN
            }
        }
    }
    if (actionDetail3 != 0) {
        falseClause = if (status != UNKNOWN) {
            getString(
                R.string.skill_action_if_status_not,
                getTarget(),
                status,
                actionDetail3 % 100
            )
        } else {
            when (actionDetail1) {
                in 600..699, 710, 6145 -> {
                    getString(
                        R.string.skill_action_if_mark_not,
                        getTarget(),
                        actionDetail3 % 100
                    )
                }

                6194 -> {
                    getString(
                        R.string.skill_action_if_mark_count_not,
                        getTarget(),
                        actionValue3.toInt(),
                        actionDetail3 % 100
                    )
                }

                700 -> {
                    getString(
                        R.string.skill_action_if_alone_not,
                        getTarget(),
                        actionDetail3 % 100
                    )
                }

                in 901..999 -> {
                    getString(
                        R.string.skill_action_if_hp_above,
                        getTarget(),
                        actionDetail1 - 900,
                        actionDetail3 % 100
                    )
                }

                2000, 1300 -> {
                    getString(
                        R.string.skill_action_if_unit_atk_type,
                        getTarget(),
                        getString(R.string.skill_status_magic_atk),
                        actionDetail3 % 100
                    )
                }

                2001 -> {
                    getString(
                        R.string.skill_action_if_unit_atk_type,
                        getTarget(),
                        getString(R.string.skill_status_physical_atk),
                        actionDetail3 % 100
                    )
                }

                else -> UNKNOWN
            }
        }
    }
    //条件
    return if (actionDetail1 in 0..99) {
        when {
            actionDetail2 != 0 && actionDetail3 != 0 -> {
                getString(
                    R.string.skill_action_random_1,
                    actionDetail1,
                    actionDetail2 % 100,
                    actionDetail3 % 100
                )
            }

            actionDetail2 != 0 -> {
                getString(
                    R.string.skill_action_random_2,
                    actionDetail1,
                    actionDetail2 % 100
                )
            }

            actionDetail3 != 0 -> {
                getString(
                    R.string.skill_action_random_2,
                    100 - actionDetail1,
                    actionDetail3 % 100
                )
            }

            else -> UNKNOWN
        }
    } else {
        when {
            trueClause != UNKNOWN && falseClause != UNKNOWN -> {
                getString(
                    R.string.skill_action_condition,
                    "${trueClause}；${falseClause}"
                )
            }

            trueClause != UNKNOWN -> {
                getString(R.string.skill_action_condition, trueClause)
            }

            falseClause != UNKNOWN -> {
                getString(R.string.skill_action_condition, falseClause)
            }

            else -> UNKNOWN
        }
    }
}
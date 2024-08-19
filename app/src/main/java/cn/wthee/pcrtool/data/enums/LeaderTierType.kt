package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

/**
 * 角色梯队
 */
enum class LeaderTierType(val type: Int, val typeNameId: Int) {
    ALL(0, R.string.leader_tier_0),
    PVP_ATK(1, R.string.leader_tier_1),
    PVP_DEF(2, R.string.leader_tier_2),
    CLAN(3, R.string.clan);

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.type == value } ?: ALL
    }
}
package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

/**
 * 活动日程类型
 */
enum class CalendarEventType(val type: Int, val typeNameId: Int, val index: Int) {
    UNKNOWN(404, R.string.unknown, -1),
    ALL(0, R.string.all, 0),
    SP_DUNGEON(-1, R.string.sp_dungeon, 1),
    TDF(-2, R.string.tdf, 2),
    COLOSSEUM(-3, R.string.colosseum, 3),
    ABYSS(-4, R.string.abyss, 4),
    TOWER(1, R.string.tower, -1),
    DAILY(18, R.string.other, -1),
    LOGIN(19, R.string.other, -1),
    FORTUNE(20, R.string.other, -1),
    N_DROP(31, R.string.other, -1),
    N_MANA(41, R.string.other, -1),
    H_DROP(32, R.string.other, -1),
    H_MANA(42, R.string.other, -1),
    VH_DROP(39, R.string.other, -1),
    VH_MANA(49, R.string.other, -1),
    EXPLORE(34, R.string.other, -1),
    SHRINE(37, R.string.other, -1),
    TEMPLE(38, R.string.other, -1),
    DUNGEON(45, R.string.other, -1),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.type == value } ?: UNKNOWN

        fun getByIndex(value: Int) = entries
            .find { it.index == value } ?: UNKNOWN
    }
}

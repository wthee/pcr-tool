package cn.wthee.pcrtool.data.enums

/**
 * 角色排序
 */
enum class SortType(val type: Int) {
    SORT_DATE(0),
    SORT_AGE(1),
    SORT_HEIGHT(2),
    SORT_WEIGHT(3),
    SORT_POSITION(4),
    SORT_BIRTHDAY(5)
}

fun getSortType(value: Int): SortType {
    for (item in SortType.values()) {
        if (item.type == value) return item
    }
    return SortType.SORT_DATE
}



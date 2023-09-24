package cn.wthee.pcrtool.data.enums

/**
 * 角色排序
 */
enum class CharacterSortType(val type: Int) {
    SORT_DATE(0),
    SORT_AGE(1),
    SORT_HEIGHT(2),
    SORT_WEIGHT(3),
    SORT_POSITION(4),
    SORT_BIRTHDAY(5),
    SORT_UNLOCK_6(6),
}

fun getSortType(value: Int): CharacterSortType {
    for (item in CharacterSortType.entries) {
        if (item.type == value) return item
    }
    return CharacterSortType.SORT_DATE
}



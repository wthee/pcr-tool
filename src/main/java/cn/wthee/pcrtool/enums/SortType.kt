package cn.wthee.pcrtool.enums

//排序
enum class SortType {
    //角色
    SORT_DATE,
    SORT_AGE,
    SORT_HEIGHT,
    SORT_WEIGHT,
    SORT_POSITION
}

val SortType.value: Int
    get() {
        return when (this) {
            SortType.SORT_DATE -> 0
            SortType.SORT_AGE -> 1
            SortType.SORT_HEIGHT -> 2
            SortType.SORT_WEIGHT -> 3
            SortType.SORT_POSITION -> 4
        }
    }

package cn.wthee.pcrtool.data.model

/**
 * 角色排行筛选
 */
data class FilterLeaderboard(
    var sort: Int = 0,
    var asc: Boolean = false,
    var onlyLast: Boolean = false
)
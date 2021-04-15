package cn.wthee.pcrtool.data.model

/**
 * 日历
 */
data class CalendarData(
    val days: List<CalendarDay>,
    val maxDate: String
)

/**
 * 事项
 */
data class CalendarDay(
    val content: List<CalendarContent>,
    val date: String
)

/**
 * 分类事项
 */
data class CalendarContent(
    val events: List<CalendarEvent>,
    val type: String
)

/**
 * 事项详情
 */
data class CalendarEvent(
    val endDate: String,
    val startDate: String,
    val title: String
)
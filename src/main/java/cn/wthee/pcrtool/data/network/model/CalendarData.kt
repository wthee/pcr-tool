package cn.wthee.pcrtool.data.network.model


data class CalendarData(
    val days: List<CalendarDay>,
    val maxDate: String
)

data class CalendarDay(
    val content: List<CalendarContent>,
    val date: String
)

data class CalendarContent(
    val events: List<CalendarEvent>,
    val type: String
)

data class CalendarEvent(
    val endDate: String,
    val startDate: String,
    val title: String
)
package cn.wthee.pcrtool.data.network.model


data class CalendarData(
    val date: String,
    val events: List<CalendarEvent>,
    val type: String
)

data class CalendarEvent(
    val endDate: String,
    val startDate: String,
    val title: String
)
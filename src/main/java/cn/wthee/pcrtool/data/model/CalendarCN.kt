package cn.wthee.pcrtool.data.model


data class CalendarCN(
    val `data`: List<CalendarData>,
    val status: Int
)

data class CalendarData(
    val date: String,
    val startDate: String,
    val endDate: String,
    val title: String,
    val type: String
)
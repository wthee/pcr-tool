package cn.wthee.pcrtool.data.model

sealed class CalendarDataItem {
    abstract val id: String


    data class Item(val data: CalendarEvent) : CalendarDataItem() {
        override val id = data.startDate + data.title
    }

    data class Header(val title: String) : CalendarDataItem() {
        override val id = Long.MIN_VALUE.toString()
    }
}

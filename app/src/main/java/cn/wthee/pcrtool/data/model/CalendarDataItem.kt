package cn.wthee.pcrtool.data.model

/**
 * 日历事项分组类
 *
 * 分组名[Header] 分组内容[Item]
 */
sealed class CalendarDataItem {
    abstract val id: String


    data class Item(val data: CalendarEvent) : CalendarDataItem() {
        override val id = data.startDate + data.title
    }

    data class Header(val title: String) : CalendarDataItem() {
        override val id = title
    }
}

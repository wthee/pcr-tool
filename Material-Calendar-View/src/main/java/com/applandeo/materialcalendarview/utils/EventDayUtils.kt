package com.applandeo.materialcalendarview.utils

import com.annimon.stream.Optional
import com.annimon.stream.Stream
import com.applandeo.materialcalendarview.EventDay
import java.util.*

/**
 * Modified by wthee
 */
object EventDayUtils {
    /**
     * This method is used to check whether this day is an event day with provided custom label color.
     *
     * @param day                A calendar instance representing day date
     * @param calendarProperties A calendar properties
     */
    fun isEventDayWithLabelColor(day: Calendar?, calendarProperties: CalendarProperties): Boolean {
        return if (calendarProperties.eventsEnabled) {
            Stream.of(calendarProperties.getEventDays())
                .anyMatch { eventDate: EventDay -> eventDate.calendar == day && eventDate.labelColor != 0 }
        } else false
    }

    /**
     * This method is used to get event day which contains custom label color.
     *
     * @param day                A calendar instance representing day date
     * @param calendarProperties A calendar properties
     */
    fun getEventDayWithLabelColor(
        day: Calendar?,
        calendarProperties: CalendarProperties
    ): Optional<EventDay> {
        return Stream.of(calendarProperties.getEventDays())
            .filter { eventDate: EventDay -> eventDate.calendar == day && eventDate.labelColor != 0 }
            .findFirst()
    }
}
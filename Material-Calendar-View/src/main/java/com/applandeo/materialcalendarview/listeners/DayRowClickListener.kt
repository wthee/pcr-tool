package com.applandeo.materialcalendarview.listeners

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import com.annimon.stream.Stream
import com.applandeo.materialcalendarview.CalendarUtils
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.R
import com.applandeo.materialcalendarview.adapters.CalendarPageAdapter
import com.applandeo.materialcalendarview.utils.CalendarProperties
import com.applandeo.materialcalendarview.utils.DateUtils
import com.applandeo.materialcalendarview.utils.DayColorsUtils
import com.applandeo.materialcalendarview.utils.SelectedDay
import java.util.*

/**
 * This class is responsible for handle click events
 *
 *
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 *
 * Modified by wthee
 */
class DayRowClickListener(
    private val mCalendarPageAdapter: CalendarPageAdapter,
    private val mCalendarProperties: CalendarProperties,
    pageMonth: Int
) : OnItemClickListener {
    private val mPageMonth: Int = if (pageMonth < 0) 11 else pageMonth

    override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        val day: Calendar = GregorianCalendar()
        day.time = adapterView.getItemAtPosition(position) as Date
        if (day.time != Date(0)) {
            if (mCalendarProperties.onDayClickListener != null) {
                onClick(day)
            }
            when (mCalendarProperties.calendarType) {
                CalendarView.ONE_DAY_PICKER -> selectOneDay(view, day)
            }
        }
    }

    private fun selectOneDay(view: View, day: Calendar) {
        val previousSelectedDay = mCalendarPageAdapter.selectedDay
        val dayLabel = view.findViewById<View>(R.id.dayLabel) as TextView
        if (isAnotherDaySelected(previousSelectedDay, day)) {
            selectDay(dayLabel, day)
            reverseUnselectedColor(previousSelectedDay)
        }
    }

    private fun selectDay(dayLabel: TextView, day: Calendar) {
        DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties)
        mCalendarPageAdapter.selectedDay = SelectedDay(dayLabel, day)
    }

    private fun reverseUnselectedColor(selectedDay: SelectedDay) {
        DayColorsUtils.setCurrentMonthDayColors(
            selectedDay.calendar,
            DateUtils.calendar, selectedDay.view as TextView, mCalendarProperties
        )
    }

    private fun isCurrentMonthDay(day: Calendar): Boolean {
        return day[Calendar.MONTH] == mPageMonth && isBetweenMinAndMax(day)
    }

    private fun isActiveDay(day: Calendar): Boolean {
        return !mCalendarProperties.disabledDays.contains(day)
    }

    private fun isBetweenMinAndMax(day: Calendar?): Boolean {
        return !(mCalendarProperties.minimumDate != null && day!!.before(mCalendarProperties.minimumDate)
                || mCalendarProperties.maximumDate != null && day!!.after(mCalendarProperties.maximumDate))
    }

    private fun isOutOfMaxRange(firstDay: Calendar?, lastDay: Calendar): Boolean {
        // Number of selected days plus one last day
        val numberOfSelectedDays = CalendarUtils.getDatesRange(firstDay, lastDay).size + 1
        val daysMaxRange = mCalendarProperties.maximumDaysRange
        return daysMaxRange != 0 && numberOfSelectedDays >= daysMaxRange
    }

    private fun isAnotherDaySelected(selectedDay: SelectedDay?, day: Calendar): Boolean {
        return (selectedDay != null && day != selectedDay.calendar
                && isCurrentMonthDay(day) && isActiveDay(day))
    }

    private fun onClick(day: Calendar) {
        Stream.of(mCalendarProperties.getEventDays())
            .filter { eventDate: EventDay -> eventDate.calendar == day }
            .findFirst()
            .ifPresentOrElse({ eventDay: EventDay -> callOnClickListener(eventDay) }) {
                createEmptyEventDay(
                    day
                )
            }
    }

    private fun createEmptyEventDay(day: Calendar) {
        val eventDay = EventDay(day)
        callOnClickListener(eventDay)
    }

    private fun callOnClickListener(eventDay: EventDay) {
        val enabledDay = (mCalendarProperties.disabledDays.contains(eventDay.calendar)
                || !isBetweenMinAndMax(eventDay.calendar))
        eventDay.isEnabled = enabledDay
        mCalendarProperties.onDayClickListener?.onDayClick(eventDay)
    }

}
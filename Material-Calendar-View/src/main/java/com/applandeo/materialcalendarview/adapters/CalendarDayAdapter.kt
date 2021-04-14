package com.applandeo.materialcalendarview.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.annimon.stream.Stream
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.R
import com.applandeo.materialcalendarview.utils.*
import java.util.*

/**
 * This class is responsible for loading a one day cell.
 *
 *
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 *
 * Modified by wthee
 */
internal class CalendarDayAdapter(
    private val mCalendarPageAdapter: CalendarPageAdapter,
    context: Context,
    private val mCalendarProperties: CalendarProperties,
    dates: ArrayList<Date>,
    pageMonth: Int
) : ArrayAdapter<Date>(
    context, mCalendarProperties.itemLayoutResource, dates
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val mPageMonth: Int = if (pageMonth < 0) 11 else pageMonth
    private val mToday = DateUtils.calendar
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var mView = view
        if (mView == null) {
            mView = mLayoutInflater.inflate(mCalendarProperties.itemLayoutResource, parent, false)
        }
        val dayLabel = mView!!.findViewById<View>(R.id.dayLabel) as TextView
        val dayIcon = mView.findViewById<View>(R.id.dayIcon) as ImageView
        val day: Calendar = GregorianCalendar()
        day.time = getItem(position)!!
        if (day.time != Date(0)) {
            // Loading an image of the event
            loadIcon(dayIcon, day)
            setLabelColors(dayLabel, day)
            dayLabel.text = day[Calendar.DAY_OF_MONTH].toString()
        } else {
            mView.visibility = View.GONE
        }
        return mView
    }

    private fun setLabelColors(dayLabel: TextView, day: Calendar) {
        // Setting not current month day color
        if (!isCurrentMonthDay(day)) {
            DayColorsUtils.setDayColors(
                dayLabel, mCalendarProperties.anotherMonthsDaysLabelsColor,
                Typeface.NORMAL, R.drawable.background_transparent
            )
            return
        }

        // Setting view for all SelectedDays
        if (isSelectedDay(day)) {
            Stream.of(mCalendarPageAdapter.selectedDays)
                .filter { selectedDay: SelectedDay -> selectedDay.calendar == day }
                .findFirst().ifPresent { selectedDay: SelectedDay -> selectedDay.view = dayLabel }
            DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties)
            return
        }

        // Setting disabled days color
        if (!isActiveDay(day)) {
            DayColorsUtils.setDayColors(
                dayLabel, mCalendarProperties.disabledDaysLabelsColor,
                Typeface.NORMAL, R.drawable.background_transparent
            )
            return
        }

        // Setting custom label color for event day
        if (isEventDayWithLabelColor(day)) {
            DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mCalendarProperties)
            return
        }

        // Setting current month day color
        DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mCalendarProperties)
    }

    private fun isSelectedDay(day: Calendar): Boolean {
        return (day[Calendar.MONTH] == mPageMonth
                && mCalendarPageAdapter.selectedDays.contains(SelectedDay(day)))
    }

    private fun isEventDayWithLabelColor(day: Calendar): Boolean {
        return EventDayUtils.isEventDayWithLabelColor(day, mCalendarProperties)
    }

    private fun isCurrentMonthDay(day: Calendar): Boolean {
        return day[Calendar.MONTH] == mPageMonth &&
                !(mCalendarProperties.minimumDate != null && day.before(mCalendarProperties.minimumDate)
                        || mCalendarProperties.maximumDate != null && day.after(mCalendarProperties.maximumDate))
    }

    private fun isActiveDay(day: Calendar): Boolean {
        return !mCalendarProperties.disabledDays.contains(day)
    }

    private fun loadIcon(dayIcon: ImageView, day: Calendar) {
        if (!mCalendarProperties.eventsEnabled) {
            dayIcon.visibility = View.GONE
            return
        }
        Stream.of(mCalendarProperties.getEventDays())
            .filter { eventDate: EventDay -> eventDate.calendar == day }
            .findFirst().executeIfPresent { eventDay: EventDay ->
                ImageUtils.loadImage(dayIcon, eventDay.imageDrawable)

                // If a day doesn't belong to current month then image is transparent
                if (!isCurrentMonthDay(day) || !isActiveDay(day)) {
                    dayIcon.alpha = 0.12f
                }
            }
    }

}
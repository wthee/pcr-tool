package com.applandeo.materialcalendarview.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.annimon.stream.Stream
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.R
import com.applandeo.materialcalendarview.exceptions.ErrorsMessages
import com.applandeo.materialcalendarview.exceptions.UnsupportedMethodsException
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.applandeo.materialcalendarview.listeners.OnSelectionAbilityListener
import java.util.*

/**
 * This class contains all properties of the calendar
 *
 *
 * Created by Mateusz Kornakiewicz on 30.10.2017.
 *
 * Modified by wthee
 */
class CalendarProperties(private val mContext: Context) {
    private var mFirstDayOfWeek = 0
    var calendarType = 0
    private var mHeaderColor = 0
    private var mHeaderLabelColor = 0
    private var mSelectionColor = 0
    private var mTodayLabelColor = 0
    var todayColor = 0
    var dialogButtonsColor = 0
    var itemLayoutResource = 0
    private var mDisabledDaysLabelsColor = 0
    private var mHighlightedDaysLabelsColor = 0
    var pagesColor = 0
    var abbreviationsBarColor = 0
    var abbreviationsLabelsColor = 0
    private var mDaysLabelsColor = 0
    private var mSelectionLabelColor = 0
    private var mAnotherMonthsDaysLabelsColor = 0
    var headerVisibility = 0
    var navigationVisibility = 0
    var abbreviationsBarVisibility = 0
    var maximumDaysRange = 0
    var eventsEnabled = false
    var swipeEnabled = false
    var previousButtonSrc: Drawable? = null
    var forwardButtonSrc: Drawable? = null
    val firstPageCalendarDate = DateUtils.calendar
    var calendar: Calendar? = null
    var minimumDate: Calendar? = null
    var maximumDate: Calendar? = null
    var onDayClickListener: OnDayClickListener? = null
    var onSelectDateListener: OnSelectDateListener? = null
    var onSelectionAbilityListener: OnSelectionAbilityListener? = null
    var onPreviousPageChangeListener: OnCalendarPageChangeListener? = null
    var onForwardPageChangeListener: OnCalendarPageChangeListener? = null
    private var mEventDays: List<EventDay> = ArrayList()
    var disabledDays: List<Calendar> = ArrayList()
        private set
    private var mHighlightedDays: List<Calendar> = ArrayList()
    var selectedDays: MutableList<SelectedDay> = ArrayList()
        private set
    var headerColor: Int
        get() = if (mHeaderColor <= 0) {
            mHeaderColor
        } else ContextCompat.getColor(mContext, mHeaderColor)
        set(headerColor) {
            mHeaderColor = headerColor
        }
    var headerLabelColor: Int
        get() = if (mHeaderLabelColor <= 0) {
            mHeaderLabelColor
        } else ContextCompat.getColor(mContext, mHeaderLabelColor)
        set(headerLabelColor) {
            mHeaderLabelColor = headerLabelColor
        }
    var selectionColor: Int
        get() = if (mSelectionColor == 0) {
            ContextCompat.getColor(mContext, R.color.defaultColor)
        } else mSelectionColor
        set(selectionColor) {
            mSelectionColor = selectionColor
        }
    var todayLabelColor: Int
        get() = if (mTodayLabelColor == 0) {
            ContextCompat.getColor(mContext, R.color.defaultColor)
        } else mTodayLabelColor
        set(todayLabelColor) {
            mTodayLabelColor = todayLabelColor
        }

    fun getEventDays() = mEventDays

    fun setEventDays(eventDays: List<EventDay>) {
        mEventDays = eventDays
    }

    fun setDisabledDays(disabledDays: List<Calendar>) {
        selectedDays.removeAll(disabledDays)
        this.disabledDays = Stream.of(disabledDays)
            .map { calendar: Calendar ->
                DateUtils.setMidnight(calendar)
                calendar
            }.toList()
    }

    var highlightedDays: List<Calendar>
        get() = mHighlightedDays
        set(highlightedDays) {
            mHighlightedDays = Stream.of(highlightedDays)
                .map { calendar: Calendar ->
                    DateUtils.setMidnight(calendar)
                    calendar
                }.toList()
        }

    fun setSelectedDay(calendar: Calendar?) {
        setSelectedDay(SelectedDay(calendar))
    }

    fun setSelectedDay(selectedDay: SelectedDay) {
        selectedDays.clear()
        selectedDays.add(selectedDay)
    }

    fun setSelectedDays(selectedDays: List<Calendar>?) {
        if (calendarType == CalendarView.ONE_DAY_PICKER) {
            throw UnsupportedMethodsException(ErrorsMessages.ONE_DAY_PICKER_MULTIPLE_SELECTION)
        }
        this.selectedDays = Stream.of(selectedDays)
            .map { calendar: Calendar? ->
                DateUtils.setMidnight(calendar)
                SelectedDay(calendar)
            }.filterNot { value: SelectedDay -> disabledDays.contains(value.calendar) }
            .toList()
    }

    var disabledDaysLabelsColor: Int
        get() = if (mDisabledDaysLabelsColor == 0) {
            ContextCompat.getColor(mContext, R.color.nextMonthDayColor)
        } else mDisabledDaysLabelsColor
        set(disabledDaysLabelsColor) {
            mDisabledDaysLabelsColor = disabledDaysLabelsColor
        }
    var highlightedDaysLabelsColor: Int
        get() = if (mHighlightedDaysLabelsColor == 0) {
            ContextCompat.getColor(mContext, R.color.nextMonthDayColor)
        } else mHighlightedDaysLabelsColor
        set(highlightedDaysLabelsColor) {
            mHighlightedDaysLabelsColor = highlightedDaysLabelsColor
        }
    var daysLabelsColor: Int
        get() = if (mDaysLabelsColor == 0) {
            ContextCompat.getColor(mContext, R.color.currentMonthDayColor)
        } else mDaysLabelsColor
        set(daysLabelsColor) {
            mDaysLabelsColor = daysLabelsColor
        }
    var selectionLabelColor: Int
        get() = if (mSelectionLabelColor == 0) {
            ContextCompat.getColor(mContext, android.R.color.white)
        } else mSelectionLabelColor
        set(selectionLabelColor) {
            mSelectionLabelColor = selectionLabelColor
        }
    var anotherMonthsDaysLabelsColor: Int
        get() = if (mAnotherMonthsDaysLabelsColor == 0) {
            ContextCompat.getColor(mContext, R.color.nextMonthDayColor)
        } else mAnotherMonthsDaysLabelsColor
        set(anotherMonthsDaysLabelsColor) {
            mAnotherMonthsDaysLabelsColor = anotherMonthsDaysLabelsColor
        }

    fun getmFirstDayOfWeek(): Int {
        return mFirstDayOfWeek
    }

    fun setmFirstDayOfWeek(mFirstDayOfWeek: Int) {
        this.mFirstDayOfWeek = mFirstDayOfWeek
    }

    companion object {
        /**
         * A number of months (pages) in the calendar
         * 481 months means 240 months (20 years) before and 240 months after the current month
         */
        const val CALENDAR_SIZE = 481
        const val FIRST_VISIBLE_PAGE = CALENDAR_SIZE / 2
    }
}
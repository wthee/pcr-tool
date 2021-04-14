package com.applandeo.materialcalendarview.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.applandeo.materialcalendarview.R
import com.applandeo.materialcalendarview.extensions.CalendarGridView
import com.applandeo.materialcalendarview.listeners.DayRowClickListener
import com.applandeo.materialcalendarview.utils.CalendarProperties
import com.applandeo.materialcalendarview.utils.SelectedDay
import java.util.*

/**
 * This class is responsible for loading a calendar page content.
 *
 *
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 *
 * Modified by wthee
 */
class CalendarPageAdapter(
    private val mContext: Context,
    private val mCalendarProperties: CalendarProperties
) : PagerAdapter() {
    private var mCalendarGridView: CalendarGridView? = null
    private var mPageMonth = 0
    override fun getCount(): Int {
        return CalendarProperties.CALENDAR_SIZE
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mCalendarGridView = inflater.inflate(R.layout.calendar_view_grid, null) as CalendarGridView
        loadMonth(position)
        mCalendarGridView!!.onItemClickListener = DayRowClickListener(
            this,
            mCalendarProperties, mPageMonth
        )
        container.addView(mCalendarGridView)
        return mCalendarGridView!!
    }

    fun addSelectedDay(selectedDay: SelectedDay) {
        if (!mCalendarProperties.selectedDays.contains(selectedDay)) {
            mCalendarProperties.selectedDays.add(selectedDay)
            informDatePicker()
            return
        }
        mCalendarProperties.selectedDays.remove(selectedDay)
        informDatePicker()
    }

    val selectedDays: MutableList<SelectedDay>
        get() = mCalendarProperties.selectedDays
    var selectedDay: SelectedDay
        get() = mCalendarProperties.selectedDays[0]
        set(selectedDay) {
            mCalendarProperties.setSelectedDay(selectedDay)
            informDatePicker()
        }

    /**
     * This method inform DatePicker about ability to return selected days
     */
    private fun informDatePicker() {
        if (mCalendarProperties.onSelectionAbilityListener != null) {
            mCalendarProperties.onSelectionAbilityListener!!
                .onChange(mCalendarProperties.selectedDays.size > 0)
        }
    }

    /**
     * This method fill calendar GridView with days
     *
     * @param position Position of current page in ViewPager
     */
    private fun loadMonth(position: Int) {
        val days = ArrayList<Date>()

        // Get Calendar object instance
        val calendar = mCalendarProperties.firstPageCalendarDate.clone() as Calendar

        // Add months to Calendar (a number of months depends on ViewPager position)
        calendar.add(Calendar.MONTH, position)

        // Set day of month as 1
        calendar[Calendar.DAY_OF_MONTH] = 1

        // Get a number of the first day of the week
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        val currentMonth = calendar[Calendar.MONTH] + 1

        // Count when month is beginning
        val firstDayOfWeek = mCalendarProperties.getmFirstDayOfWeek()
        val monthBeginningCell =
            (if (dayOfWeek < firstDayOfWeek) 7 else 0) + dayOfWeek - firstDayOfWeek

        // Subtract a number of beginning days, it will let to load a part of a previous month
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        /*
        Get all days of a month
         */while (days.size < monthBeginningCell + calendar[Calendar.DAY_OF_MONTH]) {
            // days of the month
            if (currentMonth == calendar[Calendar.MONTH] + 1) {
                days.add(calendar.time)
            } else {
                // hide days of pre month
                days.add(Date(0))
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        mPageMonth = calendar[Calendar.MONTH] - 1
        val calendarDayAdapter = CalendarDayAdapter(
            this, mContext,
            mCalendarProperties, days, mPageMonth
        )
        mCalendarGridView!!.adapter = calendarDayAdapter
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    init {
        informDatePicker()
    }
}
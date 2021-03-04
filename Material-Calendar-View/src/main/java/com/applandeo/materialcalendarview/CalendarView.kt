package com.applandeo.materialcalendarview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.annimon.stream.Stream
import com.applandeo.materialcalendarview.adapters.CalendarPageAdapter
import com.applandeo.materialcalendarview.exceptions.ErrorsMessages
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException
import com.applandeo.materialcalendarview.extensions.CalendarViewPager
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.applandeo.materialcalendarview.utils.AppearanceUtils
import com.applandeo.materialcalendarview.utils.CalendarProperties
import com.applandeo.materialcalendarview.utils.DateUtils
import com.applandeo.materialcalendarview.utils.SelectedDay
import java.util.*

/**
 * This class represents a view, displays to user as calendar. It allows to work in date picker
 * mode or like a normal calendar. In a normal calendar mode it can displays an image under the day
 * number. In both modes it marks today day. It also provides click on day events using
 * OnDayClickListener which returns an EventDay object.
 *
 * @see EventDay
 *
 * @see OnDayClickListener
 *
 *
 *
 *
 * XML attributes:
 * - Set calendar type: type="classic or one_day_picker or many_days_picker or range_picker"
 * - Set calendar header color: headerColor="@color/[color]"
 * - Set calendar header label color: headerLabelColor="@color/[color]"
 * - Set previous button resource: previousButtonSrc="@drawable/[drawable]"
 * - Ser forward button resource: forwardButtonSrc="@drawable/[drawable]"
 * - Set today label color: todayLabelColor="@color/[color]"
 * - Set selection color: selectionColor="@color/[color]"
 *
 *
 * Created by Mateusz Kornakiewicz on 23.05.2017.
 *
 * Modified by wthee
 */
class CalendarView : LinearLayout {
    private lateinit var mContext: Context
    private lateinit var mCalendarPageAdapter: CalendarPageAdapter
    private var mForwardButton: ImageButton? = null
    private var mPreviousButton: ImageButton? = null
    private var mCurrentMonthLabel: TextView? = null
    private var mCurrentPage = 0
    private var mFirstDayOfWeek = 1
    private var mViewPager: CalendarViewPager? = null
    private lateinit var mCalendarProperties: CalendarProperties

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initControl(context, attrs)
        initCalendar()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initControl(context, attrs)
        initCalendar()
    }

    //protected constructor to create CalendarView for the dialog date picker
    protected constructor(context: Context, calendarProperties: CalendarProperties) : super(
        context
    ) {
        mContext = context
        mCalendarProperties = calendarProperties
        val inflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.calendar_view, this)
        initUiElements()
        initAttributes()
        initCalendar()
    }

    private fun initControl(context: Context, attrs: AttributeSet?) {
        mContext = context
        mCalendarProperties = CalendarProperties(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.calendar_view, this)
        initUiElements()
        setAttributes(attrs)
    }

    /**
     * This method set xml values for calendar elements
     *
     * @param attrs A set of xml attributes
     */
    private fun setAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        try {
            initCalendarProperties(typedArray)
            initAttributes()
        } finally {
            typedArray.recycle()
        }
    }

    private fun initCalendarProperties(typedArray: TypedArray) {
        val headerColor = typedArray.getColor(R.styleable.CalendarView_headerColor, 0)
        mCalendarProperties.headerColor = headerColor
        val headerLabelColor = typedArray.getColor(R.styleable.CalendarView_headerLabelColor, 0)
        mCalendarProperties.headerLabelColor = headerLabelColor
        val abbreviationsBarColor =
            typedArray.getColor(R.styleable.CalendarView_abbreviationsBarColor, 0)
        mCalendarProperties.abbreviationsBarColor = abbreviationsBarColor
        val abbreviationsLabelsColor =
            typedArray.getColor(R.styleable.CalendarView_abbreviationsLabelsColor, 0)
        mCalendarProperties.abbreviationsLabelsColor = abbreviationsLabelsColor
        val pagesColor = typedArray.getColor(R.styleable.CalendarView_pagesColor, 0)
        mCalendarProperties.pagesColor = pagesColor
        val daysLabelsColor = typedArray.getColor(R.styleable.CalendarView_daysLabelsColor, 0)
        mCalendarProperties.daysLabelsColor = daysLabelsColor
        val anotherMonthsDaysLabelsColor =
            typedArray.getColor(R.styleable.CalendarView_anotherMonthsDaysLabelsColor, 0)
        mCalendarProperties.anotherMonthsDaysLabelsColor = anotherMonthsDaysLabelsColor
        val todayLabelColor = typedArray.getColor(R.styleable.CalendarView_todayLabelColor, 0)
        mCalendarProperties.todayLabelColor = todayLabelColor
        val selectionColor = typedArray.getColor(R.styleable.CalendarView_selectionColor, 0)
        mCalendarProperties.selectionColor = selectionColor
        val selectionLabelColor =
            typedArray.getColor(R.styleable.CalendarView_selectionLabelColor, 0)
        mCalendarProperties.selectionLabelColor = selectionLabelColor
        val disabledDaysLabelsColor =
            typedArray.getColor(R.styleable.CalendarView_disabledDaysLabelsColor, 0)
        mCalendarProperties.disabledDaysLabelsColor = disabledDaysLabelsColor
        val highlightedDaysLabelsColor =
            typedArray.getColor(R.styleable.CalendarView_highlightedDaysLabelsColor, 0)
        mCalendarProperties.highlightedDaysLabelsColor = highlightedDaysLabelsColor
        val calendarType = typedArray.getInt(R.styleable.CalendarView_type, ONE_DAY_PICKER)
        mCalendarProperties.calendarType = calendarType
        val maximumDaysRange = typedArray.getInt(R.styleable.CalendarView_maximumDaysRange, 0)
        mCalendarProperties.maximumDaysRange = maximumDaysRange

        // Set picker mode !DEPRECATED!
        if (typedArray.getBoolean(R.styleable.CalendarView_datePicker, false)) {
            mCalendarProperties.calendarType = ONE_DAY_PICKER
        }
        val eventsEnabled = typedArray.getBoolean(R.styleable.CalendarView_eventsEnabled, true)
        mCalendarProperties.eventsEnabled = eventsEnabled
        val swipeEnabled = typedArray.getBoolean(R.styleable.CalendarView_swipeEnabled, true)
        mCalendarProperties.swipeEnabled = swipeEnabled
        val previousButtonSrc = typedArray.getDrawable(R.styleable.CalendarView_previousButtonSrc)
        mCalendarProperties.previousButtonSrc = previousButtonSrc
        val forwardButtonSrc = typedArray.getDrawable(R.styleable.CalendarView_forwardButtonSrc)
        mCalendarProperties.forwardButtonSrc = forwardButtonSrc
        val firstDayOfWeek = typedArray.getInt(R.styleable.CalendarView_firstDayOfWeek, 1)
        setmFirstDayOfWeek(firstDayOfWeek)
    }

    private fun initAttributes() {
        AppearanceUtils.setHeaderColor(rootView, mCalendarProperties.headerColor)
        AppearanceUtils.setHeaderVisibility(rootView, mCalendarProperties.headerVisibility)
        AppearanceUtils.setAbbreviationsBarVisibility(
            rootView,
            mCalendarProperties.abbreviationsBarVisibility
        )
        AppearanceUtils.setNavigationVisibility(
            rootView,
            mCalendarProperties.navigationVisibility
        )
        AppearanceUtils.setHeaderLabelColor(rootView, mCalendarProperties.headerLabelColor)
        AppearanceUtils.setAbbreviationsBarColor(
            rootView,
            mCalendarProperties.abbreviationsBarColor
        )
        AppearanceUtils.setAbbreviationsLabels(
            rootView, mCalendarProperties.abbreviationsLabelsColor,
            mFirstDayOfWeek
        )
        AppearanceUtils.setPagesColor(rootView, mCalendarProperties.pagesColor)
        AppearanceUtils.setPreviousButtonImage(rootView, mCalendarProperties.previousButtonSrc)
        AppearanceUtils.setForwardButtonImage(rootView, mCalendarProperties.forwardButtonSrc)
        mViewPager!!.setSwipeEnabled(mCalendarProperties.swipeEnabled)

        // Sets layout for date picker or normal calendar
        setCalendarRowLayout()
    }

    fun setHeaderColor(@ColorRes color: Int) {
        mCalendarProperties.headerColor = color
        AppearanceUtils.setHeaderColor(rootView, mCalendarProperties.headerColor)
    }

    fun setHeaderVisibility(visibility: Int) {
        mCalendarProperties.headerVisibility = visibility
        AppearanceUtils.setHeaderVisibility(rootView, mCalendarProperties.headerVisibility)
    }

    fun setAbbreviationsBarVisibility(visibility: Int) {
        mCalendarProperties.abbreviationsBarVisibility = visibility
        AppearanceUtils.setAbbreviationsBarVisibility(
            rootView,
            mCalendarProperties.abbreviationsBarVisibility
        )
    }

    fun setHeaderLabelColor(@ColorRes color: Int) {
        mCalendarProperties.headerLabelColor = color
        AppearanceUtils.setHeaderLabelColor(rootView, mCalendarProperties.headerLabelColor)
    }

    fun setPreviousButtonImage(drawable: Drawable?) {
        mCalendarProperties.previousButtonSrc = drawable
        AppearanceUtils.setPreviousButtonImage(rootView, mCalendarProperties.previousButtonSrc)
    }

    fun setForwardButtonImage(drawable: Drawable?) {
        mCalendarProperties.forwardButtonSrc = drawable
        AppearanceUtils.setForwardButtonImage(rootView, mCalendarProperties.forwardButtonSrc)
    }

    private fun setCalendarRowLayout() {
        mCalendarProperties.itemLayoutResource = R.layout.calendar_view_day
    }

    private fun initUiElements() {
        mForwardButton = findViewById<View>(R.id.forwardButton) as ImageButton
        mForwardButton!!.setOnClickListener(onNextClickListener)
        mPreviousButton = findViewById<View>(R.id.previousButton) as ImageButton
        mPreviousButton!!.setOnClickListener(onPreviousClickListener)
        mCurrentMonthLabel = findViewById<View>(R.id.currentDateLabel) as TextView
        mViewPager = findViewById<View>(R.id.calendarViewPager) as CalendarViewPager
    }

    private fun initCalendar() {
        mCalendarPageAdapter = CalendarPageAdapter(mContext, mCalendarProperties)
        mViewPager!!.adapter = mCalendarPageAdapter
        mViewPager!!.addOnPageChangeListener(onPageChangeListener)
        setUpCalendarPosition(Calendar.getInstance())
    }

    private fun setUpCalendarPosition(calendar: Calendar) {
        DateUtils.setMidnight(calendar)
        if (mCalendarProperties.calendarType == ONE_DAY_PICKER) {
            mCalendarProperties.setSelectedDay(calendar)
        }
        mCalendarProperties.firstPageCalendarDate.time = calendar.time
        mCalendarProperties.firstPageCalendarDate
            .add(Calendar.MONTH, -CalendarProperties.FIRST_VISIBLE_PAGE)
        mViewPager!!.currentItem = CalendarProperties.FIRST_VISIBLE_PAGE
    }

    fun setOnPreviousPageChangeListener(listener: OnCalendarPageChangeListener?) {
        mCalendarProperties.onPreviousPageChangeListener = listener
    }

    fun setOnForwardPageChangeListener(listener: OnCalendarPageChangeListener?) {
        mCalendarProperties.onForwardPageChangeListener = listener
    }

    private val onNextClickListener =
        OnClickListener { _ -> mViewPager!!.currentItem = mViewPager!!.currentItem + 1 }
    private val onPreviousClickListener =
        OnClickListener { _ -> mViewPager!!.currentItem = mViewPager!!.currentItem - 1 }
    private val onPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        /**
         * This method set calendar header label
         *
         * @param position Current ViewPager position
         * @see ViewPager.OnPageChangeListener
         */
        override fun onPageSelected(position: Int) {
            val calendar = mCalendarProperties.firstPageCalendarDate.clone() as Calendar
            calendar.add(Calendar.MONTH, position)
            if (!isScrollingLimited(calendar, position)) {
                setHeaderName(calendar, position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun isScrollingLimited(calendar: Calendar, position: Int): Boolean {
        if (DateUtils.isMonthBefore(mCalendarProperties.minimumDate, calendar)) {
            mViewPager!!.currentItem = position + 1
            return true
        }
        if (DateUtils.isMonthAfter(mCalendarProperties.maximumDate, calendar)) {
            mViewPager!!.currentItem = position - 1
            return true
        }
        return false
    }

    private fun setHeaderName(calendar: Calendar, position: Int) {
        mCurrentMonthLabel!!.text =
            DateUtils.getMonthAndYearDate(mContext, calendar)
        callOnPageChangeListeners(position)
    }

    // This method calls page change listeners after swipe calendar or click arrow buttons
    private fun callOnPageChangeListeners(position: Int) {
        if (position > mCurrentPage && mCalendarProperties.onForwardPageChangeListener != null) {
            mViewPager!!.reMeasureCurrentPage()
            mCalendarProperties.onForwardPageChangeListener!!.onChange()
        }
        if (position < mCurrentPage && mCalendarProperties.onPreviousPageChangeListener != null) {
            mViewPager!!.reMeasureCurrentPage()
            mCalendarProperties.onPreviousPageChangeListener!!.onChange()
        }
        mCurrentPage = position
    }

    /**
     * @param onDayClickListener OnDayClickListener interface responsible for handle clicks on calendar cells
     * @see OnDayClickListener
     */
    fun setOnDayClickListener(onDayClickListener: OnDayClickListener?) {
        mCalendarProperties.onDayClickListener = onDayClickListener
    }

    /**
     * This method set a current and selected date of the calendar using Calendar object.
     *
     * @param date A Calendar object representing a date to which the calendar will be set
     */
    @Throws(OutOfDateRangeException::class)
    fun setDate(date: Calendar) {
        if (mCalendarProperties.minimumDate != null && date.before(mCalendarProperties.minimumDate)) {
            throw OutOfDateRangeException(ErrorsMessages.OUT_OF_RANGE_MIN)
        }
        if (mCalendarProperties.maximumDate != null && date.after(mCalendarProperties.maximumDate)) {
            throw OutOfDateRangeException(ErrorsMessages.OUT_OF_RANGE_MAX)
        }
        setUpCalendarPosition(date)
        mCurrentMonthLabel!!.text =
            DateUtils.getMonthAndYearDate(mContext, date)
        mCalendarPageAdapter.notifyDataSetChanged()
    }

    /**
     * This method set a current and selected date of the calendar using Date object.
     *
     * @param currentDate A date to which the calendar will be set
     */
    @Throws(OutOfDateRangeException::class)
    fun setDate(currentDate: Date?) {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        setDate(calendar)
    }

    /**
     * This method is used to set a list of events displayed in calendar cells,
     * visible as images under the day number.
     *
     * @param eventDays List of EventDay objects
     * @see EventDay
     */
    fun setEvents(eventDays: List<EventDay>) {
        if (mCalendarProperties.eventsEnabled) {
            mCalendarProperties.setEventDays(eventDays)
            mCalendarPageAdapter.notifyDataSetChanged()
        }
    }

    /**
     * @return List of Calendar object representing a selected dates
     */
    val selectedDates: List<Calendar?>
        get() = Stream.of(mCalendarPageAdapter.selectedDays)
            .map { obj: SelectedDay -> obj.calendar }
            .sortBy { calendar: Calendar? -> calendar }.toList()

    fun setSelectedDates(selectedDates: List<Calendar>?) {
        mCalendarProperties.setSelectedDays(selectedDates)
        mCalendarPageAdapter.notifyDataSetChanged()
    }

    /**
     * @return Calendar object representing a selected date
     */
    @get:Deprecated("")
    val selectedDate: Calendar?
        get() = firstSelectedDate

    /**
     * @return Calendar object representing a selected date
     */
    val firstSelectedDate: Calendar?
        get() = Stream.of(mCalendarPageAdapter.selectedDays)
            .map { obj: SelectedDay -> obj.calendar }.findFirst().get()

    /**
     * @return Calendar object representing a date of current calendar page
     */
    val currentPageDate: Calendar
        get() {
            val calendar = mCalendarProperties.firstPageCalendarDate.clone() as Calendar
            calendar[Calendar.DAY_OF_MONTH] = 1
            calendar.add(Calendar.MONTH, mViewPager!!.currentItem)
            return calendar
        }

    /**
     * This method set a minimum available date in calendar
     *
     * @param calendar Calendar object representing a minimum date
     */
    fun setMinimumDate(calendar: Calendar?) {
        mCalendarProperties.minimumDate = calendar
    }

    /**
     * This method set a maximum available date in calendar
     *
     * @param calendar Calendar object representing a maximum date
     */
    fun setMaximumDate(calendar: Calendar?) {
        mCalendarProperties.maximumDate = calendar
    }

    /**
     * This method is used to return to current month page
     */
    fun showCurrentMonthPage() {
        mViewPager!!.setCurrentItem(
            mViewPager!!.currentItem
                    - DateUtils.getMonthsBetweenDates(DateUtils.calendar, currentPageDate),
            true
        )
    }

    fun setDisabledDays(disabledDays: List<Calendar>) {
        mCalendarProperties.setDisabledDays(disabledDays)
    }

    fun setHighlightedDays(highlightedDays: List<Calendar>) {
        mCalendarProperties.highlightedDays = highlightedDays
    }

    fun setSwipeEnabled(swipeEnabled: Boolean) {
        mCalendarProperties.swipeEnabled = swipeEnabled
        mViewPager!!.setSwipeEnabled(mCalendarProperties.swipeEnabled)
    }

    fun setmFirstDayOfWeek(mFirstDayOfWeek: Int) {
        mCalendarProperties.setmFirstDayOfWeek(mFirstDayOfWeek)
        this.mFirstDayOfWeek = mFirstDayOfWeek
    }

    companion object {
        const val ONE_DAY_PICKER = 1
    }
}
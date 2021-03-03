package com.applandeo.materialcalendarview

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.RestrictTo
import com.applandeo.materialcalendarview.utils.DateUtils
import java.util.*

/**
 * This class represents an event of a day. An instance of this class is returned when user click
 * a day cell. This class can be overridden to make calendar more functional. A list of instances of
 * this class can be passed to CalendarView object using setEvents() method.
 *
 *
 * Created by Mateusz Kornakiewicz on 23.05.2017.
 */
class EventDay {
    /**
     * @return Calendar object which represents a date of current event
     */
    var calendar: Calendar
        private set

    /**
     * @return An image resource which will be displayed in the day row
     */
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    var imageDrawable: Any? = null
        private set

    /**
     * @return Color which will be displayed as row label text color
     */
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    var labelColor = 0
        private set
    private var mIsDisabled = false

    /**
     * @param day Calendar object which represents a date of the event
     */
    constructor(day: Calendar) {
        calendar = day
    }

    /**
     * @param day      Calendar object which represents a date of the event
     * @param drawable Drawable resource which will be displayed in a day cell
     */
    constructor(day: Calendar, @DrawableRes drawable: Int) {
        DateUtils.setMidnight(day)
        calendar = day
        imageDrawable = drawable
    }

    /**
     * @param day      Calendar object which represents a date of the event
     * @param drawable Drawable which will be displayed in a day cell
     */
    constructor(day: Calendar, drawable: Drawable?) {
        DateUtils.setMidnight(day)
        calendar = day
        imageDrawable = drawable
    }

    /**
     * @param day        Calendar object which represents a date of the event
     * @param drawable   Drawable resource which will be displayed in a day cell
     * @param labelColor Color which will be displayed as label text color a day cell
     */
    constructor(day: Calendar, @DrawableRes drawable: Int, labelColor: Int) {
        DateUtils.setMidnight(day)
        calendar = day
        imageDrawable = drawable
        this.labelColor = labelColor
    }

    /**
     * @param day        Calendar object which represents a date of the event
     * @param drawable   Drawable which will be displayed in a day cell
     * @param labelColor Color which will be displayed as label text color a day cell
     */
    constructor(day: Calendar, drawable: Drawable?, labelColor: Int) {
        DateUtils.setMidnight(day)
        calendar = day
        imageDrawable = drawable
        this.labelColor = labelColor
    }

    /**
     * @return Boolean value if day is not disabled
     */
    @set:RestrictTo(RestrictTo.Scope.LIBRARY)
    var isEnabled: Boolean
        get() = !mIsDisabled
        set(enabled) {
            mIsDisabled = enabled
        }
}
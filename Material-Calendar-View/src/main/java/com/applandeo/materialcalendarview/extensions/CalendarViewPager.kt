package com.applandeo.materialcalendarview.extensions

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by Mateusz Kornakiewicz on 21.11.2017.
 *
 * Modified by wthee
 */
class CalendarViewPager : ViewPager {
    private var mSwipeEnabled = true

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mHeightMeasureSpec = heightMeasureSpec
        try {
            val child = getChildAt(0)
            if (child != null) {
                child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                val h = child.measuredHeight
                mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
    }

    fun reMeasureCurrentPage() {
        requestLayout()
    }

    fun setSwipeEnabled(swipeEnabled: Boolean) {
        mSwipeEnabled = swipeEnabled
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mSwipeEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return mSwipeEnabled && super.onInterceptTouchEvent(event)
    }
}
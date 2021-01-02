package cn.wthee.pcrtool.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import cn.wthee.pcrtool.R
import kotlin.math.ceil


class TasksCompletedView : View {

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // 获取自定义的属性
        initAttrs(context, attrs)
        initVariable()
    }

    // 画实心圆的画笔
    private var mCirclePaint: Paint? = null

    // 画圆环的画笔
    private var mRingPaint: Paint? = null

    // 画字体的画笔
    private var mTextPaint: Paint? = null

    // 圆形颜色
    private var mCircleColor = 0

    // 圆环颜色
    private var mRingColor = 0

    // 半径
    private var mRadius = 0f

    // 圆环半径
    private var mRingRadius = 0f

    // 圆环宽度
    private var mStrokeWidth = 0f

    // 圆心x坐标
    private var mXCenter = 0

    // 圆心y坐标
    private var mYCenter = 0

    // 字的长度
    private var mTxtWidth = 0f

    // 字的高度
    private var mTxtHeight = 0f

    // 总进度
    private val mTotalProgress = 100

    // 当前进度
    private var mProgress = 0
    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val typeArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TasksCompletedView, 0, 0
        )
        mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, 80f)
        mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 10f)
        mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, -0x1)
        mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, -0x1)
        mRingRadius = mRadius + mStrokeWidth / 2
    }

    private fun initVariable() {
        mCirclePaint = Paint()
        mCirclePaint!!.isAntiAlias = true
        mCirclePaint!!.color = mCircleColor
        mCirclePaint!!.style = Paint.Style.FILL
        mRingPaint = Paint()
        mRingPaint!!.isAntiAlias = true
        mRingPaint!!.color = mRingColor
        mRingPaint!!.style = Paint.Style.STROKE
        mRingPaint!!.strokeWidth = mStrokeWidth
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.style = Paint.Style.FILL
        mTextPaint!!.setARGB(255, 255, 255, 255)
        mTextPaint!!.textSize = mRadius / 2
        val fm = mTextPaint!!.fontMetrics
        mTxtHeight = ceil((fm.descent - fm.ascent).toDouble()).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        mXCenter = width / 2
        mYCenter = height / 2
        canvas.drawCircle(mXCenter.toFloat(), mYCenter.toFloat(), mRadius, mCirclePaint!!)
        if (mProgress > 0) {
            val oval = RectF()
            oval.left = mXCenter - mRingRadius
            oval.top = mYCenter - mRingRadius
            oval.right = mRingRadius * 2 + (mXCenter - mRingRadius)
            oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius)
            canvas.drawArc(
                oval, -90f, mProgress.toFloat() / mTotalProgress * 360, false,
                mRingPaint!!
            ) //
            //            canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);
            val txt = "$mProgress%"
            mTxtWidth = mTextPaint!!.measureText(txt, 0, txt.length)
            canvas.drawText(
                txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4,
                mTextPaint!!
            )
        }
    }

    fun setProgress(progress: Int) {
        mProgress = progress
        postInvalidate()
    }

}
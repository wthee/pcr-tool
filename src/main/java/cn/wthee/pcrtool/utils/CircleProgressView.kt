package cn.wthee.pcrtool.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import cn.wthee.pcrtool.R
import kotlin.math.ceil


class CircleProgressView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
        initVariable()
        startAnimation()
    }

    //状态-准备加载
    private val STATUS_LOADING = 0

    //状态-开始加载
    private val STATUS_START = 1

    //风格-默认
    private val TYPE_DEFAULT = 0

    //风格-flyme
    private val TYPE_FLYME = 1

    //进度条状态
    private var mStatus = STATUS_LOADING

    // 画实心圆的画笔
    private var mCirclePaint = Paint()

    // 画圆环的画笔
    private var mRingPaint = Paint()

    // 画圆环背景的画笔
    private var mRingBackgroundPaint = Paint()

    // 画字体的画笔
    private var mTextPaint = Paint()

    // 圆形颜色
    private var mCircleColor = 0

    // 圆环颜色
    private var mRingColor = 0

    // 圆环背景颜色
    private var mRingBackgroundColor = 0

    //字体颜色
    private var mTextColor = 0

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
    private var mMaxProgress = 100

    // 当前进度
    private var mProgress = 0

    //进度条风格
    private var mType = TYPE_DEFAULT

    //进度条长度 $
    private var mProgressLength = 120f

    //动画进度
    var value = 0f
    var maxValue = 360f
    var angle = mProgressLength


    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val typeArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircleProgressView, 0, 0
        )
        mRadius = typeArray.getDimension(R.styleable.CircleProgressView_radius, 80f)
        mStrokeWidth = typeArray.getDimension(R.styleable.CircleProgressView_strokeWidth, 10f)
        mCircleColor =
            typeArray.getColor(R.styleable.CircleProgressView_circleColor, Color.TRANSPARENT)
        mRingColor = typeArray.getColor(R.styleable.CircleProgressView_ringColor, Color.GREEN)
        mRingBackgroundColor =
            typeArray.getColor(R.styleable.CircleProgressView_ringBackgroundColor, Color.LTGRAY)
        mTextColor = typeArray.getColor(R.styleable.CircleProgressView_textColor, Color.GREEN)
        mRingRadius = mRadius + mStrokeWidth / 2
        mType = typeArray.getInt(R.styleable.CircleProgressView_ringType, TYPE_DEFAULT)
    }

    private fun initVariable() {
        mCirclePaint.isAntiAlias = true
        mCirclePaint.color = mCircleColor
        mCirclePaint.style = Paint.Style.FILL
        mRingPaint.isAntiAlias = true
        mRingPaint.color = mRingColor
        mRingPaint.style = Paint.Style.STROKE
        mRingPaint.strokeWidth = mStrokeWidth
        mRingBackgroundPaint.isAntiAlias = true
        mRingBackgroundPaint.color = mRingBackgroundColor
        mRingBackgroundPaint.style = Paint.Style.STROKE
        mRingBackgroundPaint.strokeWidth = mStrokeWidth
        mTextPaint.isAntiAlias = true
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.color = mTextColor
        mTextPaint.textSize = mRadius / 2
        val fm = mTextPaint.fontMetrics
        mTxtHeight = ceil((fm.descent - fm.ascent).toDouble()).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        mXCenter = width / 2
        mYCenter = height / 2
        val oval = RectF()
        oval.left = mXCenter - mRingRadius
        oval.top = mYCenter - mRingRadius
        oval.right = mRingRadius * 2 + (mXCenter - mRingRadius)
        oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius)
        //绘制圆形
        canvas.drawCircle(mXCenter.toFloat(), mYCenter.toFloat(), mRadius, mCirclePaint)
        //绘制背景圆弧
        canvas.drawArc(
            oval, 0f, 360f, false,
            mRingBackgroundPaint
        )
        //开始绘制进度
        if (mStatus == STATUS_START) {
            if (mProgress > 0) {
                when (mType) {
                    TYPE_DEFAULT -> { //绘制左圆弧
                        //绘制圆弧
                        canvas.drawArc(
                            oval,
                            -90f,
                            mProgress.toFloat() / mMaxProgress * 360f,
                            false,
                            mRingPaint
                        )
                    }
                    TYPE_FLYME -> { //绘制左圆弧
                        canvas.drawArc(
                            oval,
                            90f,
                            mProgress.toFloat() / mMaxProgress * 180f,
                            false,
                            mRingPaint
                        )
                        //绘制右圆弧
                        canvas.drawArc(
                            oval,
                            90f,
                            -mProgress.toFloat() / mMaxProgress * 180f,
                            false,
                            mRingPaint
                        )
                    }


                }
                val txt = "$mProgress%"
                mTxtWidth = mTextPaint.measureText(txt, 0, txt.length)
                //绘制文本
                canvas.drawText(
                    txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4,
                    mTextPaint
                )
            }
        } else if (mStatus == STATUS_LOADING) {
            val startAngle = value + 90 - mProgressLength / 2
            //绘制加载进度条
            canvas.drawArc(
                oval, startAngle, mProgressLength, false,
                mRingPaint
            )

        }

    }

    fun setProgress(progress: Int) {
        mStatus = STATUS_START
        mProgress = if (progress > mMaxProgress) {
            mMaxProgress
        } else {
            progress
        }
        postInvalidate()
    }

    fun getMaxProgress() = mMaxProgress


    private fun startAnimation() {
        val anim = ValueAnimator.ofFloat(value, maxValue)
        anim.repeatCount = ValueAnimator.INFINITE //设置无限重复
        anim.interpolator = LinearInterpolator()
//        anim.repeatMode = ValueAnimator.REVERSE
        anim.duration = 600
        anim.addUpdateListener { animation ->
            value = animation.animatedValue as Float
            postInvalidate()
        }
        anim.start()
    }
}
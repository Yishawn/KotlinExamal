package kot.com.baselibrary.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.support.v4.content.ContextCompat
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import kot.com.baselibrary.R
import kot.com.baselibrary.utils.APPSystemUtils
import kot.com.baselibrary.utils.DensityUtils


/**
 * Created by yixiao on 2018/6/29.
 */
class DotPollingView : View, Animatable {
    override fun isRunning(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val TAG = this.javaClass.simpleName
    /**
     * 进度当前圆点画笔和正常圆点画笔
     */
    private val mSelectedPaint = Paint()
    private val mNormalPaint = Paint()
    /**
     * 正常圆点颜色
     */
    private var mColor: Int = 0
    // Y轴的值
    private var coinX : Float = 0f
    /**
     * 变大圆点的颜色
     */
    private var mSelectedColor: Int = 0
    /**
     * 圆点总数
     */
    private var mDotTotalCount = 3
    /**
     * 正常圆点半径
     */
    private var mDotRadius: Int = 0
    /**
     * 当前变化的圆点半径变化量 0.0 - (mDotMaxRadius - mDotRadius)之间
     */
    private var mDotCurrentRadiusChange: Float = 0.toFloat()
    /**
     * 圆点大小变化率
     */
    private var mRadiusChangeRate: Float = 0.toFloat()
    /**
     * 最大圆点半径
     */
    private var mDotMaxRadius: Int = 0
    /**
     * 圆点最大间距
     */
    private var mDotSpacing: Int = 0
    /**
     * 当前变大的圆点索引
     */
    private var mCurrentDot = 0
    // 动画
    private lateinit var mValueAnimator: ValueAnimator
    private var mAlphaChange = 0
    private val mAlphaChangeTotal = 220
    private val DOT_STATUS_BIG = 0X101
    private val DOT_STATUS_SMALL = 0X102
    private var mDotChangeStatus = DOT_STATUS_BIG

    fun setColor(mColor: Int) {
        this.mColor = mColor
        mNormalPaint.setColor(mColor)
    }

    fun setSelectedColor(mSelectedColor: Int) {
        this.mSelectedColor = mSelectedColor
        mSelectedPaint.setColor(mSelectedColor)
    }

    fun setDotTotalCount(mDotTotalCount: Int) {
        this.mDotTotalCount = mDotTotalCount
    }

    fun setDotRadius(mDotRadius: Int) {
        this.mDotRadius = mDotRadius
    }

    fun setRadiusChangeRate(mRadiusChangeRate: Float) {
        this.mRadiusChangeRate = mRadiusChangeRate
    }

    fun setDotMaxRadius(mDotMaxRadius: Int) {
        this.mDotMaxRadius = mDotMaxRadius
    }

    fun setDotSpacing(mDotSpacing: Int) {
        this.mDotSpacing = mDotSpacing
    }
    constructor(context: Context, @Nullable attrs: AttributeSet?):super(context,attrs){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotPollingView, 0, 0)
        initAttributes(typedArray)
        typedArray.recycle()
        init()
    }

    private fun initAttributes(Attributes: TypedArray) {
        mColor = Attributes.getColor(R.styleable.DotPollingView_dotP_dot_color, ContextCompat.getColor(context, R.color.colorPrimary))
        mSelectedColor = Attributes.getColor(R.styleable.DotPollingView_dotP_dot_selected_color, ContextCompat.getColor(context, R.color.colorAccent))
        mDotRadius = Attributes.getDimensionPixelSize(R.styleable.DotPollingView_dotP_dot_radius, DensityUtils.dp2px(context, 3f))
        mDotMaxRadius = Attributes.getDimensionPixelSize(R.styleable.DotPollingView_dotP_dot_max_radius, DensityUtils.dp2px(context, 5f))
        mDotSpacing = Attributes.getDimensionPixelSize(R.styleable.DotPollingView_dotP_dot_spacing, DensityUtils.dp2px(context, 6f))
        mDotTotalCount = Attributes.getInteger(R.styleable.DotPollingView_dotP_dot_count, 3)
        mRadiusChangeRate = Attributes.getFloat(R.styleable.DotPollingView_dotP_dot_size_change_rate, 0.3f)
    }

    /**
     * 初始化
     */
    private fun init() {
        mDotCurrentRadiusChange = 0f
        mSelectedPaint.setColor(mSelectedColor)
        mSelectedPaint.setAntiAlias(true)
        mSelectedPaint.setStyle(Paint.Style.FILL)
        mNormalPaint.setColor(mColor)
        mNormalPaint.setAntiAlias(true)
        mNormalPaint.setStyle(Paint.Style.FILL)
//        setupAnimators()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //测量宽高
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        var width: Int
        var height: Int

//        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = widthSize
            Log.e(TAG, "onMeasure MeasureSpec.EXACTLY widthSize=" + widthSize)
//        } else {
//            //指定最小宽度所有圆点加上间距的宽度, 以最小半径加上间距算总和再加上最左边和最右边变大后的距离
//            width = mDotTotalCount * mDotRadius * 2 + (mDotTotalCount - 1) * mDotSpacing + (mDotMaxRadius - mDotRadius) * 2
//            Log.e(TAG, "onMeasure no MeasureSpec.EXACTLY widthSize=$widthSize width=$width")
//            if (widthMode == View.MeasureSpec.AT_MOST) {
//                width = Math.min(width, widthSize)
//                Log.e(TAG, "onMeasure MeasureSpec.AT_MOST width=" + width)
//            }
//
//        }


            height = heightSize
            Log.e(TAG, "onMeasure MeasureSpec.EXACTLY heightSize=" + heightSize)

//        else {
//            height = mDotMaxRadius * 2
//            Log.e(TAG, "onMeasure no MeasureSpec.EXACTLY heightSize=$heightSize height=$height")
//            if (heightMode == View.MeasureSpec.AT_MOST) {
//                height = Math.min(height, heightSize)
//                Log.e(TAG, "onMeasure MeasureSpec.AT_MOST height=" + height)
//            }
//
//        }
        setMeasuredDimension(width, height)
    }
    private fun setupAnimators() {
        // 设置金钱的弹跳范围
        mValueAnimator = ValueAnimator.ofFloat(coinX, 1f,2f,3f)
        mValueAnimator.addUpdateListener { animation ->
            // 监听改变，动态更新
            val value = animation.animatedValue as Float
            coinX = APPSystemUtils.dp2px(context,value)
            requestLayout()
            invalidate()
        }
        // 设置金钱的速度
        mValueAnimator.duration = 800
        // 设置拦截器及重复方式
        mValueAnimator.interpolator = LinearInterpolator()
        mValueAnimator.repeatCount = ValueAnimator.INFINITE
        mValueAnimator.repeatMode = ValueAnimator.RESTART
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mNormalPaint.setAlpha(255)
        mSelectedPaint.setAlpha(255)

        if (mDotChangeStatus == DOT_STATUS_BIG) {
            mDotCurrentRadiusChange += mRadiusChangeRate
            mAlphaChange += 12
        } else {
            mDotCurrentRadiusChange -= mRadiusChangeRate
            mAlphaChange -= 12
        }

        if (mAlphaChange >= mAlphaChangeTotal) {
            mAlphaChange = mAlphaChangeTotal
        }
//        Log.e("DotPollingView", "dot current radius change: " + mDotCurrentRadiusChange)
        //第一个圆点的圆心x坐标计算：控件宽度的一半减去(所有圆点直径的和以及所有间距的和相加的总和的一半)再加上一个半径大小
        // ，为什么要加上半径？因为我们起点要的是圆心，但算出来的是最左边x坐标
        val startPointX = width / 2 - (mDotTotalCount * mDotRadius * 2 + (mDotTotalCount - 1) * mDotSpacing) / 2 + mDotRadius
        //所有圆点的圆心y坐标一致控件高度的一半
        val startPointY = height / 2
        for (i in 0 until mDotTotalCount) {
            Log.i("lqlqlqlqbqnj",i.toString()+"/"+mCurrentDot.toString())
            if (mCurrentDot == i) {//当前圆点
                mSelectedPaint.setAlpha(255 - mAlphaChange)
                mSelectedPaint.color=context.resources.getColor(R.color.two_point)
                canvas.drawCircle((startPointX + mCurrentDot * (mDotRadius * 2 + mDotSpacing)).toFloat(), startPointY.toFloat(), mDotRadius .toFloat(), mSelectedPaint)
                continue
            } else if (mCurrentDot > 1 && mCurrentDot - 2 == i)
            {
                mNormalPaint.setAlpha(255 - mAlphaChange)
                mNormalPaint.color=context.resources.getColor(R.color.three_point)

                canvas.drawCircle((startPointX + (mCurrentDot - 2) * (mDotRadius * 2 + mDotSpacing)).toFloat(), startPointY.toFloat(), mDotRadius.toFloat(), mNormalPaint)
                continue
            }
            //画正常的圆点
            mNormalPaint.setAlpha(255)
            mNormalPaint.color=context.resources.getColor(R.color.three_point)

            canvas.drawCircle((startPointX + i * (mDotRadius * 2 + mDotSpacing)).toFloat(), startPointY.toFloat(), mDotRadius.toFloat(), mNormalPaint)
        }

        //当圆点变化率达到最大或超过最大半径和正常半径之差时 变化率重置0，当前变大圆点移至下一圆点
        if (mDotCurrentRadiusChange >= mDotMaxRadius - mDotRadius && mDotChangeStatus == DOT_STATUS_BIG) {
            mDotCurrentRadiusChange = (mDotMaxRadius - mDotRadius).toFloat()
            mDotChangeStatus = DOT_STATUS_SMALL
        } else if (mDotCurrentRadiusChange <= 0 && mDotChangeStatus == DOT_STATUS_SMALL) {
            mDotChangeStatus = DOT_STATUS_BIG
            mDotCurrentRadiusChange = 0f
            mCurrentDot = if (mCurrentDot == mDotTotalCount - 1) 0 else mCurrentDot + 1
            mAlphaChange = 0
        }

        invalidate()

    }
}
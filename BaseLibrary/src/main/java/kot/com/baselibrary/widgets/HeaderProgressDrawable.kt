package kot.com.baselibrary.widgets

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.animation.LinearInterpolator
import kot.com.baselibrary.R
import kot.com.baselibrary.utils.APPSystemUtils


/**
 * 下拉刷新动画
 *
 * @auth wxf on 2018/5/22.
 */
class HeaderProgressDrawable : Drawable, Animatable {
    /** 等待动画持续时间(每次) */
    private val waitAnimationDuration = 1500L

    // 上下文
    private var context : Context
    // 画笔
    private lateinit var mPaint : Paint
    // 画笔(匀速的弧线)
    private lateinit var uniformPaint : Paint
    // Y轴的值
    private var coinY : Float = 0f
    // X轴的值
    private var coinX : Float = 0f
    // 是否在运行
    private var isRun = false

    // 等待动画
    private lateinit var animationSet : AnimatorSet
    // 动画(匀速)
    private lateinit var uniformAnimator: ValueAnimator
    // 动画(加速)
    private lateinit var variableAnimator: ValueAnimator

    // 结束动画
    private lateinit var endAnimationSet : AnimatorSet
    // 结束动画(对号左半部分)
    private lateinit var endLeftAnimator : ValueAnimator
    // 结束动画(对号右半部分)
    private lateinit var endRightAnimator : ValueAnimator

    /** 半径 */
    var radius = 0f
    /** 匀速开始时的位置 */
    private var startUniform = 0f
    /** 匀速的度数(固定不变) */
    private val angleUniform = 20f
    /** 加速位置(开始时跟随匀速) */
    private var startVariable = 0f
    /** 加速的度数 */
    private var angleVariable = 0f
    /** 左侧对号值 */
    private var endLeftValue = 0f
    /** 右侧对号值 */
    private var endRightValue = 0f

    /**
     * 构造器
     */
    constructor(context : Context){
        this.context = context

        // 设置圆半径
        radius = APPSystemUtils.dp2px(context,15f)
        // 初始化画笔
        initPaint()
        // 设置动画
        initAnimators()
    }

    /**
     * 初始化画笔
     */
    private fun initPaint(){
        // 普通圆圈画笔
        mPaint = Paint()
        mPaint.color = ContextCompat.getColor(context, R.color.header_progress)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = APPSystemUtils.dp2px(context,2f)

        // 圆圈上匀速移动的弧度画笔(透明)
        uniformPaint = Paint()
        uniformPaint.color = ContextCompat.getColor(context,R.color.header_progress_transparent)
        uniformPaint.isAntiAlias = true
        uniformPaint.style = Paint.Style.STROKE
        uniformPaint.strokeWidth = APPSystemUtils.dp2px(context,2f)
    }

    /**
     * 设置画圆的位置
     */
    fun setCoinYValue(coinY : Float){
        // 设置位置
        this.coinY = if(coinY > 360){ 360f } else if(coinY < 0) { 0f} else {coinY }
        // 刷新
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        // 设置大小
        val x = bounds.bottom.toFloat()/2
        val y = bounds.right.toFloat()/2
        // 设置矩形位置
        val oval = RectF(x-radius,y-radius,
                x+radius, y+radius)

        // 判断是否在运行动画
        if(!isRun){
            // 绘制下拉效果
            canvas.drawArc(oval,270f,coinY,false,mPaint);
        } else {
            // 绘制匀速
            canvas.drawArc(oval,startUniform,angleUniform,false,uniformPaint);
            // 绘制加速
            canvas.drawArc(oval,startVariable,angleVariable,false,mPaint);

            // 绘制对号
            canvas.drawLine(x - radius / 2, y,
                    x - radius / 2 + endLeftValue, y + endLeftValue, mPaint);
            canvas.drawLine(x - mPaint.strokeWidth/2 , y + radius / 2,
                    x  + 1.1f * endRightValue  - mPaint.strokeWidth/2, y + radius / 2 - 1.7f * endRightValue, mPaint);
        }
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun start() {
        // 开启动画
        if (!isRunning) {
            isRun = true
            coinX = 0f
            endLeftValue = 0f
            endRightValue = 0f
            animationSet.start()
        }
    }

    override fun stop() {
        if (isRunning) {
            animationSet.cancel()
        }
    }

    /**
     * 延时设置动画状态
     */
    fun stop(time : Int){
        // 播放结束动画
        stop()
        // 延时设置不显示动画的View
        Handler().postDelayed({
            isRun = false
        },time.toLong())

    }
    interface CallBackForFinshRefresh {
        fun finshRefresh()
    }


    private var mCallBack: CallBackForFinshRefresh? = null

    fun setCallBacks(callBack: CallBackForFinshRefresh) {
        mCallBack = callBack
    }
    override fun isRunning(): Boolean {
        // 是否已经运行
        return animationSet.isRunning
    }

    /**
     * 初始化动画
     */
    private fun initAnimators(){
        // 初始化等待动画
        initWaitAnimators()
        // 初始化结束动画
        initEndAnimators()
    }

    /**
     * 设置动画
     */
    private fun initWaitAnimators() {
        // 初始化两组动画
        initUniformAnimator()
        initArcAnimator()

        // 设置组合动画
        animationSet = AnimatorSet();
        // 持续时间
        animationSet.duration = waitAnimationDuration
        // 设置一起播放
        animationSet.play(uniformAnimator).with(variableAnimator);
        // 添加监听
        animationSet.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
                angleVariable = 360f
                endAnimationSet.start()
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }

    /**
     * 初始化匀速动画
     */
    private fun initUniformAnimator(){
        uniformAnimator = ValueAnimator.ofFloat(-90f,270f)
        uniformAnimator.addUpdateListener { animation ->
            // 监听改变，动态更新位置
            startUniform = animation.animatedValue as Float
            invalidateSelf()
        }

        // 设置拦截器及重复方式
        uniformAnimator.interpolator = LinearInterpolator()
        uniformAnimator.repeatCount = ValueAnimator.INFINITE
        uniformAnimator.repeatMode = ValueAnimator.RESTART
    }

    /**
     * 初始化加速动画
     */
    private fun initArcAnimator(){
        variableAnimator = ValueAnimator.ofFloat(360f,1080f)
        variableAnimator.addUpdateListener { animation ->
            // 获取度数
            val angle = animation.animatedValue as Float
            if(angle <= 720f){
                // 360以后，保持度数和匀速的开始重合，startVariable逐渐加大与匀速+度数后的位置
                startVariable = startUniform + angleUniform + (angle - 360)
                angleVariable = 720f - angle
            } else {
                // 360°以下,开始的位置和匀速的一样，只是度数自己变化
                startVariable = startUniform + angleUniform
                angleVariable = angle - 720
            }
        }

        // 设置拦截器及重复方式
        variableAnimator.interpolator = FastOutSlowInInterpolator()
        variableAnimator.repeatCount = ValueAnimator.INFINITE
        variableAnimator.repeatMode = ValueAnimator.RESTART
    }


    /**
     * 设置动画
     */
    private fun initEndAnimators() {
        // 初始化两组动画
        initEndLeftAnimator()
        initEndRightAnimator()

        // 设置组合动画
        endAnimationSet = AnimatorSet();
        // 设置一起播放
        endAnimationSet.play(endLeftAnimator).before(endRightAnimator);
        // 添加监听
        endAnimationSet.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                // 设置结束时的位置
                endLeftValue = radius / 2f
                endRightValue = radius / 2f

                Handler().postDelayed({
                    if(mCallBack!=null){
                        mCallBack!!.finshRefresh()
                    }
                },100.toLong())

            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }

    /**
     * 初始化左侧对号动画
     */
    private fun initEndLeftAnimator(){
        endLeftAnimator = ValueAnimator.ofFloat(0f, radius / 2f)
        endLeftAnimator.addUpdateListener { animation ->
            // 监听改变，动态更新位置
            endLeftValue = animation.animatedValue as Float
            invalidateSelf()
        }

        // 设置拦截器及重复方式
        endLeftAnimator.interpolator = LinearInterpolator()
        endLeftAnimator.duration = 100
    }

    /**
     * 初始化右侧对号动画
     */
    private fun initEndRightAnimator(){
        endRightAnimator = ValueAnimator.ofFloat(0f, radius / 2f)
        endRightAnimator.addUpdateListener { animation ->
            // 获取度数
            endRightValue = animation.animatedValue as Float
            invalidateSelf()
        }

        // 设置拦截器及重复方式
        endRightAnimator.interpolator = LinearInterpolator()
        endRightAnimator.duration = 200
    }
}

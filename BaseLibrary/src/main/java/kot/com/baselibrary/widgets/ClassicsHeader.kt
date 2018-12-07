package com.reitsfin.base.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.ColorInt
import android.support.v4.app.FragmentActivity
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.R
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.util.DensityUtil
import kot.com.baselibrary.widgets.HeaderProgressDrawable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yixiao on 2018/4/16.
 */
class ClassicsHeader : RelativeLayout, RefreshHeader {
    var REFRESH_HEADER_PULLDOWN = "下拉可以刷新"
    var REFRESH_HEADER_REFRESHING = "正在刷新数据中..."
    var REFRESH_HEADER_LOADING = "正在加载..."
    var REFRESH_HEADER_RELEASE = "松开立即刷新"

    //效果结束
    var KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME"

    lateinit var mLastTime: Date
    lateinit var mTitleText: TextView
    lateinit var mLastUpdateText: TextView
    lateinit var mProgressView: ImageView
    var mShared: SharedPreferences? = null
    var mRefreshKernel: RefreshKernel? = null
    var mProgressDrawable: HeaderProgressDrawable? = null
    var mSpinnerStyle = SpinnerStyle.Translate
    var mFinishDuration = 500
    var mBackgroundColor: Int = 0
    var mPaddingTop = 0
    var mPaddingBottom = 20
    var mEnableLastTime = true
    val padding = 0f

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    @SuppressLint("RestrictedApi")
    private fun initView(context: Context, attrs: AttributeSet?) {
        val density = DensityUtil()

        val layout = LinearLayout(context)
        layout.id = android.R.id.widget_frame
        layout.gravity = Gravity.CENTER_HORIZONTAL
        layout.orientation = LinearLayout.VERTICAL
        mTitleText = TextView(context)
        mTitleText.text = REFRESH_HEADER_PULLDOWN


        mLastUpdateText = TextView(context)

        val lpHeaderText = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
//        layout.addView(mTitleText, lpHeaderText)
        val lpUpdateText = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
//        layout.addView(mLastUpdateText, lpUpdateText)
        val lpProgress = LinearLayout.LayoutParams(density.dip2px(80f), density.dip2px(80f))
        mProgressView = ImageView(context)
        layout.addView(mProgressView,lpProgress)

        val lpHeaderLayout = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lpHeaderLayout.addRule(RelativeLayout.CENTER_IN_PARENT)
        addView(layout, lpHeaderLayout)

//        val lpProgress = RelativeLayout.LayoutParams(WRAP_CONTENT, density.dip2px(80f))
//        lpProgress.addRule(RelativeLayout.CENTER_IN_PARENT)
//        mProgressView = ImageView(context)
//        addView(mProgressView, lpProgress)

        if (isInEditMode) {
            mTitleText.text = REFRESH_HEADER_REFRESHING
        }

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader)

        lpUpdateText.topMargin = ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextTimeMarginTop, density.dip2px(0f))
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, density.dip2px(0f))

        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.width)
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.height)

        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.width)
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.height)

        mFinishDuration = ta.getInt(R.styleable.ClassicsHeader_srlFinishDuration, mFinishDuration)
        mEnableLastTime = ta.getBoolean(R.styleable.ClassicsHeader_srlEnableLastTime, mEnableLastTime)
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.ClassicsHeader_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)]

        mLastUpdateText.visibility = if (mEnableLastTime) View.VISIBLE else View.GONE

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableProgress))
        } else {
            mProgressDrawable = HeaderProgressDrawable(context)
            mProgressView.setImageDrawable(mProgressDrawable)
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTitle, DensityUtil.dp2px(11f)).toFloat())
        } else {
            mTitleText.textSize = 11f
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTime)) {
            mLastUpdateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTime, DensityUtil.dp2px(11f)).toFloat())
        } else {
            mLastUpdateText.textSize = 11f
        }

        val primaryColor = ta.getColor(R.styleable.ClassicsHeader_srlPrimaryColor, 0)
        val accentColor = ta.getColor(R.styleable.ClassicsHeader_srlAccentColor, 0)
        if (primaryColor != 0) {
            if (accentColor != 0) {
                setPrimaryColors(primaryColor, accentColor)
            } else {
                setPrimaryColors(primaryColor)
            }
        } else if (accentColor != 0) {
            setPrimaryColors(0, accentColor)
        }

        ta.recycle()

        if (paddingTop == 0) {
            if (paddingBottom == 0) {
                mPaddingTop = density.dip2px(padding)
                mPaddingBottom = density.dip2px(padding)
                setPadding(paddingLeft, density.dip2px(padding), paddingRight, density.dip2px(padding))
            } else {
                mPaddingTop = density.dip2px(padding)
                mPaddingBottom = paddingBottom
                setPadding(paddingLeft, density.dip2px(padding), paddingRight, paddingBottom)
            }
        } else {
            if (paddingBottom == 0) {
                mPaddingTop = paddingTop
                mPaddingBottom = density.dip2px(padding)
                setPadding(paddingLeft, paddingTop, paddingRight, density.dip2px(padding))
            } else {
                mPaddingTop = paddingTop
                mPaddingBottom = paddingBottom
            }
        }

        try {//try 不能删除-否则会出现兼容性问题
            if (context is FragmentActivity) {
                val manager = context.supportFragmentManager
                if (manager != null) {
                    val fragments = manager.fragments
                    if (fragments != null && fragments.size > 0) {
                        setLastUpdateTime(Date())
                        return
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }


        KEY_LAST_UPDATE_TIME += context.javaClass.name

        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE)
        val times = mShared!!.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())
        setLastUpdateTime(Date(times))

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
            setPadding(paddingLeft, 0, paddingRight, 0)
        } else {
            setPadding(paddingLeft, mPaddingTop, paddingRight, mPaddingBottom)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, extendHeight: Int) {
        mRefreshKernel = kernel
        mRefreshKernel!!.requestDrawBackgoundForHeader(mBackgroundColor)
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun onPullingDown(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {
        // 设置下拉动作，硬币显示的部分。percent为百分比，后面那个值，根据360/percent，稍微调整后得出
        mProgressDrawable!!.setCoinYValue(percent * 250f)
    }

    override fun onReleasing(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {

    }

    override fun onStartAnimator(layout: RefreshLayout, headHeight: Int, extendHeight: Int) {
        if (mProgressDrawable != null) {
            mProgressDrawable!!.start()
        } else {
            val drawable = mProgressView.drawable
            if (drawable is Animatable) {
                (drawable as Animatable).start()
            }
        }
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        if (success) {
            setLastUpdateTime(Date())
        }

        if (mProgressDrawable != null) {
            mProgressDrawable!!.stop(mFinishDuration)
        } else {
            val drawable = mProgressView.drawable
            if (drawable is Animatable) {
                (drawable as Animatable).stop()
            }
        }
//        mCallBack!!.finshRefresh()
        return mFinishDuration//延迟再弹回

    }

    @Deprecated("")
    override fun setPrimaryColors(@ColorInt vararg colors: Int) {
        if (colors.size > 0) {
            if (background !is BitmapDrawable) {
                setPrimaryColor(colors[0])
            }
            if (colors.size > 1) {
                setAccentColor(colors[1])
            } else {
                setAccentColor(if (colors[0] == -0x1) -0x99999a else -0x1)
            }
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return mSpinnerStyle
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.None -> {
                mLastUpdateText.visibility = if (mEnableLastTime) View.VISIBLE else View.GONE
                mTitleText.text = REFRESH_HEADER_PULLDOWN
            }
            RefreshState.PullDownToRefresh -> {
                mTitleText.text = REFRESH_HEADER_PULLDOWN
            }
            RefreshState.Refreshing -> {
                mTitleText.text = REFRESH_HEADER_REFRESHING
            }
            RefreshState.ReleaseToRefresh -> {
                mTitleText.text = REFRESH_HEADER_RELEASE
            }
            RefreshState.Loading -> {
                mLastUpdateText.visibility = View.GONE
                mTitleText.text = REFRESH_HEADER_LOADING
                Logger.e("RefreshState.Loading")
            }
        }
    }

    fun setLastUpdateTime(time: Date): com.reitsfin.base.widgets.ClassicsHeader {
        mLastTime = time
        mLastUpdateText.text = SimpleDateFormat("最后更新：${com.kotlin.base.utils.DateUtils.getRefreshHeaderDate(time)}HH:mm", Locale.CHINA).format(time)
        if (mShared != null && !isInEditMode) {
            mShared!!.edit().putLong(KEY_LAST_UPDATE_TIME, time.time).apply()
        }
        return this
    }

    fun setPrimaryColor(@ColorInt primaryColor: Int): com.reitsfin.base.widgets.ClassicsHeader {
        setBackgroundColor(primaryColor)
        mBackgroundColor = primaryColor
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestDrawBackgoundForHeader(mBackgroundColor)
        }
        return this
    }

    fun setAccentColor(@ColorInt accentColor: Int): com.reitsfin.base.widgets.ClassicsHeader {
        mTitleText.setTextColor(accentColor)
        mLastUpdateText.setTextColor(accentColor and 0x00ffffff or -0x34000000)
        return this
    }
}

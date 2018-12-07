package kot.com.baselibrary.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.AbsListView
import android.widget.ScrollView


/**
 * Created by yixiao on 2018/7/12.
 */
class SuperEasyRefreshLayout: ViewGroup {

    private val LOG_TAG = SuperEasyRefreshLayout::class.java.simpleName

    /**活跃的手指id,可能是第一个手指，也可能是第二个手指。 */
    private var mActivePointerId = -1

    private var isEnablePullToLoadMore: Boolean = true
    private var isEnablePullToRefresh: Boolean = false
    private val LAYOUT_ATTRS = intArrayOf(android.R.attr.enabled)

    var mNotify: Boolean = false

    /**顶部刷新view */
    lateinit var mRefreshView: SuperEasyRefreshHeadView

    /**refresh的高度值 */
    private var mRefreshViewHeight: Int = 0

    /**加载更多的view */
    private var mFooterView: SuperEasyRefreshFootView? = null

    /**footView的高度值 */
    private var mFootViewHeight: Int = 0

    /**处于刷新时，list View距顶部的距离，单位时dp，只有一次赋值，可以理解为是一个常量。 */
    var mRefreshOffset: Int = 0

    /**当前刷新View的顶部坐标 。随时变化 */
    var mCurrentTargetOffsetTop: Int = 0

    /**只有一次赋值，等于刷新view的高度的负值 */
    protected var mOriginalOffsetTop: Int = 0

    /**在onInterceptTouchEvent方法中按下的Y坐标 */
    private var mInitialDownY: Float = 0.toFloat()

    /**这个是一个固定坐标，它的意义具体还不确定，打印值等于24 */
    private var mTouchSlop: Int

    /**Y方向上的一个坐标，mInitialMotionY = mInitialDownY + mTouchSlop */
    private var mInitialMotionY: Float = 0.toFloat()

    private var mTarget: View? = null // the target of the gesture
    var mListener: OnRefreshListener? = null
    var mLoadMoreListener: OnLoadMoreListener? = null
    var mRefreshing = false
    /**是否正在加载更多 */
    private var isLoadingMore: Boolean = false

    /**是否在拖动 */
    private var mIsBeingDragged: Boolean = false

    /**移动动画使用的差值器 */
    private var mDecelerateInterpolator: DecelerateInterpolator

    /**移动动画监听器，当松开手指，移动动画回到刷新位置，动画结束后调用刷新监听器。 */
    private val mRefreshListener = object : AnimationListener {
        override fun onAnimationStart(animation: Animation) {}

        override fun onAnimationRepeat(animation: Animation) {}

        @SuppressLint("NewApi")
        override fun onAnimationEnd(animation: Animation) {
            if (mRefreshing) {
                if (mNotify) {
                    if (mListener != null) {
                        mListener!!.onRefresh()
                    }
                }
                mCurrentTargetOffsetTop = mRefreshView.getTop()
            } else {
                reset()
            }
        }
    }


    /*
    * 重置，回到初始状态
    * */
    fun reset() {
        mRefreshView.hideProgressBar()
        isLoadingMore = false
        mRefreshView.clearAnimation()
        mRefreshView.visibility = View.GONE
        mRefreshView.setRefreshText("")
        moveToStart()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            reset()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }


    /**
     * Simple constructor to use when creating a SwipeRefreshLayout from code.
     *
     * @param context
     */


    /**
     * Constructor that is called when inflating SwipeRefreshLayout from XML.
     *
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet?):super(context, attrs){
        /**触发移动事件的最小距离，自定义View处理touch事件的时候，有的时候需要判断用户是否真的存在movie，
         * 系统提供了这样的方法。表示滑动的时候，手的移动要大于这个返回的距离值才开始移动控件。 */
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

        setWillNotDraw(false)
        mDecelerateInterpolator = DecelerateInterpolator(2f)
        this.isEnablePullToLoadMore=true
        this.isEnablePullToRefresh=false
        val a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS)
        isEnabled = a.getBoolean(0, true)
        a.recycle()
    }

    fun isEnablePullToLoadMore(): Boolean {
        return this.isEnablePullToLoadMore
    }

    fun setEnablePullToLoadMore(enable: Boolean) {
        this.isEnablePullToLoadMore = enable
    }

    fun isEnablePullToRefresh(): Boolean {
        return this.isEnablePullToRefresh
    }

    fun setEnablePullToRefresh(enable: Boolean) {
        this.isEnablePullToRefresh = enable
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        /**
         *  mTarget = getChildAt(0)//得到显示数据的子View可能当前需要滑动的子VIEW不是第零层 就会有问题
         */

        mTarget = getChildAt(0)//得到显示数据的View
        mRefreshView = SuperEasyRefreshHeadView(context)//刷新的headerview
        addView(mRefreshView)

        mFooterView = SuperEasyRefreshFootView(context)//加载更多的底部view
        addView(mFooterView)

        mRefreshViewHeight = mRefreshView.headViewHeight
        mFootViewHeight = mFooterView!!.footViewHeight

        mRefreshOffset = (mRefreshViewHeight * 1.5f).toInt()

        ViewCompat.setChildrenDrawingOrderEnabled(this, true)

        mCurrentTargetOffsetTop = -mRefreshViewHeight
        mOriginalOffsetTop = mCurrentTargetOffsetTop
        moveToStart()
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mTarget == null) {
            ensureTarget()
        }
        if (mTarget == null) {
            return
        }



        mTarget!!.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY))
        mRefreshView.measure(0, 0)
        mFooterView!!.measure(0, 0)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0) {
            return
        }
        if (mTarget == null) {
            ensureTarget()
        }
        if (mTarget == null) {
            return
        }

        val childPaddingLeft = paddingLeft
        val childPaddingTop = paddingTop
        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom

        val refreshViewTop = mCurrentTargetOffsetTop
        val targetTop = refreshViewTop + childPaddingTop + mRefreshView.getMeasuredHeight()
        val footerViewTop = targetTop + childHeight

        mTarget!!.layout(childPaddingLeft, targetTop, childPaddingLeft + childWidth, targetTop + childHeight)

        val refreshViewLeft = (width - mRefreshView.getMeasuredWidth()) / 2
        mRefreshView.layout(refreshViewLeft, refreshViewTop, refreshViewLeft + mRefreshView.getMeasuredWidth(), refreshViewTop + mRefreshView.getMeasuredHeight())
        val footViewLeft = (width - mFooterView!!.getMeasuredWidth()) / 2
        mFooterView!!.layout(footViewLeft, footerViewTop, footViewLeft + mFooterView!!.getMeasuredWidth(), footerViewTop + mFooterView!!.getMeasuredHeight())
    }

    /**
     * 判断view向上是否可以滑动
     */
    fun canChildScrollUp(): Boolean {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget is AbsListView) {
                val absListView = mTarget as AbsListView
                return absListView.childCount > 0 && (absListView.firstVisiblePosition > 0 || absListView.getChildAt(0)
                        .top < absListView.paddingTop)
            } else {
                return mTarget!!.getScrollY() > 0
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1)
        }
        return ViewCompat.canScrollVertically(mTarget!!, -1)
    }

    /**
     * 判断view向下是否可以滑动
     */
    fun canChildScrollDown(): Boolean {

        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget is AbsListView) {
                val absListView = mTarget as AbsListView

                return absListView.childCount > 0 && (absListView.firstVisiblePosition > 0 || absListView.getChildAt(0)
                        .top < absListView.paddingTop)
            } else {
                return mTarget!!.getScrollY() > 0
            }
        } else {
            var strs= ViewCompat.canScrollVertically(mTarget, 1)
            Log.i("ScrollVer",strs.toString())
            return ViewCompat.canScrollVertically(mTarget, 1)

        }
//        return ViewCompat.canScrollVertically(mTarget!!, 1)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()
        val action = ev.actionMasked
        val pointerIndex: Int

        if (!isEnabled || canChildScrollUp() && canChildScrollDown() || mRefreshing || isLoadingMore) {
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                moveToStart()
                mActivePointerId = ev.getPointerId(0)//得到第一个手指
                mIsBeingDragged = false

                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                mInitialDownY = ev.getY(pointerIndex)
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == -1) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.")
                    return false
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                val y = ev.getY(pointerIndex)
                var yDiff = y - mInitialDownY
                if (!canChildScrollUp()) {//若是顶部不能滑动，则yDiff是正值，直接与mTouchSlop做比较。

                }
                if (!isEnablePullToRefresh) {//若是底部不能滑动，则yDiff是负值，取反后与mTouchSlop做比较。
                    yDiff = -yDiff


                }
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop
                    mIsBeingDragged = true
                    return true
                }
            }

            MotionEvent.ACTION_POINTER_UP//有手指抬起
            -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP//手指全抬起
                , MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = -1
            }
        }

        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        val pointerIndex: Int
        if (!isEnabled || mRefreshing || isLoadingMore) {
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }

                val y = ev.getY(pointerIndex)
                var yDiff = y - mInitialDownY
                if (!canChildScrollUp()) {//若是顶部不能滑动，则yDiff是正值，直接与mTouchSlop做比较。
                    yDiff = yDiff
                }
                if (!isEnablePullToRefresh){//若是不能下拉，则yDiff是负值，取反后与mTouchSlop做比较。
                    yDiff = -yDiff
                }
//                if (!canChildScrollDown()) {//若是底部不能滑动，则yDiff是负值，取反后与mTouchSlop做比较。
//                    yDiff = -yDiff
//                }
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop
                    mIsBeingDragged = true

                }

                if (mIsBeingDragged) {
                    /**滑动的距离，向下滑动为正，向下滑动为负 */
                    val overscrollTop = (y - mInitialMotionY) * 0.5f
                    if (overscrollTop > 0 && !canChildScrollUp()) {
                        moveSpinner(overscrollTop)
                    } else if (overscrollTop < 0 ) {//当处于底部，且有滑动的趋势直接加载更多。
                        if(this.isEnablePullToLoadMore){
                            loadMore()
                        }else{
                            return false
                        }

                    } else {
                        return false
                    }
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointerIndex = ev.actionIndex
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.")
                    return false
                }
                mActivePointerId = ev.getPointerId(pointerIndex)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.")
                    return false
                }

                if (mIsBeingDragged) {
                    val y = ev.getY(pointerIndex)
                    val overscrollTop = (y - mInitialMotionY) * 0.5f
                    mIsBeingDragged = false
                    finishSpinner(overscrollTop)
                }
                mActivePointerId = -1
                return false
            }
            MotionEvent.ACTION_CANCEL -> return false
        }

        return true
    }

    /**第二个手指抬起 */
    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    @SuppressLint("NewApi")
    private fun moveSpinner(overscrollTop: Float) {
        val originalDragPercent = overscrollTop / mRefreshOffset

        val dragPercent = Math.min(1f, Math.abs(originalDragPercent))
        val extraOS = Math.abs(overscrollTop) - mRefreshOffset
        val slingshotDist = mRefreshOffset.toFloat()
        val tensionSlingshotPercent = Math.max(0f, Math.min(extraOS, slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - Math.pow(
                (tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = slingshotDist * tensionPercent * 2f

        val targetY = mOriginalOffsetTop + (slingshotDist * dragPercent + extraMove).toInt()
        // where 1.0f is a full circle
        if (mRefreshView.getVisibility() !== View.VISIBLE) {
            mRefreshView.setVisibility(View.VISIBLE)
        }

        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop)
        mRefreshView.setCoinYValue(overscrollTop+150f)
        if (overscrollTop > mRefreshOffset) {
            mRefreshView.setRefreshText("")
        } else {
            mRefreshView.setRefreshText("")
        }
    }

    /**手指抬起时的操作 */
    private fun finishSpinner(overscrollTop: Float) {
        if (overscrollTop > mRefreshOffset) {//滑动超过一定距离时刷新
            setRefreshing(true, true /* notify */)
        } else {//否则回到 初始位置
            mRefreshing = false
            animateOffsetFromToTarget(mCurrentTargetOffsetTop, mOriginalOffsetTop, null)
        }
    }


    /**
     * 上滑加载更多
     */
    private fun loadMore() {
        if (!isLoadingMore) {
            animateOffsetFromToTarget(mCurrentTargetOffsetTop, mCurrentTargetOffsetTop - mFootViewHeight, null)
            if (mLoadMoreListener != null) {
                mLoadMoreListener!!.onLoad()
                isLoadingMore = true
            }
        }
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    fun setRefreshing(refreshing: Boolean) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing
            val endTarget = mRefreshOffset + mOriginalOffsetTop
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop)
            mNotify = false
        } else {
            setRefreshing(refreshing, false /* notify */)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                mRefreshView.setRefreshText("")
                mRefreshView.showProgressBar()
                if (mListener != null) {
                    mListener!!.onRefresh()
                }



                val endTarget = mRefreshOffset - Math.abs(mOriginalOffsetTop)

//                mRefreshView.hideProgressBar(object : HeaderProgressDrawable.IProgressAnimatorListener{
//                    override fun onAnimatorEnd() {
//                        animateOffsetFromToTarget(mCurrentTargetOffsetTop, endTarget, mRefreshListener)
//
////                        animateOffsetFromToTarget(mCurrentTargetOffsetTop, mOriginalOffsetTop, null)
//                    }
//                })
            } else {

            }
        }
    }

    /**完成加载更多  */
    fun finishLoadMore() {
        isLoadingMore = false
        reset()
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    fun isRefreshing(): Boolean {
        return mRefreshing
    }

    private fun ensureTarget() {
        if (mTarget == null) {
            mTarget = getChildAt(0)
        }
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if (android.os.Build.VERSION.SDK_INT < 21 && mTarget is AbsListView || mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget!!)) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b)
        }
    }

    /**
     * 从x位置移动到y位置，并伴随动画监听器
     */
    private fun animateOffsetFromToTarget(fromPosition: Int, targetPosition: Int, listener: AnimationListener?) {
        val animateFromToTarget = AnimateFromToTarget(fromPosition, targetPosition)
        animateFromToTarget.duration = 200
        animateFromToTarget.interpolator = mDecelerateInterpolator
        if (listener != null) {
            animateFromToTarget.setAnimationListener(listener)
        }
        mRefreshView.clearAnimation()
        mRefreshView.startAnimation(animateFromToTarget)
    }

    /**
     * 移动到初始位置
     */
    fun moveToStart() {
        val offset = mOriginalOffsetTop - mRefreshView.getTop()
        setTargetOffsetTopAndBottom(offset)
    }

    /**
     * 移动某个view，移动的距离。此时移动的是mRefreshView，由于此时改变了mCurrentTargetOffsetTop的值，
     * 而且onMeasure方法和onLayout方法会执行，所以其他view也会移动
     */
    fun setTargetOffsetTopAndBottom(offset: Int) {
        mRefreshView.bringToFront()
        ViewCompat.offsetTopAndBottom(mRefreshView, offset)
        mCurrentTargetOffsetTop = mRefreshView.getTop()
    }

    /**
     * 移动动画
     */
    private inner class AnimateFromToTarget(var mFromPosition: Int, var mTargetPosition: Int) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            var targetTop = 0
            targetTop = mFromPosition + ((mTargetPosition - mFromPosition) * interpolatedTime).toInt()
            val offset = targetTop - mRefreshView.getTop()
            setTargetOffsetTopAndBottom(offset)
        }
    }

    /**
     * 下拉刷新监听器
     */
    interface OnRefreshListener {
        fun onRefresh()
    }

    /**
     * 设置下拉刷新监听器
     */
    fun setOnRefreshListener(listener: OnRefreshListener) {
        mListener = listener
    }

    /**
     * 加载更多监听器
     */
    interface OnLoadMoreListener {
        fun onLoad()
    }

    /**
     * 设置加载更多监听器
     */
    fun setOnLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        mLoadMoreListener = loadMoreListener
    }
}
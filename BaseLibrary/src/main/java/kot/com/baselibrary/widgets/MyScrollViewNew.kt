package kot.com.baselibrary.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView
import android.view.ViewConfiguration






/**
 * Created by yixiao on 2018/7/18.
 */
class MyScrollViewNew: ScrollView {
    private var downX: Int = 0
    private var downY: Int = 0
    private var mTouchSlop: Int = 0

    private var isTop = false//是不是滑动到了最低端 ；使用这个方法，解决了上拉加载的问题
    private var onScrollToBottom: OnScrollToBottomListener? = null
    fun isTop(): Boolean {
        return isTop
    }

    fun setTop(top: Boolean) {
        isTop = top
    }
    constructor(context: Context, attrs: AttributeSet):super(context, attrs){
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    }




    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean,
                                clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (scrollY != 0 && null != onScrollToBottom && isTop()) {
            onScrollToBottom!!.onScrollBottomListener(clampedY)
        }
    }

    fun setOnScrollToBottomLintener(listener: OnScrollToBottomListener) {
        onScrollToBottom = listener
    }

    interface OnScrollToBottomListener {
        fun onScrollBottomListener(isBottom: Boolean)
    }

    override fun  onInterceptTouchEvent(e: MotionEvent):Boolean {
        var action = e.getAction();
        when (action){
            MotionEvent.ACTION_DOWN->{
                setTop(false);
                downX =  e.getRawX().toInt();
                downY =  e.getRawY().toInt();
            }
            MotionEvent.ACTION_MOVE->{
                var moveY =  e.getRawY();

                /****判断是向下滑动，才设置为true****/
                if(downY-moveY>0){
                    setTop(true);
                }else{
                    setTop(false);
                }
                if (Math.abs(moveY - downY) > mTouchSlop) {
                    return true;
                }
            }
        }

        return super.onInterceptTouchEvent(e);
    }

}
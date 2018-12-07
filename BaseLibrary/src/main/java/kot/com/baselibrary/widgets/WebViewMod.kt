package kot.com.baselibrary.widgets

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.widget.EditText

/**
 * Created by yixiao on 2018/3/15.
 */
class WebViewMod @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr){
    var mFocusDistraction: EditText? = null

    init {
        // This lets the layout editor display the view.




        mFocusDistraction = EditText(context)
        mFocusDistraction!!.setBackgroundResource(android.R.color.transparent)
        this.addView(mFocusDistraction)
        mFocusDistraction!!.getLayoutParams().width = 1
        mFocusDistraction!!.getLayoutParams().height = 1
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        invalidate()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    public override fun overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int,
                                     scrollRangeX: Int, scrollRangeY: Int, maxOverScrollX: Int,
                                     maxOverScrollY: Int, isTouchEvent: Boolean): Boolean {
        return false
    }

    /**
     * 使WebView不可滚动
     */
    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(0, 0)
    }
}
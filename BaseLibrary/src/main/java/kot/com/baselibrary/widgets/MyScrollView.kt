package kot.com.baselibrary.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import android.util.DisplayMetrics
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import android.app.Activity
import android.view.Display
import android.view.View
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext




/**
 * Created by yixiao on 2018/5/31.
 */
class MyScrollView: ScrollView {
    private var listener: OnScrollListener? = null
    private var mContext: Context? = null
    constructor(context: Context, attrs: AttributeSet):super(context,attrs){
        this.mContext = context;
    }
    fun setOnScrollListener(listener: OnScrollListener) {
        this.listener = listener
    }


    //设置接口
    interface OnScrollListener {
        fun onScroll(scrollY: Int)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        try {
            val display = (mContext as Activity).windowManager.defaultDisplay
            val d = DisplayMetrics()
            display.getMetrics(d)
            // 设置控件最大高度不能超过屏幕高度的一半
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(d.heightPixels+10 , View.MeasureSpec.AT_MOST)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 重新计算控件的宽高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    //重写原生onScrollChanged方法，将参数传递给接口，由接口传递出去
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (listener != null) {

            //这里我只传了垂直滑动的距离
            listener!!.onScroll(t)
        }
    }
}
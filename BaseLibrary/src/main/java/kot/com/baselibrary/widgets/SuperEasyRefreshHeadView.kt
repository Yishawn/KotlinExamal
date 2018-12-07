package kot.com.baselibrary.widgets

import android.content.Context
import android.graphics.drawable.Animatable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kot.com.baselibrary.R


/**
 * Created by yixiao on 2018/7/12.
 */
class SuperEasyRefreshHeadView: LinearLayout {
    var progressBar: ImageView
    var headViewHeight: Int = 0
    var textView: TextView
    var mProgressDrawable: HeaderProgressDrawable? = null
    constructor(context: Context):super(context){
        val view = View.inflate(getContext(), R.layout.view_super_easy_refresh_head, null)
        textView = view.findViewById<TextView>(R.id.super_easy_refresh_text_view) as TextView
        progressBar = view.findViewById<ImageView>(R.id.super_easy_refresh_head_progress_bar) as ImageView
        mProgressDrawable = HeaderProgressDrawable(context)
        progressBar.setImageDrawable(mProgressDrawable)
        addView(view)
        hideProgressBar()
        val metrics = resources.displayMetrics

        headViewHeight = (40 * metrics.density).toInt()//注意高度的设置。
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(headViewHeight, View.MeasureSpec.EXACTLY))
    }

    /**
     * 设置刷新的文本
     */
    fun setRefreshText(text: String) {
        textView.text = text
    }

    /**
     * 设置画圆的位置
     */
    fun setCoinYValue(coinY : Float){
        mProgressDrawable!!.setCoinYValue(coinY)
    }

    /**
     * 隐藏ProgressBar
     */


    /**
     * 隐藏ProgressBar
     */
    fun hideProgressBar(){
        if (mProgressDrawable != null) {
            mProgressDrawable!!.stop()
        } else {
            val drawable = HeaderProgressDrawable(context)
            if (drawable is Animatable) {
                (drawable as Animatable).stop()
            }
        }
    }

    /**
     * 显示ProgressBar
     */
    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        if (mProgressDrawable != null) {
            mProgressDrawable!!.start()
        } else {
            val drawable = HeaderProgressDrawable(context)
            if (drawable is Animatable) {
                (drawable as Animatable).start()
            }
        }
    }
}
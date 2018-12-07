package kot.com.baselibrary.widgets

import android.content.Context
import android.widget.LinearLayout
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import kot.com.baselibrary.R


/**
 * Created by yixiao on 2018/7/12.
 */
class SuperEasyRefreshFootView : LinearLayout {
    var footViewHeight: Int = 0
    lateinit var textView: TextView
    constructor(context: Context):super(context){
        val view = View.inflate(getContext(), R.layout.view_super_easy_refresh_foot, null)
        textView = view.findViewById(R.id.super_easy_refresh_text_view)
        addView(view)
        val metrics = resources.displayMetrics
        footViewHeight = (50 * metrics.density).toInt()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(footViewHeight, View.MeasureSpec.EXACTLY))
    }
}
package kot.com.baselibrary.widgets

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.widget.TextView

/**
 * Created by yixiao on 2018/4/10.
 */
class MyTabView : AppCompatTextView {
    var index: Int = 0
    constructor(context: Context) : super(context) {

    }
    constructor(context: Context,index:Int) : super(context) {
        this.index = index
    }
}
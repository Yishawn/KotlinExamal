package kot.com.baselibrary.widgets
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kot.com.baselibrary.R
import kot.com.baselibrary.ext.onClick

import kotlinx.android.synthetic.main.layout_header_bar_blue.view.*

/**
 * Created by yixiao on 2018/4/10.
 */
class HeaderBarBlue @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){
    //是否显示"返回"图标
    private var isShowBack = true
    //Title文字
    var titleText:String? = null
    //右侧文字
    private var rightText:String? = null

    init {
        //获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar)

        isShowBack = typedArray.getBoolean(R.styleable.HeaderBar_isShowBack,true)

        titleText = typedArray.getString(R.styleable.HeaderBar_titleText)
        rightText = typedArray.getString(R.styleable.HeaderBar_rightText)

        initView()
        typedArray.recycle()
    }

    /*
        初始化视图
     */
    private fun initView() {
        View.inflate(context, R.layout.layout_header_bar_blue,this)

        mblueLeftIv.visibility = if (isShowBack) View.VISIBLE else View.GONE

        //标题不为空，设置值
        titleText?.let {
            mblueTitleTv.text = it
        }

        //右侧文字不为空，设置值
        rightText?.let {
            mblueRightTv.text = it
            mblueRightTv.visibility = View.VISIBLE
        }

        //返回图标默认实现（关闭Activity）
        mblueLeftIv.onClick {
            if (context is Activity){
                (context as Activity).finish()
            }
        }

    }

    fun setBgColor(color : Int){
        rl_main.setBackgroundColor(color)
    }

    /*
        获取左侧视图
     */
    fun getLeftView(): LinearLayout? {
        return mblueLeftIv
    }

    /*
        获取右侧视图
     */
    fun getRightView(): TextView {
        return mblueRightTv
    }fun getTitleView(): TextView {
        return mblueTitleTv
    }

    /*
        获取右侧文字
     */
    fun getRightText():String{
        return mblueRightTv.text.toString()
    }
}
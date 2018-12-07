package kot.com.baselibrary.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.view.MotionEvent
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Drawable
import android.view.View
import kot.com.baselibrary.R


/**
 * Created by yixiao on 2018/6/22.
 */
class OnlyButton: Button {
    private val TAG = "ButtonM"
    /**
     * 按钮的背景色
     */
    private var backColor = 0
    /**
     * 按钮被按下时的背景色
     */

    /**
     * 按钮的背景图片
     */
//    private var backGroundDrawable: Drawable? = null
    /**
     * 按钮被按下时显示的背景图片
     */
//    private var backGroundDrawablePress: Drawable? = null
    /**
     * 按钮文字的颜色
     */
    private var textcolor:  ColorStateList? = null
    /**
     * 按钮被按下时文字的颜色
     */
//    private var textColorPress: ColorStateList? = null
    private var gradientDrawable: GradientDrawable? = null
    /**
     * 是否设置圆角或者圆形等样式
     */
    private var fillet = false
    /**
     * 标示onTouch方法的返回值，用来解决onClick和onTouch冲突问题
     */
    private var isCost = true

    constructor( context: Context):super(context)
    constructor( context: Context,attrs: AttributeSet):super(context,attrs){
        init(context,attrs,0)
    }
    constructor( context:Context,  attrs:AttributeSet,  defStyle:Int):super(context,  attrs ,defStyle){
       init(context,attrs,defStyle)
    }

    private fun init(context:Context,  attrs:AttributeSet,  defStyle:Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.buttonM, defStyle, 0)
        if (a != null) {
            //设置背景色
            val colorList = a.getColorStateList(R.styleable.buttonM_backColor)
            if (colorList != null) {
                backColor = colorList.getColorForState(drawableState, 0)
                if (backColor !== 0) {
                    setBackgroundColor(backColor)
                }
            }
            //记录按钮被按下时的背景色
//            val colorListPress = a.getColorStateList(R.styleable.buttonM_backColorPress)
//            if (colorListPress != null) {
//                backColorPress = colorListPress.getColorForState(drawableState, 0)
//            }
            //设置背景图片，若backColor与backGroundDrawable同时存在，则backGroundDrawable将覆盖backColor
//            backGroundDrawable = a.getDrawable(R.styleable.buttonM_backGroundImage)
//            if (backGroundDrawable != null) {
//                setBackgroundDrawable(backGroundDrawable)
//            }
            //记录按钮被按下时的背景图片
//            backGroundDrawablePress = a.getDrawable(R.styleable.buttonM_backGroundImagePress)
            //设置文字的颜色
            textcolor = a.getColorStateList(R.styleable.buttonM_textColor)
            if (textcolor != null) {
                setTextColor(textcolor)
            }
            //记录按钮被按下时文字的颜色
//            textColorPress = a.getColorStateList(R.styleable.buttonM_textColorPress)
            //设置圆角或圆形等样式的背景色
            fillet = a.getBoolean(R.styleable.buttonM_fillet, false)
            if (fillet) {
                getGradientDrawable()
                if (backColor !== 0) {
                    gradientDrawable!!.setColor(backColor)
                    setBackgroundDrawable(gradientDrawable)
                }
            }
            //设置圆角矩形的角度，fillet为true时才生效
            val radius = a.getFloat(R.styleable.buttonM_radius, 0f)
            if (fillet && radius != 0f) {
                setRadius(radius)
            }
            //设置按钮形状，fillet为true时才生效
            val shape = a.getInteger(R.styleable.buttonM_shape, 0)
            if (fillet && shape != 0) {
                setShape(shape)
            }
            a.recycle()
        }
        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(arg0: View, event: MotionEvent): Boolean {
                //根据touch事件设置按下抬起的样式
                return setTouchStyle(event.action)
            }
        })
    }

    /**
     * 根据按下或者抬起来改变背景和文字样式
     * @param state
     * @return isCost
     * 为解决onTouch和onClick冲突的问题
     * 根据事件分发机制，如果onTouch返回true，则不响应onClick事件
     * 因此采用isCost标识位，当用户设置了onClickListener则onTouch返回false
     */
    private fun setTouchStyle(state: Int): Boolean {
        if (state == MotionEvent.ACTION_DOWN) {
//            if (backColorPress !== 0) {
//                if (fillet) {
//                    gradientDrawable!!.setColor(backColorPress)
//                    setBackgroundDrawable(gradientDrawable)
//                } else {
//                    setBackgroundColor(backColorPress)
//                }
//            }
//            if (backGroundDrawablePress != null) {
//                setBackgroundDrawable(backGroundDrawablePress)
//            }
//            if (textColorPress != null) {
//                setTextColor(textColorPress)
//            }
        }
        if (state == MotionEvent.ACTION_UP) {
            if (backColor !== 0) {
                if (fillet) {
                    gradientDrawable!!.setColor(backColor)
//                    setBackgroundDrawable(gradientDrawable)
                } else {
                    setBackgroundColor(backColor)
                }
            }
//            if (backGroundDrawable != null) {
//                setBackgroundDrawable(backGroundDrawable)
//            }
            if (textcolor != null) {
                setTextColor(textcolor)
            }
        }
        return isCost
    }

    /**
     * 重写setOnClickListener方法，解决onTouch和onClick冲突问题
     * @param l
     */
    override fun setOnClickListener(l: View.OnClickListener?) {
        super.setOnClickListener(l)
        isCost = false
    }

    /**
     * 设置按钮的背景色
     * @param backColor
     */
    fun setBackColor(backColor: Int) {
        this.backColor = backColor
        if (fillet) {
            gradientDrawable!!.setColor(backColor)
//            setBackgroundDrawable(gradientDrawable)
        } else {
            setBackgroundColor(backColor)
        }
    }

    /**
     * 设置按钮被按下时的背景色
     * @param backColorPress
     */
//    fun setBackColorPress(backColorPress: Int) {
//        this.backColorPress = backColorPress
//    }

    /**
     * 设置按钮的背景图片
     * @param backGroundDrawable
     */
//    fun setBackGroundDrawable(backGroundDrawable: Drawable) {
//        this.backGroundDrawable = backGroundDrawable
//        setBackgroundDrawable(backGroundDrawable)
//    }

    /**
     * 设置按钮被按下时的背景图片
     * @param backGroundDrawablePress
     */
//    fun setBackGroundDrawablePress(backGroundDrawablePress: Drawable) {
//        this.backGroundDrawablePress = backGroundDrawablePress
//    }

    /**
     * 设置文字的颜色
     * @param textColor
     */
    override fun setTextColor(textColor: Int) {
        if (textColor == 0) return
        this.textcolor = ColorStateList.valueOf(textColor)
        //此处应加super关键字，调用父类的setTextColor方法，否则会造成递归导致内存溢出
        super.setTextColor(this.textcolor)
    }

    /**
     * 设置按钮被按下时文字的颜色
     * @param textColorPress
     */
//    fun setTextColorPress(textColorPress: Int) {
//        if (textColorPress == 0) return
//        this.textColorPress = ColorStateList.valueOf(textColorPress)
//    }

    /**
     * 设置按钮是否设置圆角或者圆形等样式
     * @param fillet
     */
    fun setFillet(fillet: Boolean) {
        this.fillet = fillet
        getGradientDrawable()
    }

    /**
     * 设置圆角按钮的角度
     * @param radius
     */
    fun setRadius(radius: Float) {
        if (!fillet) return
        getGradientDrawable()
        gradientDrawable!!.setCornerRadius(radius)
//        setBackgroundDrawable(gradientDrawable)
    }

    /**
     * 设置按钮的形状
     * @param shape
     */
    fun setShape(shape: Int) {
        if (!fillet) return
        getGradientDrawable()
        gradientDrawable!!.setShape(shape)
//        setBackgroundDrawable(gradientDrawable)
    }

    private fun getGradientDrawable() {
        if (gradientDrawable == null) {
            gradientDrawable = GradientDrawable()
        }
    }

}
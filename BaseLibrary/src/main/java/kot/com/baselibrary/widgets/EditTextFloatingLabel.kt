package kot.com.baselibrary.widgets

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kotlin.base.widgets.VerifyButton
import kot.com.baselibrary.R
import kot.com.baselibrary.utils.APPSystemUtils

import kotlinx.android.synthetic.main.scan_invite.view.*
import kotlinx.android.synthetic.main.validate_content.view.*
import kotlinx.android.synthetic.main.view_edittext_float.view.*


/**
 *  浮动文字输入框
 *
 * @auth wxf on 2018/4/18.
 */
class EditTextFloatingLabel constructor(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {
    // 属性常量
    private val TYPE_NORMAL = 100;
    private val TYPE_NUM = 101;
    private val TYPE_PHONE = 102;
    private val TYPE_PWD = 103;
    private val TYPE_VALIDATE = 104;
    private val TYPE_INVITE = 105;

    // 初始化类型为普通
    private var etType = TYPE_NORMAL
    // hint显示
    private var hint: String? = ""
    // hintLabel显示
    private var hintLabel: String? = ""
    // label是否悬浮中
    private var labelFloating = false
    // 编辑框最大字符数
    private var etMaxLength: Int = -1
    // 密码是否显示
    private var pwdShow = false
    // 文本变化监听
    private var editTextListener: EditTextListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextFloatingLabel)
        initView(typedArray)
        typedArray.recycle()
    }

    /**
     * 初始化View
     */
    private fun initView(typedArray: TypedArray) {
        // 设置自定义属性
        View.inflate(context, R.layout.view_edittext_float, this)
        hint = typedArray.getString(R.styleable.EditTextFloatingLabel_et_hint)
        hintLabel = typedArray.getString(R.styleable.EditTextFloatingLabel_et_hint_label)
        etType = typedArray.getInt(R.styleable.EditTextFloatingLabel_et_type, TYPE_NORMAL)
        etMaxLength = typedArray.getInt(R.styleable.EditTextFloatingLabel_et_maxLength, -1)

        // 判断值
        if (null == hint) {
            hint = ""
        }
        if (null == hintLabel) {
            hintLabel = ""
        }

        // 设置最大文本数
        if (etMaxLength != -1) {
            et_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(etMaxLength))
        }

        // 赋值
        tv_hint.text = hint

        // 设置取消点击监听
        ll_cancel.setOnClickListener { et_text.setText("") }

        // 设置输入框属性
        setEditTextProperty()
    }

    /**
     * 设置输入框属性
     */
    private fun setEditTextProperty() {
        when (etType) {
            TYPE_NUM -> {
                // 数字模式
                et_text.inputType = EditorInfo.TYPE_CLASS_NUMBER
            }
            TYPE_PHONE -> {
                // 电话号码模式
                et_text.inputType = EditorInfo.TYPE_CLASS_NUMBER
            }
            TYPE_PWD -> {
                // 密码模式
                et_text.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                setPwdView()
            }
            TYPE_VALIDATE -> {
                // 验证框模式
                setValidateView()
            }
            TYPE_INVITE -> {
                // 邀请码模式
                setInvitationView()
            }
        }

        // 设置输入框监听
        et_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 根据文本 变化设置显示
                if (s.length > 0) {
                    setViewByEditText(true)
                } else {
                    setViewByEditText(false)
                }

                if (null != editTextListener) {
                    editTextListener!!.onTextChanged(s, start, before, count)
                }
            }
        })
    }

    /**
     * 根据EditText文本设置View显示
     */
    private fun setViewByEditText(hasText: Boolean) {
        // 状态发生变化，判断label
        if (!hintLabel!!.isEmpty()) {
            if (!hasText && labelFloating) {
                // 如果输入框清空，且label还是悬浮状态，开启动画
                startAnimator(false)
            } else if (hasText && !labelFloating) {
                // 如果输入框有值，且label不是悬浮状态，开启动画
                startAnimator(true)
            }
        }

        if (hasText) {
            // 有文本
            // 显示清空按钮
            if (ll_cancel.visibility != View.VISIBLE) {
                ll_cancel.visibility = View.VISIBLE
                // 如果是密码模式，跟清空布局保持一致，直接显示
                if (etType == TYPE_PWD) {
                    ll_pwd.visibility = View.VISIBLE
                }
            }

            // 判断label
            if (!hintLabel!!.isEmpty() && !labelFloating) {
                // 如果输入框有值，且label不是悬浮状态，开启动画
                startAnimator(true)
            }

        } else {
            // 没有文本
            // 隐藏清空
            if (ll_cancel.visibility != View.GONE) {
                ll_cancel.visibility = View.GONE
                // 如果是密码模式，跟清空布局保持一致，直接隐藏
                if (etType == TYPE_PWD) {
                    ll_pwd.visibility = View.GONE
                }
            }

            // 判断label
            if (!hintLabel!!.isEmpty() && labelFloating) {
                // 如果输入框有值，且label不是悬浮状态，开启动画
                startAnimator(false)
            }
        }
    }

    /**
     * 设置密码布局
     */
    private fun setPwdView() {
        // 设置点击监听事件
        ll_pwd.setOnClickListener {
            if (pwdShow) {
                // 如果当前是显示，则单击后隐藏密码
                pwdShow = false
                et_text.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_pwd.setImageResource(R.mipmap.floating_et_hide_pwd)
            } else {
                // 反之则显示密码
                pwdShow = true
                et_text.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv_pwd.setImageResource(R.mipmap.floating_et_show_pwd)
            }
            et_text.setSelection(et_text.text.length)
        }
    }

    /**
     * 设置邀请布局
     */
    private fun setInvitationView() {
        // 打开邀请布局
        if(vSScan != null){
            vSScan.inflate()
        }
    }

    /**
     * 设置验证布局
     */
    private fun setValidateView() {
        // 打开验证布局
        if (vSValidate != null) {
            vSValidate.inflate()
        }
    }

    /**
     * 开启动画
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun startAnimator(isFloating: Boolean) {
        // 移动的值
        val translationYStart = 1F;
        val translationYEnd = -APPSystemUtils.dp2px(context, 18f);
        val translationXStart = 1F;
        val translationXEnd = -APPSystemUtils.dp2px(context, 3F * hintLabel!!.length);
        val alphaStart = 0F
        val alphaEnd = 1F
        val scaleStart = 1F
        val scaleEnd = 0.6F

        // 平移
        val translationX: ObjectAnimator
        val translationY: ObjectAnimator
        // 透明度
        val alphaAnim: ObjectAnimator
        // 放大缩小
        val scaleX: ObjectAnimator
        val scaleY: ObjectAnimator
        // 背景色
        val colorAnim: ObjectAnimator

        // 设置动画
        if (!isFloating) {
            // 位置还原
            translationX = ObjectAnimator.ofFloat(tv_hint, "translationX", translationXEnd, translationXStart)
            translationY = ObjectAnimator.ofFloat(tv_hint, "translationY", translationYEnd, translationYStart)
            alphaAnim = ObjectAnimator.ofFloat(tv_hint, "alpha", alphaEnd, alphaEnd) // 还原时透明度不变
            scaleX = ObjectAnimator.ofFloat(tv_hint, "scaleX", scaleEnd, scaleStart)
            scaleY = ObjectAnimator.ofFloat(tv_hint, "scaleY", scaleEnd, scaleStart)
            colorAnim = ObjectAnimator.ofInt(tv_hint, "textColor", ContextCompat.getColor(context, R.color.floating_hint_label_text), ContextCompat.getColor(context, R.color.floating_hint_text))
        } else {
            // 位置悬浮
            translationX = ObjectAnimator.ofFloat(tv_hint, "translationX", translationXStart, translationXEnd)
            translationY = ObjectAnimator.ofFloat(tv_hint, "translationY", translationYStart, translationYEnd)
            alphaAnim = ObjectAnimator.ofFloat(tv_hint, "alpha", alphaStart, alphaEnd)
            scaleX = ObjectAnimator.ofFloat(tv_hint, "scaleX", scaleStart, scaleEnd)
            scaleY = ObjectAnimator.ofFloat(tv_hint, "scaleY", scaleStart, scaleEnd)
            colorAnim = ObjectAnimator.ofInt(tv_hint, "textColor", ContextCompat.getColor(context, R.color.floating_hint_text), ContextCompat.getColor(context, R.color.floating_hint_label_text))
        }
        colorAnim.setEvaluator(ArgbEvaluator()) // 渐变器

        // 组合设置
        val animatorSet = AnimatorSet()
        animatorSet.play(translationX).with(translationY).with(alphaAnim).with(scaleX).with(scaleY).with(colorAnim)
        animatorSet.duration = 200

        // 设置动画后是否悬浮label
        labelFloating = isFloating

        // 设置监听，主要监听label和hint字体变化
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                // 动作完毕后换字体
                if (null != hintLabel && hintLabel!!.isNotEmpty() && !labelFloating) {
                    tv_hint.text = hint
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                // 动作开始前换字体
                if (null != hintLabel && hintLabel!!.isNotEmpty() && labelFloating) {
                    tv_hint.text = hintLabel
                }
            }
        })

        // 开启动画
        post { animatorSet.start() }
    }

    /**
     * 获取输入文本
     */
    fun getText(): String {
        return et_text.text.toString()
    }

    /**
     * 获取验证按钮布局
     */
    fun getValidateButton(): VerifyButton {
        return get_code
    }

    /**
     * 获取邀请按钮布局
     */
    fun getInviteView(): LinearLayout {
        return ll_invite
    }

    /**
     * 设置邀请码文本
     */
    fun setInvitationText(text: String) {
        et_text.setText(text)
    }

    /**
     * 设置编辑框变化监听
     */
    fun setEditTextChange(listener: EditTextListener) {
        editTextListener = listener
    }

    /**
     * 编辑框监听
     */
    interface EditTextListener {
        fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
    }
}
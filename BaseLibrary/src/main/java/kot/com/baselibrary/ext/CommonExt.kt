package kot.com.baselibrary.ext

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.*
import com.kennyc.view.MultiStateView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kot.com.baselibrary.R
import kot.com.baselibrary.protocol.BaseResp
import kot.com.baselibrary.rx.BaseFunc
import kot.com.baselibrary.rx.BaseFuncBoolean
import kot.com.baselibrary.rx.BaseObservers
import kot.com.baselibrary.utils.GlideUtils
import kot.com.baselibrary.widgets.DefaultTextWatcher
import kot.com.baselibrary.widgets.EditTextFloatingLabel
import org.jetbrains.anko.find

/**
 *
 *
 * @auth dyx on 2018/12/4.
 */
//TODO 由于项目中很多地方会用到注册，所以写成扩展类的形式，传入处理事件的订阅者，统一处理和返回结果
fun <T> io.reactivex.Observable<T>.excute(subscriber: BaseObservers<T>, lifecycleProvider: LifecycleProvider<*>) {
    this // TODO 定义注册账号事件产生线程：io线程
            .observeOn(AndroidSchedulers.mainThread())
            .compose(lifecycleProvider.bindToLifecycle())
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())

            // TODO 注册账号事件消费线程：主线程
            .subscribe(subscriber)
}

/*
    扩展点击事件
 */
fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}

fun View.OnClickListener(method: () -> Unit) {
    this.setOnClickListener {
        method() }

}

/**
 * 验证密码
 */
fun validatePwd(pwd : String) : Boolean{
    // 正则表达式：6-16位，数字+字母
    val validate = Regex("^(?![0-9]+\$)(?![a-zA-Z]+\$)[0-9A-Za-z]{6,16}\$")
    return validate.matches(pwd)
}

//判断注册按钮是否可用，传入判断方法的到布尔
fun Button.enable(view: View, method: () -> Boolean) {
    val button = this
    if(view is EditText){
        view.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonEnable(button,method)
            }

        })
    }
    else if (view is CheckBox){
        view.setOnClickListener { setButtonEnable(button,method) }
    }
    else if (view is ImageView){
        view.setOnClickListener { setButtonEnable(button,method) }
    }
    else if(view is EditTextFloatingLabel){
        view.setEditTextChange(object : EditTextFloatingLabel.EditTextListener{
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setButtonEnable(button,method)
            }
        })
    }
}

/**
 * 按钮是否可用
 */
fun setButtonEnable(button : Button, method: () -> Boolean){
    button.isEnabled = method()
    var boolean = method()
    if (boolean) {
        button.isEnabled = true
        button.setBackgroundColor(button.resources.getColor(R.color.gold_textcolor))
        button.setTextColor(button.resources.getColor(R.color.white))
    } else {
        button.isEnabled = false
        button.setBackgroundColor(button.resources.getColor(R.color.gray_button))
        button.setTextColor(button.resources.getColor(R.color.graytext))
    }

}
fun Button.EnableWithImage(){}
//判断注册按钮是否可用，传入判断方法的到布尔
fun Button.enabletx(et: TextView, method: () -> Boolean) {
    val button = this
    et.addTextChangedListener(object : DefaultTextWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            button.isEnabled = method()
            if (method()) {
                button.isEnabled = true
                button.setBackgroundColor(resources.getColor(R.color.gold_textcolor))
                button.setTextColor(button.resources.getColor(R.color.white))
            } else {
                button.isEnabled = false
                button.setBackgroundColor(resources.getColor(R.color.gray_button))
                button.setTextColor(button.resources.getColor(R.color.graytext))
            }
        }

    })
}
/*
    扩展点击事件，参数为方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}

/*
    ImageView加载网络图片
 */
fun ImageView.loadUrl(url: String) {

    GlideUtils.loadUrlImage(context, url, this)
}

/*
    多状态视图开始加载
 */
fun MultiStateView.startLoading() {
    viewState = MultiStateView.VIEW_STATE_LOADING
    val loadingView = getView(MultiStateView.VIEW_STATE_LOADING)
    val animBackground = loadingView!!.find<View>(R.id.loading_anim_view).background
    (animBackground as AnimationDrawable).start()
}

/*
    扩展视图可见性
 */
fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.getVisible() : Boolean{
    return this.visibility == View.VISIBLE
}

fun <T> Observable<BaseResp<T>>.covert(): Observable<T> {
    return this.flatMap(BaseFunc())
}

fun <T> Observable<BaseResp<T>>.covertBollean(): Observable<Boolean> {
    return this.flatMap(BaseFuncBoolean())
}
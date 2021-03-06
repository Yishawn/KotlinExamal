package kot.com.baselibrary.rx

import com.orhanobut.logger.Logger
import kot.com.baselibrary.common.ResultCode
import kot.com.baselibrary.presenter.view.BaseView
import rx.Subscriber
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 *自定义的订阅者功能类似观察者
 *
 * @auth dyx on 2018/12/4.
 */
open class BaseSubscriber <T> (var mview:BaseView?): Subscriber<T>(){
    /** 出错情况是否需要弹出Toast */
    private var needToast = true
    /** 出错情况是否需要跳转到登陆页面 */
    private var redirectLogin = true

    /**
     * 构造器
     *
     * @param view
     *          BaseView
     * @param needToast
     *          出错情况是否需要弹出Toast
     */
    constructor(view: BaseView?, needToast : Boolean) : this(view) {
        this.mview = view
        this.needToast = needToast
    }

    /**
     * 构造器
     *
     * @param view
     *          BaseView
     * @param needToast
     *          出错情况是否需要弹出Toast
     * @param redirectLogin
     *          登录验证失败是否需要跳转到登陆页面
     */
    constructor(view:BaseView?,needToast : Boolean,redirectLogin:Boolean) : this(view) {
        this.mview = view
        this.needToast = needToast
        this.redirectLogin = redirectLogin
    }

    /**
     * 完成方法
     */
    override fun onCompleted() {
        mview!!.hideLoading()
    }

    override fun onNext(t: T) {}

    /**
     * 错误方法
     */
    override fun onError(e: Throwable?) {
        // 打印错误日志
        Logger.e(e.toString())
        // 隐藏加载
        mview!!.hideLoading()

        // 设置错误信息
        if (e is BaseException){
            // 定义的错误信息，直接返回
            mview!!.onError(e.msg,e.code,needToast,redirectLogin)
        }else if (e is ConnectException || e is SocketTimeoutException || e is SocketException){
            // 网络信息设置
            mview!!.onError("网络出了点问题，请重新试试", ResultCode.NET_ERROR,needToast,redirectLogin)
        }else{
            // 目前统一处理
            mview!!.onError("网络出了点问题，请重新试试",ResultCode.NET_ERROR,needToast,redirectLogin)
        }
    }
}
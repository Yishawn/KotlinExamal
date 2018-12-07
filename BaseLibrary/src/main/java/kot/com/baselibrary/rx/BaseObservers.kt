package kot.com.baselibrary.rx

import com.orhanobut.logger.Logger
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kot.com.baselibrary.common.ResultCode
import kot.com.baselibrary.presenter.view.BaseView
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 *自定义的观察者，当服务器数据返回之后通过调用这个观察者队列获取到有用的数据，并作出相应的处理，比如停止LOADING等
 *
 * @auth dyx on 2018/12/4.
 */
open class BaseObservers <T> (var mview: BaseView?): Observer<T> {
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


    override fun onError(e: Throwable) {
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

    override fun onComplete() {
        mview!!.hideLoading()
    }

    override fun onSubscribe(d: Disposable) {
    }





    override fun onNext(t: T) {}



}
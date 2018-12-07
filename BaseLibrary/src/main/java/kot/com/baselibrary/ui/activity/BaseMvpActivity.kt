package kot.com.baselibrary.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import kot.com.baselibrary.base.utils.ARouterUtils
import kot.com.baselibrary.common.BaseApplication
import kot.com.baselibrary.common.ResultCode
import kot.com.baselibrary.injection.component.ActivityComponent
import kot.com.baselibrary.injection.component.DaggerActivityComponent
import kot.com.baselibrary.injection.module.ActivityModule
import kot.com.baselibrary.injection.module.LifecycleProviderModule
import kot.com.baselibrary.presenter.BasePresenter
import kot.com.baselibrary.presenter.view.BaseView
import kot.com.baselibrary.toast.ToastUtils
import kot.com.baselibrary.utils.router.RouterPath
import kot.com.baselibrary.widgets.ProgressLoading
import kot.com.baselibrary.widgets.SuperEasyRefreshLayout
import javax.inject.Inject

/**
 *mvp结构用的基类activity，包含了网络异常返回
 *
 * @auth dyx on 2018/12/5.
 */
abstract class BaseMvpActivity <T : BasePresenter<*>> : BaseActivity(), BaseView {
    lateinit var activityCoponent: ActivityComponent
    lateinit var mProgressLoading: ProgressLoading


    override fun showLoading() {
        mProgressLoading.showLoading()
    }

    override fun hideLoading() {
        if (mProgressLoading.isShowing) {
            mProgressLoading.hideLoading()
        }
    }
    interface refreshorLoadMore{
        fun finishAction(string: String)
    }
    /**
     * 添加SWIP的刷新和加载更多的监听器
     */
    fun initListener(smartrefresh: SuperEasyRefreshLayout, method: () -> Unit, methods: () -> Unit) {

        smartrefresh.setOnRefreshListener(object: SuperEasyRefreshLayout.OnRefreshListener{
            override fun onRefresh() {

                method()

            }
        })


        smartrefresh.setOnLoadMoreListener(object: SuperEasyRefreshLayout.OnLoadMoreListener {
            override fun onLoad() {
                methods()
            }

        });
    }
    private var mCallBack: refreshorLoadMore? = null

    fun setCallBack(callBack: refreshorLoadMore) {
        mCallBack = callBack
    }

    fun doCallBackMethod() {
        val info = "这里CallBackUtils即将发送的数据。"
        mCallBack!!.finishAction(info)
    }
    override fun onDestroy() {
        if (mProgressLoading.isShowing) {
            mProgressLoading.dismiss()
        }
        super.onDestroy()
    }
    override fun onError(text: String, data: Int) {

    }

    /**
     * 添加SWIP的刷新和加载更多的监听器
     */
//    fun initListener(smartrefresh:SuperEasyRefreshLayout) {
//        smartrefresh.setOnRefreshListener(object: SuperEasyRefreshLayout.OnRefreshListener{
//            override fun onRefresh() {
//                Handler().postDelayed( object:Runnable {
//                    override fun run() {
//                        smartrefresh.setRefreshing(false)
//                        ToastUtils.centerToast(this,"刷新 成功")
//                    }
//
//
//                },2000)
//            }
//        })
//
//
//        smartrefresh.setOnLoadMoreListener(object: SuperEasyRefreshLayout.OnLoadMoreListener {
//            override fun onLoad() {
//                Handler().postDelayed( object:Runnable {
//                    override fun run() {
//                        smartrefresh.finishLoadMore();
//                        ToastUtils.centerToast(this,"加载更多成功")
//                    }
//
//
//                },2000)
//            }
//
//        });
//    }
    /**
     * 返回错误
     */
    override fun onError(text: String, data: Int, needToast: Boolean,redirectLogin:Boolean) {
        // 是否需要Toast提示
        if (needToast) {
            ToastUtils.centerToast(this, text)
        }
        // 是否需要跳转到登录页面
        if (data == ResultCode.AUTHENTICATION_ERROR && redirectLogin){
//            ARouter.getInstance()
//                    .build(RouterPath.UserCenter.PATH_LOGIN)
//                    .greenChannel()
//                    //.withTransition(R.anim.slide_right_in,R.anim.slide_right_out)
//                    .navigation()
            ARouterUtils.getInstance().startActivity(this, RouterPath.UserCenter.PATH_LOGIN)

        }
        // 继续回传
        onError(text, data)
    }

    @Inject
    lateinit var mPresenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initActivityInjection()
        injectComponent()
        //ARouter注册
        ARouter.getInstance().inject(this)
        mProgressLoading = ProgressLoading.create(this)

    }

    //为了防止遗忘注入
    abstract fun injectComponent()

    /*
        初始Activity Component
     */
    private fun initActivityInjection() {
        //因为baseapplication已经初始化过了ActivityComponent依赖于appComponent，所以拿到APPLICATION之后得到appComponent
        activityCoponent = DaggerActivityComponent.builder().appComponent((application as BaseApplication).appComponent).activityModule(ActivityModule(this)).lifecycleProviderModule(LifecycleProviderModule(this))
                .build()
    }


}
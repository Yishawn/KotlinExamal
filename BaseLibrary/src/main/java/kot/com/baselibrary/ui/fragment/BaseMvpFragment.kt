package kot.com.baselibrary.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.*
import android.widget.*
import kot.com.baselibrary.base.utils.ARouterUtils
import kot.com.baselibrary.common.BaseApplication
import kot.com.baselibrary.common.CommonMethod
import kot.com.baselibrary.common.ResultCode
import kot.com.baselibrary.injection.component.ActivityComponent
import kot.com.baselibrary.injection.component.DaggerActivityComponent
import kot.com.baselibrary.injection.module.ActivityModule
import kot.com.baselibrary.injection.module.LifecycleProviderModule
import kot.com.baselibrary.intfc.IPermissionsResultListener
import kot.com.baselibrary.presenter.BasePresenter
import kot.com.baselibrary.presenter.view.BaseView
import kot.com.baselibrary.toast.ToastUtils
import kot.com.baselibrary.utils.StatusBarCompat
import kot.com.baselibrary.utils.router.RouterPath
import kot.com.baselibrary.widgets.ProgressLoading
import kot.com.baselibrary.widgets.PubBtnDialog
import kot.com.baselibrary.widgets.SuperEasyRefreshLayout
import javax.inject.Inject

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
open abstract class BaseMvpFragment <T : BasePresenter<*>> :BaseFragment(), BaseView {
    lateinit var activityCoponent: ActivityComponent
    var presstime: Long = 0
    var activityList: MutableList<Activity> = ArrayList()
    lateinit var mListener: IPermissionsResultListener
    private var mRequestCode: Int = 0
    var mListPermissions: MutableList<String> = ArrayList()
    lateinit var mProgressLoading: ProgressLoading
    var isNeedApply=true
    @Inject
    lateinit var mPresenter :T
    lateinit var dialogs: Dialog
    lateinit var ok_btns: Button
    lateinit var cancle_btns: Button
    lateinit var content_texts: TextView
    // 公共类
    lateinit var mCommonMethod: CommonMethod
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mProgressLoading= ProgressLoading.create(requireActivity())
        mCommonMethod = CommonMethod()
        initActivityInjection()
        injectComponent()
        //        removstatubars()

        return super.onCreateView(inflater, container, savedInstanceState)
    }
    //为了防止遗忘注入
    abstract fun injectComponent()
    /*
        初始Activity Component
     */
    private fun initActivityInjection() {
        //因为baseapplication已经初始化过了ActivityComponent依赖于appComponent，所以拿到APPLICATION之后得到appComponent
        activityCoponent= DaggerActivityComponent.builder().appComponent((requireActivity().application as BaseApplication).appComponent).activityModule(ActivityModule(requireActivity())).lifecycleProviderModule(LifecycleProviderModule(this))
                .build()
    }

    /**
     * 是否重复点击
     */
    protected fun isDoubleClick(v: View): Boolean {
        return mCommonMethod.isDoubleClick(v)
    }
    interface refreshorLoadMore{
        fun finishAction(string: String)
    }

    private var mCallBack: refreshorLoadMore? = null

    fun setCallBack(callBack: refreshorLoadMore) {
        mCallBack = callBack
    }

    fun doCallBackMethod() {
        val info = "这里CallBackUtils即将发送的数据。"
        mCallBack!!.finishAction(info)
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
    /**
     * 两个按钮的对话框(左键为取消)
     */
    fun showTwoBtnCancelDialog(activity: Activity, content:String, ok_text:String, cancle_text:String, onOkClick: View.OnClickListener, color:Int) {
        dialogs = PubBtnDialog.Builder(activity, PubBtnDialog.DialogMode.TWO_BTN)
                .setCanceledOnTouchOutside(false)
                .setLeftBtnCancel(true) // 设置左键为取消键，点击会关闭dialog
                .setLeftBtnText(cancle_text)
                .setRightBtnText(ok_text)
                .setRightBtnTextColor(color)
                .setRightBtnOnclickListener(onOkClick)
                .setMessage(content)
                .createDialog()
        dialogs.show()
    }
    private fun removstatubars() {
        val window = requireActivity().window
        val mContentView = requireActivity().findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT) as ViewGroup
        //首先使 ChildView 不预留空间
        var mChildView: View? = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
        }
        val statusBarHeight = StatusBarCompat.getStatusBarHeight(requireActivity())
        //需要设置这个 flag 才能设置状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //避免多次调用该方法时,多次移除了 View
        if (mChildView != null && mChildView.layoutParams != null && mChildView.layoutParams.height == statusBarHeight) {
            //移除假的 View.
            mContentView.removeView(mChildView)
            mChildView = mContentView.getChildAt(0)
        }
        if (mChildView != null) {
            val lp = mChildView.layoutParams as FrameLayout.LayoutParams
            //清除 ChildView 的 marginTop 属性
            if (lp != null && lp.topMargin >= statusBarHeight) {
                lp.topMargin -= statusBarHeight
                mChildView.layoutParams = lp
            }
        }

    }
    override fun showLoading() {
        if(null != mProgressLoading){
            mProgressLoading.showLoading()
        }
    }

    override fun hideLoading() {
        if(null != mProgressLoading && mProgressLoading.isShowing){
            mProgressLoading.hideLoading()
        }
    }


    override fun onError(text: String,data:Int) {

    }

    /**
     * 返回错误
     */
    override fun onError(text: String, data: Int, needToast: Boolean,redirectLogin:Boolean) {
        // 是否需要Toast提示
        if(needToast){
            ToastUtils.centerToast(requireActivity(),text)
        }
        // 是否需要跳转到登录页面
        if (data == ResultCode.AUTHENTICATION_ERROR && redirectLogin){
//            ARouter.getInstance()
//                    .build(RouterPath.UserCenter.PATH_LOGIN)
//                    .greenChannel()
//                    //.withTransition(R.anim.slide_right_in,R.anim.slide_right_out)
//                    .navigation(activity)
            ARouterUtils.getInstance().startActivity(requireActivity(), RouterPath.UserCenter.PATH_LOGIN)
            requireActivity().finish()
        }
        onError(text,data)
    }
    fun checkPermissions(permissions: Array<out String>, requestCode: Int, listener: IPermissionsResultListener):Boolean {
        //权限不能为空
        if (permissions != null || permissions.size != 0) {
            mListener = listener
            mRequestCode = requestCode
            for (i in permissions) {
                if (isHavePermissions(i).not()) {
                    mListPermissions.add(i)

                }
            }
            if (mListPermissions.size==0){
                return true
            }


            //遍历完后申请
            applyPermissions()
        }
        return false
    }

    @SuppressLint("RestrictedApi")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments = childFragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }
    //判断权限是否申请
    fun  isHavePermissions( permissions:String):Boolean {
        if (ContextCompat.checkSelfPermission(requireActivity(), permissions) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    //申请权限
    fun applyPermissions() {
        if (!mListPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), mListPermissions.toTypedArray(), mRequestCode)
        }
    }
    fun changeHeaderView(header_view: View, color: Int){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            header_view.visibility = View.GONE
        } else {
            var result = 0
            val resourceId = requireContext().resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = requireContext().resources.getDimensionPixelSize(resourceId)
            }
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, result)
            header_view.layoutParams = params
        }
    }
    fun changeHeaderViewRe(header_view: View, color: Int){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            header_view.visibility = View.GONE
        } else {
            var result = 0
            val resourceId = requireContext().resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = requireContext().resources.getDimensionPixelSize(resourceId)
            }
            val params = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, result)
            header_view.layoutParams = params
        }
    }

}
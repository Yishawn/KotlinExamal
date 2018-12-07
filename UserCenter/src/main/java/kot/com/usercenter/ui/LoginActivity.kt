package kot.com.usercenter.ui

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.orhanobut.logger.Logger
import com.umeng.analytics.MobclickAgent
import kot.com.baselibrary.base.utils.ARouterUtils
import kot.com.baselibrary.common.BaseConstant
import kot.com.baselibrary.common.ProviderConstant
import kot.com.baselibrary.common.UserInfoManager
import kot.com.baselibrary.data.UserInfo
import kot.com.baselibrary.ext.enable
import kot.com.baselibrary.ext.onClick
import kot.com.baselibrary.ext.validatePwd
import kot.com.baselibrary.toast.ToastUtils
import kot.com.baselibrary.ui.activity.BaseMvpActivity
import kot.com.baselibrary.utils.StringUtil
import kot.com.baselibrary.utils.router.RouterPath
import kot.com.usercenter.R
import kot.com.usercenter.data.protocol.TokenInfo
import kot.com.usercenter.injection.component.DaggerUserComponent
import kot.com.usercenter.injection.module.UserModule
import kot.com.usercenter.presenter.LoginPresenter
import kot.com.usercenter.presenter.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseMvpActivity<LoginPresenter>(), LoginView, View.OnClickListener  {

    // 点击次数
    private val ARRAY_SIZE = 4
    // 设置数组
    private val mHits = LongArray(ARRAY_SIZE)

    override fun injectComponent() {
        DaggerUserComponent.builder().activityComponent(activityCoponent).userModule(UserModule()).build().inject(this)
        mPresenter.mView = this
    }

    override fun onBackPressed() {
        openMainActivity(true)
    }

    override fun onError(text: String, data: Int) {
//        ToastUtils.centerToast(this,text)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mLoginBtn -> {
                // 防止重复点击
                if (!isDoubleClick(view)) {
                    val phone = mMobileEt.getText()
                    val pwd = mPwdEt.getText()
                    //友盟埋点-登录按钮点击事件
                    MobclickAgent.onEvent(applicationContext, "1_login_action")
                    if (phone.isEmpty()) run { ToastUtils.centerToast(this, "请输入手机号") }
                    else if (!StringUtil.checkPhoneNum(phone)) run { ToastUtils.centerToast(this, "请输入正确手机号") }
                    else if (pwd.isEmpty()) run { ToastUtils.centerToast(this, "请输入密码") }
                    else if (!validatePwd(pwd)) run { ToastUtils.centerToast(this, "密码需为6-16位字母、数字组合密码") }
                    else {
                        val parms = mapOf(Pair("mobile", mMobileEt.getText()), Pair("password", mPwdEt.getText()))
                        mPresenter.login(parms)
                    }
                }
            }
            R.id.fast_regis -> {
                //友盟埋点-登录页面_立即注册按钮点击事件
                MobclickAgent.onEvent(applicationContext, "2_goto_register_action")
                ARouterUtils.getInstance().startActivity(this, RouterPath.UserCenter.PATH_REGISTER)
            }
            R.id.forget_pwd -> {
                //友盟埋点-登录页面_忘记密码按钮点击事件
                MobclickAgent.onEvent(applicationContext, "3_goto_find_psw")
                ARouterUtils.getInstance().startActivity(this,RouterPath.UserCenter.PATH_FORGETPWD)
            }
            R.id.ll_login_back_g -> {
                openMainActivity(true)
            }
            R.id.ll_icon -> {
//                // 测试类
                onTestClick()
            }
        }
    }


    private fun onTestClick() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1);
        mHits[mHits.size - 1] = SystemClock.uptimeMillis();
        var currentTime = SystemClock.uptimeMillis() - ARRAY_SIZE * 490
        if (mHits[0] >= currentTime) {
            ARouterUtils.getInstance().startActivity(this,RouterPath.UserCenter.PATH_TEST_SETTING)
            ToastUtils.centerToast(this, "开启隐藏关卡")
        } else {
            Logger.e("mHits[0] = " + mHits[0] + " , currentTime = $currentTime")
        }

    }
    var viewY: Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        initview()

    }

    private fun initview() {
        UserInfoManager.instance.logout()
        mLoginBtn.enable(mMobileEt, { isButtonEnable() })
        mLoginBtn.enable(mPwdEt, { isButtonEnable() })
        mLoginBtn.onClick(this)
        fast_regis.onClick(this)
        forget_pwd.onClick(this)
        ll_login_back_g.onClick(this)

        // 是否为debug模式
        if (BaseConstant.SHOW_TEST_MODE) {
            ll_icon.onClick(this)
        }
//        root_login.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//            //View代表方法传入的控件
//            val viewLocation = IntArray(2)
//            mLoginBtn.getLocationInWindow(viewLocation)
//            viewY =viewLocation[1]
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
//            Logger.e("y方向距离$viewY")
//            scrollview_login.scrollTo(0, viewY/3)
//        }


    }

    private fun isButtonEnable(): Boolean {
        return mMobileEt.getText().isEmpty().not() &&
                mPwdEt.getText().isEmpty().not()
    }

    /**
    finish()     * 返回服务器token
     */
    override fun onLoginResult(result: TokenInfo) {
        UserInfoManager.instance.setToken(result.value, result.expiredAt, result.id)
    }


    /**
     * 跳转到首页面，并关闭当前页面
     */
    private fun openMainActivity(isLogout: Boolean) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        val isOpen = imm.isActive()
        if (isOpen) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        var bundle=Bundle()
        bundle.putInt(ProviderConstant.AROUTER_NAME_FRAGMENT, ProviderConstant.AROUTER_VALUE_HOME)
        bundle.putBoolean(ProviderConstant.AROUTER_IS_LOGOUT, isLogout)
        ARouterUtils.getInstance().startActivityWithBundle(this,RouterPath.App.PATH_Main,bundle)
    }
}

package kot.com.usercenter.presenter

import kot.com.baselibrary.ext.excute
import kot.com.baselibrary.presenter.BasePresenter
import kot.com.baselibrary.rx.BaseObservers
import kot.com.usercenter.data.protocol.TokenInfo
import kot.com.usercenter.presenter.view.LoginView
import kot.com.usercenter.service.impl.UserServiceImpl
import javax.inject.Inject

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
class LoginPresenter @Inject constructor() : BasePresenter<LoginView>()
{
    @Inject
    lateinit var userService: UserServiceImpl
    fun login(map: Map<String,String>){
        mView.showLoading()
        userService.login(map)
                .excute(object : BaseObservers<TokenInfo>(mView) {
                    override fun onNext(t: TokenInfo) {
                        mView.onLoginResult(t)
                    }
                },lifecycleProvider)
    }


}
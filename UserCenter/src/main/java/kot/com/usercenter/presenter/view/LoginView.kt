package kot.com.usercenter.presenter.view

import kot.com.baselibrary.presenter.view.BaseView
import kot.com.usercenter.data.protocol.TokenInfo

/**
 *view层定义的接口提供activity回调
 *
 * @auth dyx on 2018/12/5.
 */
interface LoginView: BaseView {
    fun onLoginResult(result: TokenInfo)

}
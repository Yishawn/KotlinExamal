package kot.com.baselibrary.presenter

import android.content.Context
import com.trello.rxlifecycle2.LifecycleProvider
import kot.com.baselibrary.presenter.view.BaseView
import kot.com.baselibrary.utils.NetWorkUtils
import javax.inject.Inject

/**
 *
 *
 * @auth dyx on 2018/12/4.
 */
open class BasePresenter  <T: BaseView>{
    lateinit var mView:T

    //Dagger注入，Rx生命周期管理
    @Inject
    lateinit var lifecycleProvider: LifecycleProvider<*>
    @Inject
    lateinit var context: Context
    fun checkNetWork():Boolean{
        if (NetWorkUtils.isNetWorkAvailable(context).not()){
            return true
        }
        mView.onError("网络不可用",0)
        return false

    }
}
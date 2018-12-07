package kot.com.baselibrary.presenter.view

/**
 *
 *
 * @auth dyx on 2018/12/4.
 */
interface BaseView { fun showLoading()
    fun hideLoading()
    fun onError(text:String,data:Int)
    fun onError(text:String,data:Int,needToast:Boolean,redirectLogin:Boolean)
}
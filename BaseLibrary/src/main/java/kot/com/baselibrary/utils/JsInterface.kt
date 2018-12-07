package kot.com.baselibrary.utils

import android.content.Context
import android.webkit.JavascriptInterface

import kot.com.baselibrary.base.utils.ARouterUtils
import kot.com.baselibrary.toast.ToastUtils
import kot.com.baselibrary.utils.router.RouterPath

/**
 * Description: js调用
 */
class JsInterface(private val mContext: Context) {


    /**
     * 在js中调用window.AndroidWebView.showInfoFromJs(name)，便会触发此方法。
     * 此方法名称一定要和js中showInfoFromJava方法一样
     *
     * @param name
     */
    //例子1，下边两个方法是用来供h5页面来调用的测试方法
    @JavascriptInterface
    fun showInfoFromJs(name: Array<String>) {
        ToastUtils.centerToast(mContext,"来自js的信息:" + name[0])
    }

    @JavascriptInterface//网页如果发现没有登陆，会调用此方法，然后跳到登陆页面
    fun reLogin() {
        ARouterUtils.getInstance().startActivity(mContext, RouterPath.UserCenter.PATH_LOGIN)
    }

    @JavascriptInterface
    fun showInfoFromJs(): String {
        ToastUtils.centerToast(mContext, "JS调用App方法")
        return "abc"
    }
}

package kot.com.baselibrary.intfc

/**
 *全局使用的权限调用接口
 *
 * @auth dyx on 2018/12/4.
 */
interface IPermissionsResultListener {
    //成功
    fun onSuccessful( grantResults:IntArray)

    //失败
    fun  onFailure()
}
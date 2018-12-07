package kot.com.baselibrary.common

/**
 *网络请求返回码
 *
 * @auth dyx on 2018/12/4.
 */
class ResultCode {
    companion object {
        // 成功
        const val SUCCESS = 0
        // 网络错误
        const val  NET_ERROR = 10003
        // 认证错误，退出登录
        const val AUTHENTICATION_ERROR = 103000
    }
}
package kot.com.baselibrary.common

/**
 *这是一个全局提供公共Code的类
 *
 * @auth dyx on 2018/12/4.
 */
class ProviderConstant {
    companion object {
        const val KEY_SP_IS_WARN = "is_warn"
        const val KEY_SP_UPDATA_DATAS = "datas"
//        //unreadMessageCount
//        const val KEY_PARM_MD5 = "sp_parm"

        // token值
        const val KEY_SP_USER_INFO = "sp_user_info"
        // token值
        const val KEY_SP_TOKEN = "sp_token"
        //H5配置信息
        const val KEY_SP_H5_SETTING = "sp_h5_info"

        // 第一次打开APP
        const val KEY_SP_CURRENT_APP_VERSION = "sp_current_app_version"

        // ARouter值
        const val AROUTER_NAME_FRAGMENT = "arouter_name_fragment"
        const val AROUTER_VALUE_HOME = 0
        const val AROUTER_VALUE_PRODUCT = 1
        const val AROUTER_VALUE_TREASURE = 2
        const val AROUTER_VALUE_USERCENTER = 3
        // 是否登出状态
        const val AROUTER_IS_LOGOUT = "arouter_is_logout"
    }
}
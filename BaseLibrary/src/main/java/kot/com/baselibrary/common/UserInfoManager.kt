package kot.com.baselibrary.common

import android.text.TextUtils
import android.util.Base64
import com.google.gson.Gson
import com.kotlin.base.utils.AppPrefsUtils
import kot.com.baselibrary.data.UserInfo

/**
 *用户信息管理类
 *
 * @auth dyx on 2018/12/4.
 */
class UserInfoManager {
    /** 初始化UserInfo信息 */
    private var userInfo: UserInfo = UserInfo()
    /**
     * 单例模式
     */
    private constructor() {
        // 初始化
        init()
    }

    /**
     * 双重校验锁单例模式
     */
    companion object {
        val instance: UserInfoManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            UserInfoManager()
        }
    }

    /**
     * 初始化
     */
    private fun init() {
        // 读取本地数据到UserInfo
        readUserInfo()
    }

    /**
     * 设置用户信息
     */
    fun setUserInfo(userInfo: UserInfo) {
        // UserInfo本地和服务器存在差别，所以设置的时候，只设置服务器UserInfo有的字段
        this.userInfo.id = userInfo.id
        this.userInfo.avatar = userInfo.avatar
        this.userInfo.verifyRealName = userInfo.verifyRealName
        this.userInfo.mobile = userInfo.mobile
        // 缓存到本地
        saveUserInfo()
    }

    /**
     * 设置token,token在本地通过shared preference保存的时候容易被拿到，所以token通过base64加密进行存储
     */
    fun setToken(token: String, tokenTime: Long, id: Int) {
        userInfo.token = Base64.encodeToString(token.toByteArray(), Base64.DEFAULT)
        userInfo.tokenTime = tokenTime
        userInfo.id = id
        // 缓存到本地
        saveUserInfo()
    }

    /**
     * 获取token
     */
    fun getToken(): String {
        return if (TextUtils.isEmpty(userInfo.token)) {
            ""
        } else {
            String(Base64.decode(userInfo.token, Base64.DEFAULT))
        }
    }
    /**
     * 设置email地址
     */
    fun setEmailAddress(emailAddress: String) {
        userInfo.emailAddress = emailAddress

        // 缓存到本地
        saveUserInfo()
    }

    /**
     * 获取email地址
     */
    fun getEmailAddress(): String {
        return if (null == userInfo.emailAddress) {
            ""
        } else {
            userInfo.emailAddress!!
        }
    }




    /**
     * 获取用户ID
     */
    fun getId(): Int {
        return this.userInfo.id
    }

    /**
     * 获取头像
     */
    fun getAvatar(): String {
        return if (null == userInfo.avatar) {
            ""
        } else {
            userInfo.avatar!!
        }
    }

    /**
     * 获取姓名
     */
    fun getVerifyRealName(): String {
        return if (null == this.userInfo.verifyRealName) {
            ""
        } else {
            this.userInfo.verifyRealName!!
        }
    }

    /**
     * 获取电话号
     */
    fun getMobile(): String {
        return if (null == this.userInfo.mobile) {
            ""
        } else {
            this.userInfo.mobile!!
        }
    }









    /**
     * 保存用户信息到本地
     */
    private fun saveUserInfo() {
        // 转为GSon
        val str = Gson().toJson(userInfo)

        // 存入SP中
        AppPrefsUtils.putString(ProviderConstant.KEY_SP_USER_INFO, str)
    }



    /**
     * 读取本地用户信息
     */
    private fun readUserInfo() {
        try {
            // 获取SP信息
            val str = AppPrefsUtils.getString(ProviderConstant.KEY_SP_USER_INFO)
            // 如果不为空，转换为OBJ
            if (str.isNotEmpty()) {
                userInfo = Gson().fromJson(AppPrefsUtils.getString(ProviderConstant.KEY_SP_USER_INFO), UserInfo::class.java)
            }
        } catch (exception: Exception) {
        }
    }

    /**
     * 是否登陆状态
     */
    fun isLogin(): Boolean {
        // 以UserInfo状态来确定是否为登录状态
        if (userInfo.id > 0) {
            return true;
        }
        return false
    }

    /**
     * 退出登陆
     */
    fun logout() {
        // 清空SP数据
        AppPrefsUtils.putString(ProviderConstant.KEY_SP_USER_INFO, "")
        // 其他清理
        AppPrefsUtils.putInt(ProviderConstant.KEY_SP_IS_WARN, 1)
        // 初始化用户信息
        this.userInfo = UserInfo()
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(): UserInfo? {
        return this.userInfo;
    }
}
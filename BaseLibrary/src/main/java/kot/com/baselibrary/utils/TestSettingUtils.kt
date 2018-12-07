package kot.com.baselibrary.utils

import com.kotlin.base.utils.AppPrefsUtils

/**
 * 用户测试类
 *
 * @auth wxf on 2018/4/26.
 */
object TestSettingUtils{
    // 字段名称
    private const val SP_KEY = "sp_test"
    /**
     * 获取测试设定的url路径
     */
    fun getTestSettingURL():String{
        return AppPrefsUtils.getString(SP_KEY)
    }

    /**
     * 判单是否有设定
     */
    fun hasTestSetting():Boolean{
        return AppPrefsUtils.getString(SP_KEY).isNotEmpty()
    }

    /**
     * 设置
     */
    fun putTextSettingUrl(url:String){
        AppPrefsUtils.putString(SP_KEY,url)
    }
}
package kot.com.kotlinexamal

import android.support.multidex.MultiDex
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kot.com.baselibrary.common.BaseApplication
import kot.com.baselibrary.common.BaseConstant

/**
 *
 *
 * @auth dyx on 2018/12/7.
 */
class AppApplication: BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        // 解决64K限制
        MultiDex.install(this)

        // 初始化Constant数据
        // 极光初始化---暂时用不到，注释掉
//        JPushInterface.setDebugMode(true)
//        JPushInterface.init(this)


        // 初始化Logger
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BaseConstant.SHOW_TEST_MODE
            }
        })
    }



}
package kot.com.baselibrary.common

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.umeng.commonsdk.UMConfigure
import kot.com.baselibrary.injection.component.AppComponent
import kot.com.baselibrary.injection.component.DaggerAppComponent
import kot.com.baselibrary.injection.module.AppModule

/**
 *定义全项目的基类Application方便继承
 *全局的路由跳转是使用的阿里ARouter，如果是调试项目可以打开  ARouter.openLog()和openDebug()，打包时记得关闭
 * @auth dyx on 2018/12/4.
 */
open class BaseApplication : Application(){
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        initAppInjection()
        context=this
        //ARouter初始化
//        ARouter.openLog()
//        ARouter.openDebug()
        ARouter.init(this)
    }

    private fun initAppInjection() {
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }


    /*
        全局伴生对象
     */
    companion object {
        lateinit var context: Context
    }
}
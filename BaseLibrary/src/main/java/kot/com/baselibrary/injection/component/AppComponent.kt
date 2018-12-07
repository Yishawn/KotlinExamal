package kot.com.baselibrary.injection.component

import android.content.Context
import dagger.Component
import kot.com.baselibrary.injection.module.AppModule
import javax.inject.Singleton

/**
 *
 *
 * @auth dyx on 2018/12/4.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    //暴漏出来一个接口
    fun context(): Context
}
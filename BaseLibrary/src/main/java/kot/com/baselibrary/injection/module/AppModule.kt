package kot.com.baselibrary.injection.module

import android.content.Context
import dagger.Module
import dagger.Provides
import kot.com.baselibrary.common.BaseApplication
import javax.inject.Singleton

/**
 *定义的系统级别的AppModule，把全局的context注解到项目力保证全局的CONTEXT单例且不为空
 *
 * @auth dyx on 2018/12/4.
 */
@Module
class AppModule (private val context: BaseApplication){
    @Provides
    @Singleton
    fun providesContext(): Context {
        return context
    }
}
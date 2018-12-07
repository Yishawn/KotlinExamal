package kot.com.baselibrary.injection.module

import com.trello.rxlifecycle2.LifecycleProvider
import dagger.Module
import dagger.Provides

/**
 *基类LifecycleProviderModule，用于自动管理项目中ACTIVITY的生命周期，防止内存泄漏
 *
 * @auth dyx on 2018/12/4.
 */
@Module
class LifecycleProviderModule (private val lifecycleProvider: LifecycleProvider<*>){
    @Provides
    fun providesLifecycleProvider(): LifecycleProvider<*> {
        return lifecycleProvider
    }
}
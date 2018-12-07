package kot.com.baselibrary.injection.component

import android.app.Activity
import android.content.Context
import com.trello.rxlifecycle2.LifecycleProvider
import dagger.Component
import kot.com.baselibrary.injection.ActivityScope
import kot.com.baselibrary.injection.module.ActivityModule
import kot.com.baselibrary.injection.module.LifecycleProviderModule

/**
 *
 *
 * @auth dyx on 2018/12/4.
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class),modules = arrayOf(ActivityModule::class, LifecycleProviderModule::class))
interface ActivityComponent {
    fun activity(): Activity
    fun context(): Context
    fun lifecycleProvider(): LifecycleProvider<*>
}
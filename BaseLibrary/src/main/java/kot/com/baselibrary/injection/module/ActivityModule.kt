package kot.com.baselibrary.injection.module

import android.app.Activity
import dagger.Module
import dagger.Provides
import kot.com.baselibrary.injection.ActivityScope

/**
 *组件化必须的一个MODULE依赖到AppComponent中
 *
 * @auth dyx on 2018/12/4.
 */
@Module
class ActivityModule (private val activity: Activity) {

    @ActivityScope
    @Provides
    fun provideActivity(): Activity {
        return this.activity
    }
}
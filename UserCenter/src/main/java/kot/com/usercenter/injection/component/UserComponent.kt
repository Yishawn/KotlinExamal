package kot.com.usercenter.injection.component

import dagger.Component
import kot.com.baselibrary.injection.PerComponentScope
import kot.com.baselibrary.injection.component.ActivityComponent
import kot.com.usercenter.injection.module.UserModule
import kot.com.usercenter.ui.LoginActivity

/**
 *整个module需要的注册文件，把要注解的activity全部注册在此
 *
 * @auth dyx on 2018/12/5.
 */
@PerComponentScope
@Component(dependencies = arrayOf(ActivityComponent::class),modules = arrayOf(UserModule::class))
interface UserComponent {

    fun inject(activity: LoginActivity)


}
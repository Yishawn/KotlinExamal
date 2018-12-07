package kot.com.usercenter.injection.module

import dagger.Module
import dagger.Provides
import kot.com.usercenter.service.UserService
import kot.com.usercenter.service.impl.UserServiceImpl

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
@Module
class UserModule {
    @Provides
    fun providesUserService(service: UserServiceImpl): UserService {
        return service
    }
}
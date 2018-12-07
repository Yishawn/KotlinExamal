package kot.com.usercenter.service.impl

import io.reactivex.Observable
import kot.com.baselibrary.ext.covert
import kot.com.usercenter.data.protocol.TokenInfo
import kot.com.usercenter.data.repository.UserRepository
import kot.com.usercenter.service.UserService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
class UserServiceImpl @Inject constructor(): UserService {

    @Inject
    lateinit var repository: UserRepository
    override fun login(map: Map<String,String>): Observable<TokenInfo> {
        return repository.login(map)
                .covert()    }


}
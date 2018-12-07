package kot.com.usercenter.data.repository

import io.reactivex.Observable
import kot.com.baselibrary.protocol.BaseResp
import kot.com.baselibrary.req.net.RetrofitFactory
import kot.com.usercenter.data.api.UserApi
import kot.com.usercenter.data.protocol.TokenInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
class UserRepository @Inject constructor(){

    /*
        用户登录
     */
    fun login(map: Map<String,String>): Observable<BaseResp<TokenInfo>> {
        return RetrofitFactory.instance.create(UserApi::class.java).login(map)
    }

}
package kot.com.usercenter.data.api

import io.reactivex.Observable
import kot.com.baselibrary.protocol.BaseResp
import kot.com.usercenter.data.protocol.TokenInfo
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 *用来添加请求接口的
 *
 * @auth dyx on 2018/12/5.
 */
interface UserApi {

    /*
      用户登录
   */
    @POST("api/app/user/login")
    @FormUrlEncoded
    fun login(@FieldMap req: Map<String, String>): Observable<BaseResp<TokenInfo>>


}
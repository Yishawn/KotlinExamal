package kot.com.usercenter.service

import io.reactivex.Observable
import kot.com.usercenter.data.protocol.TokenInfo

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
interface UserService {
    /*
       登陆
    */
    fun login(map: Map<String,String>): Observable<TokenInfo>
}
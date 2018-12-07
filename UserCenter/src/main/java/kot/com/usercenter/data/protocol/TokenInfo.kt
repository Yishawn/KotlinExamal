package kot.com.usercenter.data.protocol

/**
 *Token信息
 *
 * @auth dyx on 2018/12/6.
 */

data class TokenInfo(val id:Int,val value:String,var expiredAt:Long){}
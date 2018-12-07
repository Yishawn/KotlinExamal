package kot.com.baselibrary.data

/**
 *
 *
 * @auth dyx on 2018/12/4.
 */
data class UserInfo (var id:Int, // 用户ID
                     var avatar:String?, // 头像地址
                     var verifyRealName:String?, // 用户名
                     var mobile:String?, // 手机号
                     var emailAddress:String?, // email地址
                     var token:String?, // token
                     var tokenTime:Long // token时间

){

    /**
     * 设置空参构造
     */
    constructor() : this(0,"","","","","",0)
}
package kot.com.baselibrary.protocol

/**
 *定义的网络请求基类，data是服务器返回对象
 *
 * @auth dyx on 2018/12/4.
 */
open class BaseResp <T> (val code :Int,val msg :String,val  data :T){
}
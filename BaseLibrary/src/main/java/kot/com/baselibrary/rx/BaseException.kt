package kot.com.baselibrary.rx

/**
 *自定义的服务器请求数据错误的对象
 *
 * @auth dyx on 2018/12/4.
 */
class BaseException (val code :Int,val msg:String):Throwable()
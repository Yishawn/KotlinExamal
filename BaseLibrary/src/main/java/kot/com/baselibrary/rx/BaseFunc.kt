package kot.com.baselibrary.rx

import io.reactivex.Observable
import io.reactivex.functions.Function
import kot.com.baselibrary.common.ResultCode
import kot.com.baselibrary.protocol.BaseResp

/**
 *自定义的数据返回处理队列的观察者用来消费和传递服务器返回的数据并转化为对象
 *
 * @auth dyx on 2018/12/4.
 */
class BaseFunc<T>: Function<BaseResp<T>, Observable<T>> {
    override fun apply(t: BaseResp<T>): Observable<T> {

        if (t.code!= ResultCode.SUCCESS){
            return Observable.error(BaseException(t.code,t.msg))
        }
        return Observable.just(t.data)
    }


}
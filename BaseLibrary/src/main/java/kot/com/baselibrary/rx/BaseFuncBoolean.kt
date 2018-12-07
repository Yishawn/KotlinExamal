package kot.com.baselibrary.rx

import io.reactivex.Observable
import io.reactivex.functions.Function
import kot.com.baselibrary.protocol.BaseResp

/**
 *自定义的数据返回处理队列的观察者用来消费和传递服务器返回的数据并转化为boolean,比如发送短信成功
 *
 * @auth dyx on 2018/12/4.
 */
class BaseFuncBoolean <T>: Function<BaseResp<T>, Observable<Boolean>> {
    override fun apply(t: BaseResp<T>): Observable<Boolean> {
        if (t.code!=0){
            return Observable.error(BaseException(t.code,t.msg))
        }
        return Observable.just(true)
    }


}
package kot.com.baselibrary.req.net

import kot.com.baselibrary.common.BaseConstant
import kot.com.baselibrary.req.interceptor.SignInterceptor
import kot.com.baselibrary.utils.TestSettingUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 *定义的全局的Retrofit工厂用于处理网络请求的一个基类
 * @auth dyx on 2018/12/4.
 */
class RetrofitFactory private constructor(){
    // 默认超时时间
    val TIME_OUT = 30L
    companion object {
        // lazy 本身就是线程安全的加载方式
        val instance: RetrofitFactory by lazy {
            RetrofitFactory()}
    }
    private val retrofit: Retrofit
    private val interceptor: Interceptor

    init {
        interceptor = Interceptor { chain ->
            val request = chain.request()
                    .newBuilder()
                    .addHeader("charset", "UTF-8")
                    .addHeader("X-Req-Device", "1")
                    .addHeader("X-Req-Version","1.0.0")
                    .build()
            chain.proceed(request)
        }
        // 判断测试模式，并且有测试设定地址，则取测试设定地址
        if(BaseConstant.SHOW_TEST_MODE && TestSettingUtils.hasTestSetting()){
            BaseConstant.SERVER_ADDRESS = TestSettingUtils.getTestSettingURL()
        }

        retrofit = Retrofit.Builder()
                .baseUrl(BaseConstant.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(initClient())
                .build()
    }

    /*
          OKHttp创建
       */
    private fun initClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(initLogInterceptor())
                .addInterceptor(SignInterceptor())
                .addInterceptor(interceptor)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build()
    }

    /*
        日志拦截器
     */
    private fun initLogInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    /*
        联网时具体服务实例化操作方法接口
     */
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}
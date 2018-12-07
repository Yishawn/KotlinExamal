package kot.com.baselibrary.req.interceptor

import com.orhanobut.logger.Logger
import kot.com.baselibrary.utils.ParamsUtils
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.net.URLDecoder
import java.nio.charset.Charset

/**
 * 用于retrofit的网络请求拦截器，设置动态heder
 *参数签名Interceptor（如果有参数里面带'&='符号，会有问题，所以有类似参数的，需要在API中，手动加入Header）
 *
 * @auth dyx on 2018/12/4.
 */
class SignInterceptor  : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取request
        var request = chain.request()

        // 如果设置了header,就不再设置了
        if(null != request.header("X-Req-Timestamp")
                && request.header("X-Req-Timestamp")!!.isNotEmpty()){
            return chain.proceed(request)
        }

        // 初始化参数map
        var paramMap : Map<String,String>? = null
        // 根据get和post请求分别获取参数
        if(request.method() == "GET"){
            paramMap = getParamForGET(request.url().toString())
        } else if(request.method() == "POST"){
            paramMap = getParamForPOST(request.body())
        }

        // 初始化加密数据
        var sign = ""

        // 获取当前时间
        val nowTime= ParamsUtils.getNowTime()

        // 获取加密数据
        sign = if(null != paramMap && paramMap.isNotEmpty()){
            ParamsUtils.getSignature(paramMap,nowTime)
        } else {
            ParamsUtils.getSignatureWithNoParam(nowTime)
        }

        // 设置header
        request = request
                .newBuilder()
                .addHeader("X-Req-Timestamp", nowTime)
                .addHeader("X-Req-Auth", sign)
                .build();
        return chain.proceed(request)
    }

    /**
     * 获取get方法中的参数
     */
    private fun getParamForGET(url : String?):Map<String,String>{
        if(null != url){
            try{
                // 截取问号
                val urlSpl = url.substring(url.indexOf("?")+1)
                // 如果是两位，说明有参数
                if(urlSpl.isNotEmpty()){
                    // 使用以下方法获取参数直接返回
                    return getParamForUrl(urlSpl)
                }
            } catch (exception:Exception){}
        }
        // 没有参数，直接返回
        return mutableMapOf()
    }

    /**
     * 获取Post方法中的参数
     */
    private fun getParamForPOST(requestBody : RequestBody?):Map<String,String>{
        if(null != requestBody){
            // 初始化buffer
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            var charset = Charset.forName("UTF-8")
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(charset)
            }
            // 使用以下方法获取参数直接返回
            return getParamForUrl(buffer.readString(charset))
        }
        // 没有参数，直接返回
        return mutableMapOf()
    }

    /**
     * 根据地址获取参数
     */
    private fun getParamForUrl(url : String?):Map<String,String>{
        // 初始化参数
        val paramMap = mutableMapOf<String, String>()
        // 空判断
        if(null != url && url.trim().isNotEmpty()){
            try {
                // url解码
                val urlEncoder = URLDecoder.decode(url,"UTF8")
                // 根据'&'符号分割
                val paramStrSpl = urlEncoder.split("&")
                if(paramStrSpl.isNotEmpty()){
                    // 循环遍历每个截取后的字符串
                    for (params : String in paramStrSpl){
                        // 把字符串按照'='分割
                        val param = params.split("=")
                        // 分割后放入map当中
                        if(param.isNotEmpty() && param.size == 2){
                            paramMap[param[0]] = param[1]
                        }
                    }
                }
            } catch (exception:Exception){
                var error = ""
                if(null != exception){
                    error = exception.toString()
                }
                Logger.e("getParamForUrl , exception = $error")
            }
        }
        return paramMap
    }
}
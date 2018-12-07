package kot.com.baselibrary.utils

import kot.com.baselibrary.common.UserInfoManager
import java.util.*

/**
 * Created by yixiao on 2018/3/7.
 */
object ParamsUtils {

    /**
     * 获取有参签名
     */
    fun getSignature(maps: Map<String, Any?>, nowTime: String): String {
        val entries = maps.entries
        var singDate = arrayOfNulls<String>(maps.size)
        val uid = UserInfoManager.instance.getId()
        for ((index, entry) in entries.withIndex()) {
            if (entry.value != null) {
                singDate[index] = entry.key
            }
        }
        val afterSort = getSortStringArray(singDate, maps)
        val sign = Md5Util.encode(afterSort + nowTime + UserInfoManager.instance.getToken())
        return uid.toString() + ":" + sign
    }

    /**
     * 获取当前时间
     */
    fun getNowTime(): String {
        val nowTime = System.currentTimeMillis() / 1000
        return nowTime.toString()
    }

    /**
     * 获取无参签名
     */
    fun getSignatureWithNoParam(now_time: String): String {
        val signature = Md5Util.encode(now_time + UserInfoManager.instance.getToken())
        val uid = UserInfoManager.instance.getId()
        return uid.toString() + signature
    }

    /*
       对请求参数根据字母进行排序
     */
    private fun getSortStringArray(arrayToSort: Array<String?>, map: Map<String, Any?>): String {
        // 初始化参数
        var returnSorts = ""
        // 调用数组的静态排序方法sort,且不区分大小写
        Arrays.sort(arrayToSort)
        // 参数遍历相加
        for (i in arrayToSort.indices) {
            returnSorts += map.getValue(arrayToSort[i] as String)
        }
        return returnSorts
    }
}
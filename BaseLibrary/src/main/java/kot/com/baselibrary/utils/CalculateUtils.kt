package kot.com.baselibrary.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

/**
 * Created by yixiao on 2018/3/22.
 */
object CalculateUtils {
    fun IdNOToAge(IdNO: String): Int {
        val ca = Calendar.getInstance()
        val nowYear = ca.get(Calendar.YEAR)
        val nowMonth = ca.get(Calendar.MONTH) + 1
        val len = IdNO.length
        if (len == 18) {
            val IDYear = Integer.parseInt(IdNO.substring(6, 10))
            val IDMonth = Integer.parseInt(IdNO.substring(10, 12))
            return if (IDMonth - nowMonth > 0) {
                nowYear - IDYear - 1
            } else
                nowYear - IDYear
        } else
            println("错误的身份证号")
        return 0
    }

    /**
     * 获取二进制DecimalFormat
     */
    fun getDecimalFormat(str : String) : CustomDecimalFormat {
        val format = CustomDecimalFormat(str)
        format.roundingMode = RoundingMode.FLOOR
        return format
    }

    /**
     * 自定义DecimalFormat
     */
    class CustomDecimalFormat(pattern: String?) : DecimalFormat(pattern) {

        /**
         * 自定义format
         */
        fun customFormat(number: Double): String {
            // 赋值(主要解决，在Android手机上运行时，以下4个数会自动减1的问题)
            return format(number + 0.00001)
        }
    }
}
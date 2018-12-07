package kot.com.baselibrary.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import java.text.DecimalFormat

object Utils {
    private var lastClickTime: Long = 0

    fun isFastDoubleClick(): Boolean {
        val time = System.currentTimeMillis()
        val timeD = time - lastClickTime
//        if (timeD in 1..999) {
//
//            return true
//        }
        if (timeD in 1..300) {

            return true
        }
        lastClickTime = time
        return false
    }
    val fnum = DecimalFormat("#,##0.00")
    val df = CalculateUtils.getDecimalFormat("#,##0.00")
    val points = CalculateUtils.getDecimalFormat("###0.00")

    /** 获取屏幕高度(像素)  */
    fun getScreenHeightPixels(context: Context): Int {
        return getDisplayMetrics(context).heightPixels
    }

    /** 获取屏幕高度(dp)  */
    fun getScreenHeightDp(context: Context): Float {
        val displayMetrics = getDisplayMetrics(context)
        return displayMetrics.heightPixels / displayMetrics.density
    }

    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        val outMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(outMetrics)
        return outMetrics
    }

    fun dip2px(dipValue: Float): Float {
        val scale = Resources.getSystem().getDisplayMetrics().density
        return dipValue * scale + 0.5f
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context
                .resources
                .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context
                    .resources
                    .getDimensionPixelSize(resourceId)
        }
        return result
    }
}
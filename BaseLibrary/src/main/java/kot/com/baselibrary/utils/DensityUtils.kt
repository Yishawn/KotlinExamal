package kot.com.baselibrary.utils

import android.content.Context
import android.util.TypedValue



/**
 * Created by yixiao on 2018/6/29.
 */
object  DensityUtils {
    private fun DensityUtils(){
        throw UnsupportedOperationException("Cannot instantiate the object")
    }

    /**
     * dp转px
     */
    open fun dp2px(ctx: Context, dpVal: Float): Int {
        // 设备像素缩放比
        return (dpVal * ctx.getResources().getDisplayMetrics().density + 0.5f).toInt()// 4.9->5 4.4->4
        /*return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
				ctx.getResources().getDisplayMetrics());*/
    }

    /**
     * sp转px
     *
     * @param ctx
     * @param spVal
     * @return
     */
    open  fun sp2px(ctx: Context, spVal: Float): Int {
        //		return (int) (spVal * (ctx.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, ctx.getResources().getDisplayMetrics()).toInt()
    }

    /**
     * px转sp
     *
     * @param ctx
     * @param pxVal
     * @return
     */
    open  fun px2sp(ctx: Context, pxVal: Float): Float {
        return pxVal / ctx.getResources().getDisplayMetrics().scaledDensity + 0.5f
    }

    /**
     * px转dp
     */
    open  fun px2dp(ctx: Context, px: Int): Float {
        return px / (ctx.getResources().getDisplayMetrics().density + 0.5f)
    }
}
package kot.com.baselibrary.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.view.View


/**
 * APP系统工具
 *
 * @auth wxf on 2018/4/19.
 */
object APPSystemUtils{

    /**
     * 获取版本名称，例如"V1.0.0"
     */
    fun getVersionName(context: Context):String{
        var localVersion = ""
        try {
            val packageInfo = context.applicationContext.packageManager
                    .getPackageInfo(context.packageName, 0)
            localVersion = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return localVersion
    }

    /**
     * get App versionCode
     * @param context
     * @return
     */
    fun getVersionCode(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionCode = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionCode = packageInfo.versionCode.toString() + ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionCode
    }

    /**
     * 设置状态栏字体颜色为黑色
     */
    fun setStatusBar(activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    /**
     * 将dp值转换为px值
     */
    fun dp2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }


}
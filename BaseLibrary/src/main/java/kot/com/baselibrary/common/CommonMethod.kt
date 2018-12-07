package kot.com.baselibrary.common

import android.view.View

/**
 *简单的基础防止重复点击的方法封装
 *
 * @auth dyx on 2018/12/4.
 */
class CommonMethod {
    // 重复点击间隔时间
    val INTERNAL_TIME = 1000

    /**
     * 是否重复点击
     */
    fun isDoubleClick(v: View): Boolean {
        val tag = v.getTag(v.id)
        val lastClickTime = if (tag != null) tag as Long else 0
        val currentTime = System.currentTimeMillis()
        v.setTag(v.id,currentTime)
        return currentTime - lastClickTime < INTERNAL_TIME
    }
}
package kot.com.baselibrary.utils.router

import android.support.v4.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Fragment路由
 *
 * @auth wxf on 2018/6/14.
 */
object RouterFragment{
    /**
     * 主页fragment
     */
    fun getHomeFragment() : Fragment{
        return ARouter.getInstance().build(RouterPath.App.PATH_FRAGMENT).navigation() as Fragment
    }

    /**
     * 产品fragment
     */
    fun getProductFragment() : Fragment{
        return ARouter.getInstance().build(RouterPath.Product.PATH_FRAGMENT).navigation() as Fragment
    }

    /**
     * 财富fragment
     */
    fun getTreasureFragment() : Fragment{
        return ARouter.getInstance().build(RouterPath.Treasure.PATH_FRAGMENT).navigation() as Fragment
    }

    /**
     * 我的fragment
     */
    fun getUserCenterFragment() : Fragment{
        return ARouter.getInstance().build(RouterPath.UserCenter.PATH_FRAGMENT).navigation() as Fragment
    }
}
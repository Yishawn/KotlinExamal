package kot.com.baselibrary.intfc

import kot.com.baselibrary.data.FragmentInfo

/**
 *application接口(用于在lib中初始化各模块数据)
 *
 * @auth dyx on 2018/12/4.
 */
interface IApplicationInit {
    /**
     * 初始化fragment
     */
    fun onCreateFragment() : FragmentInfo
}
package kot.com.kotlinexamal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kot.com.baselibrary.base.utils.ARouterUtils
import kot.com.baselibrary.ext.onClick
import kot.com.baselibrary.ui.activity.BaseActivity
import kot.com.baselibrary.utils.router.RouterPath
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity :BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //kotlin特有的简便点击时间方法
        logout.onClick {
            //arouter跳转第一个参数是上下文，第二个参数是在RouterPath注册的ACTIVITY地址
            ARouterUtils.getInstance().startActivity(this, RouterPath.UserCenter.PATH_LOGIN)
        }

    }
}

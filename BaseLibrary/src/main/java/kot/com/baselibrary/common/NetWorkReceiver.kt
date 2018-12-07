package kot.com.baselibrary.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 *网络监听
 *
 * @auth dyx on 2018/12/4.
 */
class NetWorkReceiver : BroadcastReceiver {
    // 上下文
    var context : Context
    // 拦截器
    var filter : IntentFilter
    // 监听
    var listenter : OnNetWorkChangeListener
    // 网络信息
    lateinit var networkInfo : NetworkInfo
    // 连接管理
    var connectivityManager: ConnectivityManager
    /**
     * 构造器
     */
    constructor(context: Context, listener : OnNetWorkChangeListener){
        this.context = context
        this.listenter = listener

        // 拦截器
        filter = IntentFilter()
//        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // 网络信息
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP){
            if(null!=connectivityManager.activeNetworkInfo){
                networkInfo =connectivityManager.activeNetworkInfo
            }
        }else{
            val networks = connectivityManager.allNetworks
            //用于存放网络连接信息
            val sb = StringBuilder()
            //通过循环将网络信息逐个取出来
            for (i in networks.indices) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                networkInfo = connectivityManager.getNetworkInfo(networks[i])
                sb.append(networkInfo.typeName + " connect is " + networkInfo.isConnected)
            }
        }

    }

    /**
     * 注册监听
     */
    fun register(){
        context.registerReceiver(this, filter);
    }

    /**
     * 注销监听
     */
    fun unRegister(){
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager = this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (null != networkInfo) {
            listenter.onNetWorkChangeListener(networkInfo.isAvailable)
        }
    }

    /**
     * 网络是否可用
     */
    fun isNetWorkAvailable(){

    }

    /**
     * 网络变化监听
     */
    interface OnNetWorkChangeListener {
        // 网络变化
        fun onNetWorkChangeListener(isAvailable : Boolean)
    }
}
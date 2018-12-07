package kot.com.baselibrary.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
class AppManger private constructor(){
    //管理栈数组
    private val activityStack: Stack<Activity> = Stack()
    //采用单例模式静态方法实例化
    companion object {
        val instance:AppManger by lazy { AppManger() }
    }
    /*
        入栈
     */
    fun addActivity(activity: Activity){
        activityStack.add(activity)

    }
    /*
        出栈
     */
    fun finishActivity(activity: Activity){
        activity.finish()
        activityStack.remove(activity)
    }
    //获取当前栈顶的ACTIVITY
    fun currentActivity(): Activity {
        return activityStack.lastElement()
    }
    fun finishAllActivity(){
        for (activity in activityStack){
            activity.finish()
        }
        activityStack.clear()
    }
    @SuppressLint("MissingPermission")
    fun exitApp(context: Context){
        finishAllActivity()
        val activityManger=context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManger.killBackgroundProcesses(context.packageName)
        System.exit(0)

    }

}
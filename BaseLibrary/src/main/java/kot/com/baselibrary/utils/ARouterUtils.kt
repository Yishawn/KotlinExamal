package kot.com.baselibrary.base.utils

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import kot.com.baselibrary.R

import javax.inject.Singleton


class ARouterUtils {
      companion object {
    fun getInstance():ARouterUtils{
        var myinstance:ARouterUtils?=null
        if(myinstance ==null){
          synchronized(ARouterUtils::class){
              if(myinstance==null){
                  myinstance= ARouterUtils()
              }
          }
        }
        return myinstance!!
    }

    }
//    fun startActivity(mycontext: Context,path:String,method: () -> Unit){
//        if(!TextUtils.isEmpty(path)){
//            ARouter.getInstance().build(path).withTransition(R.anim.slide_right_in,R.anim.slide_normal).navigation(mycontext,object : NavCallback(){
//                override fun onArrival(postcard: Postcard?) {
//                    method
//                }
//            })
//        }
//    }
    fun startActivity(mycontext: Context,path:String){
        if(!TextUtils.isEmpty(path)){
            ARouter.getInstance().build(path).withTransition(R.anim.slide_right_in,R.anim.slide_normal).navigation(mycontext)
        }
    }
   fun startActivityWithBundle(mycontext: Context,path:String,myBundle:Bundle){
       if(!TextUtils.isEmpty(path)) {
           ARouter.getInstance()
                   .build(path)
                   .withTransition(R.anim.slide_right_in,R.anim.slide_normal)
                   .withBundle("arouterBundle",myBundle)
                   .navigation(mycontext)
       }
   }
}
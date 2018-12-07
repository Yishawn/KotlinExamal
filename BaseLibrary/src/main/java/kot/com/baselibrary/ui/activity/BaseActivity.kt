package kot.com.baselibrary.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kot.com.baselibrary.common.AppManger
import kot.com.baselibrary.common.CommonMethod
import kot.com.baselibrary.intfc.IPermissionsResultListener
import kot.com.baselibrary.utils.Utils
import kot.com.baselibrary.widgets.PubBtnDialog
import org.jetbrains.anko.find

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
open class BaseActivity : RxAppCompatActivity() {
    var presstime: Long = 0
    var activityList: MutableList<Activity> = ArrayList()
    lateinit var mListener: IPermissionsResultListener
    private var mRequestCode: Int = 0
    var mListPermissions: MutableList<String> = ArrayList()
    lateinit var dialogs: Dialog
    var isNeedApply=true
    // 公共类
    lateinit var mCommonMethod: CommonMethod

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManger.instance.addActivity(this)
        addActivity(this)
        mCommonMethod = CommonMethod()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    fun changeHeaderView(header_view: View, color: Int){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            header_view.visibility = View.GONE
        } else {
            var result = 0
            val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = this.resources.getDimensionPixelSize(resourceId)
            }
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, result)
            header_view.layoutParams = params
        }
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    /**
     * 是否重复点击
     */
    protected fun isDoubleClick(v: View): Boolean {
        return mCommonMethod.isDoubleClick(v)
    }

    private fun addActivity(activity: Activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManger.instance.finishActivity(this)
        activityList.remove(this)
    }

    fun checkPermissions(permissions: Array<out String>, requestCode: Int, listener: IPermissionsResultListener):Boolean {
        //权限不能为空
        mListPermissions.clear()
        if (permissions != null || permissions.size != 0) {
            mListener = listener
            mRequestCode = requestCode
            for (i in permissions) {
                if (isHavePermissions(i).not()) {
                    mListPermissions.add(i)

                }
            }
            if (mListPermissions.size==0){
                return true
            }


            //遍历完后申请
            applyPermissions()
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == mRequestCode) {
            if (grantResults.size > 0) {
                for(rs in grantResults){
                    if(rs == -1){
                        mListener.onFailure()
                        return
                    }
                }
                mListener.onSuccessful(grantResults)
                return
            }

            mListener.onFailure()
            return
        }
    }
    //判断权限是否申请
    private fun  isHavePermissions( permissions:String):Boolean {
        if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    //申请权限
    fun applyPermissions() {
        if (!mListPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, mListPermissions.toTypedArray(), mRequestCode)
        }
    }
    //获取Window中视图content
    val contentView: View
        get() {
            val content = find<FrameLayout>(android.R.id.content)
            return content.getChildAt(0)
        }

    /**
     * 一个按钮的对话框(点击返回键可以取消对话框)
     */
    fun showOneBtnDialog(activity: Activity, content:String, okText:String, onOkClick: View.OnClickListener, color:Int){
        showOneBtnDialog(activity,content,okText, onOkClick,color,true)
    }

    /**
     * 一个按钮的对话框
     */
    fun showOneBtnDialog(activity: Activity, content:String, okText:String, onOkClick: View.OnClickListener, color:Int, isCancelable : Boolean){
        dialogs = PubBtnDialog.Builder(activity, PubBtnDialog.DialogMode.ONE_BTN)
                .setCanceledOnTouchOutside(false) // 点击对话框以外的部分，是否可以关闭对话框
                .setCenterBtnTextColor(color)
                .setCenterBtnOnclickListener(onOkClick)
                .setCenterBtnText(okText)
                .setMessage(content)
                .setCancelable(isCancelable)// 点击返回键是否可以取消对话框
                .createDialog()
        dialogs.show()
    }

    /**
     * 两个按钮的对话框(左键为取消)
     */
    fun showTwoBtnCancelDialog(activity: Activity, content:String, okText:String, cancelText:String, onOkClick: View.OnClickListener, color:Int) {
        dialogs = PubBtnDialog.Builder(activity, PubBtnDialog.DialogMode.TWO_BTN)
                .setCanceledOnTouchOutside(false)
                .setLeftBtnCancel(true) // 设置左键为取消键，点击会关闭dialog
                .setLeftBtnText(cancelText)
                .setRightBtnText(okText)
                .setRightBtnTextColor(color)
                .setRightBtnOnclickListener(onOkClick)
                .setMessage(content)
                .createDialog()
        dialogs.show()
    }

    /**
     * 两个按钮的对话框
     */
    fun showTwoBtnDialog(activity: Activity, content:String, okText:String, cancelText:String, onOkClick: View.OnClickListener, color:Int, onCancelOkClick: View.OnClickListener) {
        dialogs = PubBtnDialog.Builder(activity, PubBtnDialog.DialogMode.TWO_BTN)
                .setCanceledOnTouchOutside(false)
                .setLeftBtnText(cancelText)
                .setLeftBtnOnclickListener(onCancelOkClick)
                .setRightBtnText(okText)
                .setRightBtnTextColor(color)
                .setRightBtnOnclickListener(onOkClick)
                .setMessage(content)
                .createDialog()
        dialogs.show()
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.action == MotionEvent.ACTION_DOWN) {
            if (Utils.isFastDoubleClick()) {
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}
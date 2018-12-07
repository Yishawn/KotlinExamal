package kot.com.baselibrary.toast
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import kot.com.baselibrary.common.BaseApplication


/**
 * Created by yiheng on 2018/3/29.
 */
class ToastUtils {

    fun showToast(context: Context, content: String) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(content)
        }
        toast!!.show()

    }

    companion object {
        private var toast: Toast? = null

        fun centerToast(context: Context, string: String) {
            if (toast == null) {
                toast = Toast.makeText(BaseApplication.context, null, Toast.LENGTH_SHORT)
            }
            toast!!.setText(string)
            toast!!.setGravity(Gravity.CENTER, 0, 0)
            toast!!.show()
        }

        fun defaultToast(context: Context, string: String) {
            var toast = Toast.makeText(context, null, Toast.LENGTH_SHORT)
            toast.setText(string)
            toast.show()
        }
    }


}
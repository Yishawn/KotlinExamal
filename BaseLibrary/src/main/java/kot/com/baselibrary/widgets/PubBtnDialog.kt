package kot.com.baselibrary.widgets

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kot.com.baselibrary.R


/**
 * 公共弹窗(目前为一个按钮和两个按钮,默认两个按钮)
 *
 * @auth wxf on 2018/4/23.
 */
class PubBtnDialog(context :Context): Dialog(context) {
    // 对话框模式
    enum class DialogMode{
        ONE_BTN,TWO_BTN
    }
    class Builder(context: Context, private val dialogMode:DialogMode) {
        // 弹窗文本
        private var message : String? = null
        // 左按钮监听
        private var leftBtnClickListener : View.OnClickListener? = null
        // 左键是否为取消键
        private var isLeftCancel : Boolean = false
        // 左键文本
        private var leftText : String = ""
        // 左键文本颜色
        private var leftTextColor : Int = -1
        // 右按钮监听
        private var rightBtnClickListener : View.OnClickListener? = null
        // 右键是否为取消键
        private var isRightCancel : Boolean = false
        // 右键文本
        private var rightText : String = ""
        // 右键文本颜色
        private var rightTextColor : Int = -1
        // 单钮监听
        private var centerBtnClickListener : View.OnClickListener? = null
        // 单按钮文本
        private var centerText : String = ""
        // 单按钮文本颜色
        private var centerTextColor : Int = -1
        // 点击外区域是否关闭
        private var isCanceledOnTouchOutside : Boolean = true
        // 是否可以点击手机Back键取消对话框显示
        private var isCancelable : Boolean = true
        // 布局
        private val layout: View
        // 初始化
        private val dialog: PubBtnDialog = PubBtnDialog(context)

        init {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //自定义style
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layout = inflater.inflate(R.layout.alert_layout, null)
            dialog.addContentView(layout, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))


            // 将对话框的大小按屏幕大小的百分比设置
            val m = (context as Activity).windowManager
            // 获取屏幕宽、高用
            val d = m.defaultDisplay
            // 获取对话框当前的参数值
            val p = dialog.window.attributes
            // 高度设置为屏幕
            p.height = (d.height * 0.27).toInt()
            // 宽度设置为屏幕
            p.width = (d.width * 0.83).toInt()
            dialog.window.attributes = p
        }

        /**
         * 设置单键点击监听
         */
        fun setCenterBtnOnclickListener(listener: View.OnClickListener): Builder{
            centerBtnClickListener = listener
            return this
        }

        /**
         * 设置单按钮按键文本
         */
        fun setCenterBtnText(text: String) : Builder{
            this.centerText = text
            return this
        }

        /**
         * 设置单按键颜色
         */
        fun setCenterBtnTextColor(color: Int) : Builder{
            this.centerTextColor = color
            return this
        }

        /**
         * 设置左侧按键颜色
         */
        fun setLeftBtnTextColor(color: Int) : Builder{
            this.leftTextColor = color
            return this
        }

        /**
         * 设置右侧按键颜色
         */
        fun setRightBtnTextColor(color: Int) : Builder{
            this.rightTextColor = color
            return this
        }

        /**
         * 设置左侧按键文本
         */
        fun setLeftBtnText(text: String) : Builder{
            this.leftText = text
            return this
        }

        /**
         * 设置右侧按键文本
         */
        fun setRightBtnText(text: String) : Builder{
            this.rightText = text
            return this
        }

        /**
         * 设置右键点击监听
         */
        fun setRightBtnOnclickListener(listener: View.OnClickListener): Builder{
            rightBtnClickListener = listener
            return this
        }

        /**
         * 设置左键点击监听
         */
        fun setLeftBtnOnclickListener(listener: View.OnClickListener): Builder{
            leftBtnClickListener = listener
            return this
        }

        /**
         * 设置左键为取消键
         */
        fun setLeftBtnCancel(isCancel : Boolean): Builder{
            isLeftCancel = isCancel
            return this
        }

        /**
         * 设置右键为取消键
         */
        fun setRightBtnCancel(isCancel : Boolean): Builder{
            isRightCancel = isCancel
            return this
        }

        /**
         * 设置信息
         */
        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        /**
         * 设置是否通过点击对话框之外的地方取消对话框显示(默认true)
         */
        fun setCanceledOnTouchOutside(isCanceledOnTouchOutside : Boolean):Builder {
            this.isCanceledOnTouchOutside = isCanceledOnTouchOutside
            return this
        }

        /**
         * 设置是否以点击手机Back键取消对话框显示(默认true)
         */
        fun setCancelable(isCanceled : Boolean):Builder {
            this.isCancelable = isCanceled
            return this
        }


        /**
         * 创建对话框
         *
         * @return
         */
        fun createDialog(): PubBtnDialog {

            // 设置对话框类型
            when(dialogMode){
                DialogMode.ONE_BTN ->{
                    // 单个按钮
                    layout.findViewById<LinearLayout>(R.id.ll_btn_two).visibility = View.GONE
                    layout.findViewById<LinearLayout>(R.id.ll_btn_one).visibility = View.VISIBLE
                }
                DialogMode.TWO_BTN ->{
                    // 两个按钮
                    layout.findViewById<LinearLayout>(R.id.ll_btn_two).visibility = View.VISIBLE
                    layout.findViewById<LinearLayout>(R.id.ll_btn_one).visibility = View.GONE
                }
            }

            // 文本内容
            if (message != null) {
                (layout.findViewById<TextView>(R.id.tv_text) as TextView).text = message
            }

            // 左键文本颜色
            if(leftTextColor != -1){
                (layout.findViewById<Button>(R.id.btn_left) as Button).setTextColor(leftTextColor)
            }

            // 左键文本
            if(leftText.isNotEmpty()){
                (layout.findViewById<Button>(R.id.btn_left) as Button).text = leftText
            }

            // 左键点击监听
            if(null != leftBtnClickListener){
                (layout.findViewById<Button>(R.id.btn_left) as Button).setOnClickListener { v -> leftBtnClickListener!!.onClick(v) }
            }

            // 左键为取消键
            if(isLeftCancel){
                (layout.findViewById<Button>(R.id.btn_left) as Button).setOnClickListener { dialog.dismiss() }
            }

            // 右键文本颜色
            if(rightTextColor != -1){
                (layout.findViewById<Button>(R.id.btn_right) as Button).setTextColor(rightTextColor)
            }

            // 右键文本
            if(rightText.isNotEmpty()){
                (layout.findViewById<Button>(R.id.btn_right) as Button).text = rightText
            }

            // 右键点击监听
            if(null != rightBtnClickListener){
                (layout.findViewById<Button>(R.id.btn_right) as Button).setOnClickListener { v -> rightBtnClickListener!!.onClick(v) }
            }

            // 右键为取消键
            if(isRightCancel){
                (layout.findViewById<Button>(R.id.btn_right) as Button).setOnClickListener { dialog.dismiss() }
            }

            // 单键文本颜色
            if(centerTextColor != -1){
                (layout.findViewById<Button>(R.id.btn_center) as Button).setTextColor(centerTextColor)
            }

            // 单键文本
            if(centerText.isNotEmpty()){
                (layout.findViewById<Button>(R.id.btn_center) as Button).text = centerText
            }

            // 单键点击监听
            if(null != centerBtnClickListener){
                (layout.findViewById<Button>(R.id.btn_center) as Button).setOnClickListener { v -> centerBtnClickListener!!.onClick(v) }
            }

            // 设置布局
            dialog.setContentView(layout)
            // 是否可以点击手机Back键取消对话框显示
            dialog.setCancelable(isCancelable)
            // 是否通过点击对话框之外的地方取消对话框显示
            dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside)

            return dialog
        }
    }
}
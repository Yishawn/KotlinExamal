package kot.com.baselibrary.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.TextView
import com.orhanobut.logger.Logger
import kot.com.baselibrary.ext.setVisible
import kot.com.baselibrary.utils.JsInterface


/**
 * 类描述：自定义带进度加载条的webView
 *
 * @version :
 */
class ProgressWebView(context: Context, attrs: AttributeSet) : WebView(context, attrs) {
    private val mHandler: Handler = Handler()
    private val mWebView: WebView
    private var myReques: String = ""
    private var mTitle: TextView = TextView(context)
    private var isSetTitle: Boolean = false
    private var progressView: ProgressView? = null//进度条
    private var isLoadSuccess = true
    private var isShowProgressWebView: Boolean = true
    /** 界面加载状态监听 */
    private var pageStatusListener:IPageStatusListener? = null

    /**
     * 刷新界面（此处为加载完成后进度消失）
     */
    init {
        mWebView = this
        initSettings()
    }

    private val runnable = Runnable { progressView?.visibility = View.GONE }

    private fun initSettings() {
        //初始化进度条
        progressView = ProgressView(context)
        progressView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2)
        progressView?.setColor(Color.parseColor("#BB905B"))
        progressView?.setProgress(10)
        //把进度条加到Webview中
        addView(progressView)
        // 初始化设置
        val mSettings = this.settings
        mSettings.javaScriptEnabled = true//开启javascript
        mSettings.domStorageEnabled = true//开启DOM
        mSettings.defaultTextEncodingName = "utf-8"//设置字符编码
        //设置web页面
        mSettings.allowFileAccess = true//设置支持文件流
        mSettings.setSupportZoom(true)// 支持缩放
        mSettings.builtInZoomControls = false// 支持缩放
        mSettings.useWideViewPort = true// 调整到适合webview大小
        mSettings.loadWithOverviewMode = true// 调整到适合webview大小
        mSettings.defaultZoom = WebSettings.ZoomDensity.FAR// 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        //提高渲染的优先级,目的是为了加载更快，现总结几种加速WebView加载的方法
        // 1、提高渲染的优先级 mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 2、使用webView.getSettings().setBlockNetworkImage，把图片加载放在最后来加载渲染。
        // 3,使用硬件加速，该功能在Android 3.0 (API level 11)才加入。
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        mSettings.blockNetworkImage = true
        mSettings.setAppCacheEnabled(false)//开启缓存机制
        setWebViewClient(MyWebClient())
        //AndroidWebView 是jsbridge,如果h5想要调用app端方法，需要用到这个，app中不需要用到
        addJavascriptInterface(JsInterface(context), "AndroidWebView")
        setWebChromeClient(MyWebChromeClient())
        mWebView.isVerticalScrollBarEnabled=false
        mWebView.isHorizontalScrollBarEnabled=false
    }

    /**
     * 自定义WebChromeClient
     */
    private inner class MyWebChromeClient : WebChromeClient() {
        /**
         * 进度改变的回掉
         *
         * @param view        WebView
         * @param newProgress 新进度
         */
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (!isShowProgressWebView) {//如果不显示进度条，直接跳过更新进度条的操作
                return
            }
            var newProgress = newProgress
            Logger.e("当前进度为"+newProgress)
            if (newProgress == 100) {
                progressView?.setProgress(100)
                //0.2秒后隐藏进度条
                mHandler.postDelayed(runnable, 200)
            } else if (progressView?.visibility == View.GONE) {
                progressView?.visibility = View.VISIBLE
            }
            //设置初始进度10，这样会显得效果真一点，总不能从1开始吧
            if (newProgress < 10) {
                newProgress = 10
            }
            //不断更新进度
            progressView?.setProgress(newProgress)
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            if (isSetTitle && !title.toString().startsWith("http") && "网页无法打开" != title.toString()) {
                if (title != null) {
                    mTitle.text = title
                }
            }
        }
    }

    fun ssetTitle(title: TextView, nowTitle: String?) {
        if (TextUtils.isEmpty(nowTitle)) {
            mTitle = title
            isSetTitle = true
        }
    }

    fun loadDetails(webFunction: String) {
        try {
            myReques = webFunction
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private inner class MyWebClient : WebViewClient() {
        /**
         * 加载过程中 拦截加载的地址url
         *
         * @param view
         * @param url  被拦截的url
         * @return
         */

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            mWebView.loadUrl(url)
            return true
        }

        /**
         * 页面加载过程中，加载资源回调的方法
         *
         * @param view
         * @param url
         */
        override fun onLoadResource(view: WebView, url: String) {
            super.onLoadResource(view, url)
        }

        /**
         * 页面加载完成回调的方法
         *
         * @param view
         * @param url
         */
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            //  Logger.e("页面加载完毕")
            // 关闭图片加载阻塞
            if (isLoadSuccess) {
                mWebView.setVisible(true)
            } else {
                mWebView.setVisible(false)
            }
            Logger.e("onPageFinished")
            view.settings.blockNetworkImage = false
//            loadUrl("javascript:getNewsID($myNewsId)")
            if (!TextUtils.isEmpty(myReques)) {
                loadUrl(myReques)
            }

            // 返回监听
            if(null != pageStatusListener){
                pageStatusListener!!.onPageFinished()
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            isLoadSuccess = true
            super.onPageStarted(view, url, favicon)

        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            // super.onReceivedError(view, request, error)
            mWebView.setVisible(false)
            isLoadSuccess = false
            // 返回监听
            if(null != pageStatusListener){
                pageStatusListener!!.onPageError()
            }
            Logger.e("onReceivedError")
        }

        override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
            super.onScaleChanged(view, oldScale, newScale)
            this@ProgressWebView.requestFocus()
            this@ProgressWebView.requestFocusFromTouch()
        }
    }

    //点击返回上一页面而不是退出浏览器
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * dp转换成px
     *
     * @param context Context
     * @param dp      dp
     * @return px值
     */
    private fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun hideProgress() {
        isShowProgressWebView = false
        progressView?.visibility = View.GONE
    }

    /**
     * 设置页面监听
     *
     * @param listener
     *          页面监听
     */
    fun setPageStatusListener(listener : IPageStatusListener){
        pageStatusListener = listener
    }

    /**
     * 界面状态接口
     */
    interface IPageStatusListener{
        /**
         * 界面加载完成
         */
        fun onPageFinished()

        /**
         * 界面错误
         */
        fun onPageError()
    }
}
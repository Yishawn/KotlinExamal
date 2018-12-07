package kot.com.baselibrary.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat

import android.util.AttributeSet
import android.view.View
import kot.com.baselibrary.R


class WebViewProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var progress = 1//进度默认为1
    private var paint: Paint? = null//进度条的画笔

    init {
        initPaint(context)
    }

    private fun initPaint(context: Context) {
        //颜色渐变从colors[0]到colors[2],透明度从0到1
        //        LinearGradient shader = new LinearGradient(
        //                0, 0,
        //                100, HEIGHT,
        //                colors,
        //                new float[]{0 , 0.5f, 1.0f},
        //                Shader.TileMode.MIRROR);
        paint = Paint(Paint.DITHER_FLAG)
        paint!!.style = Paint.Style.STROKE// 填充方式为描边
        paint!!.strokeWidth = HEIGHT.toFloat()//设置画笔的宽度
        paint!!.isAntiAlias = true// 抗锯齿
        paint!!.isDither = true// 使用抖动效果
        paint!!.color = ContextCompat.getColor(context, R.color.webview_progress_color)//画笔设置颜色
        //        paint.setShader(shader);//画笔设置渐变
    }

    /**
     * 设置进度
     * @param progress 进度值
     */
    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()//刷新画笔
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, (getWidth() * progress / 100).toFloat(), HEIGHT.toFloat(), paint!!)//画矩形从（0.0）开始到（progress,height）的区域
    }


    companion object {
        private val HEIGHT = 5//进度条高度为5
        //  渐变颜色数组
        private val colors = intArrayOf(-0x852dc9, -0x753eb6, 0x35B056) //int类型颜色值格式：0x+透明值+颜色的rgb值
    }
}
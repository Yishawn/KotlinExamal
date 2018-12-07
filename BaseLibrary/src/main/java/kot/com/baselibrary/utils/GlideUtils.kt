package kot.com.baselibrary.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import kot.com.baselibrary.R


/*
    Glide工具类
 */
object GlideUtils {
    fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).centerCrop().into(imageView)
    }

    fun loadImageFitCenter(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).fitCenter().into(imageView)
    }

    /*
        当fragment或者activity失去焦点或者destroyed的时候，Glide会自动停止加载相关资源，确保资源不会被浪费
     */
    fun loadUrlImage(context: Context, url: String, imageView: ImageView){
        Glide.with(context).load(url).placeholder(R.drawable.icon_back).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.icon_back).centerCrop().into(
                object : SimpleTarget<GlideDrawable>() {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 glideAnimation: GlideAnimation<in GlideDrawable>) {
                        imageView.setImageDrawable(resource)
                    }
                })
    }

    /**
     * 加载头像资源(显示边框)
     *
     * @param context
     *          上下文
     * @param url
     *          头像地址
     * @param imageView
     *          头像所用的View
     */
    fun loadHeadImage(context: Context, url: String, imageView: ImageView){
        loadHeadImage(context, url, imageView, true)
    }

    /**
     * 加载头像资源
     *
     * @param context
     *          上下文
     * @param url
     *          头像地址
     * @param imageView
     *          头像所用的View
     * @param showBorder
     *          显示边框
     */
    fun loadHeadImage(context: Context, url: String, imageView: ImageView,showBorder : Boolean){
        // 初始化圆形设置
        val glideCircleTransform = if(showBorder){
            // 显示边框
            GlideCircleTransform(context, 2, context.resources.getColor(R.color.white))
        } else {
            // 不显示边框
            GlideCircleTransform(context)
        }

        // 设置glide
        Glide.with(context).load(url).centerCrop().placeholder(R.drawable.img_user_normal_photo)
                .transform(glideCircleTransform)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView)
    }

    internal class GlideCircleTransform : BitmapTransformation {

        private var mBorderPaint: Paint? = null
        private var mBorderWidth: Float = 0f

        constructor(context: Context) : super(context) {}

        constructor(context: Context, borderWidth: Int, borderColor: Int) : super(context) {
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth

            mBorderPaint = Paint()
            mBorderPaint!!.isDither = true
            mBorderPaint!!.isAntiAlias = true
            mBorderPaint!!.color = borderColor
            mBorderPaint!!.style = Paint.Style.STROKE
            mBorderPaint!!.strokeWidth = mBorderWidth
        }


        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
            return circleCrop(pool, toTransform)
        }

        private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
            if (source == null) return null

            val size = (Math.min(source.width, source.height) - mBorderWidth / 2).toInt()
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2
            val squared = Bitmap.createBitmap(source, x, y, size, size)
            var result: Bitmap? = pool.get(size, size, Bitmap.Config.ARGB_8888)
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            }
            val canvas = Canvas(result)
            val paint = Paint()
            paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            if (null != mBorderPaint) {
                val borderRadius = r - mBorderWidth / 2
                canvas.drawCircle(r, r, borderRadius, mBorderPaint)
            }
            return result
        }

        override fun getId(): String {
            return javaClass.name
        }
    }
}

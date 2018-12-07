package kot.com.baselibrary.widgets

import android.content.Context
import android.widget.ImageView
import com.youth.banner.loader.ImageLoader
import kot.com.baselibrary.utils.GlideUtils

/**
 * Created by yixiao on 2018/3/12.
 */
class BannerImageLoader:ImageLoader() {
    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        GlideUtils.loadUrlImage(context, path.toString(), imageView)
    }
}
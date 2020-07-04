package cn.wthee.pcrtool.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions


object GlideUtil {

    private val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.load)
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .timeout(30000)

    fun load(url: String, view: ImageView, error: Int, fragment: Fragment?) {
        Glide.with(MyApplication.getContext())
            .load(url)
            .error(error)
            .thumbnail(Glide.with(view).load(R.drawable.load))
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    fragment?.startPostponedEnterTransition()
                    view.setImageResource(error)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    fragment?.startPostponedEnterTransition()
                    view.setImageResource(0)
                    return false
                }
            })
            .apply(options)
            .into(view)
    }

}
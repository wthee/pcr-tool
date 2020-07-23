package cn.wthee.pcrtool.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
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
        .timeout(10 * 60 * 1000)

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

    fun loadReturnBitmap(url: String, view: ImageView, error: Int, onBitmap: OnBitmap) {
        Glide.with(MyApplication.getContext())
            .load(url)
            .error(error)
            .thumbnail(Glide.with(view).load(R.drawable.load))
            .apply(options)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
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
                    onBitmap.returnBitmap(resource?.toBitmap()!!)
                    view.setImageResource(0)
                    return false
                }
            })
            .into(view)
    }

    fun loadWithListener(
        url: String,
        view: ImageView,
        error: Int,
        fragment: Fragment?,
        onLoadListener: OnLoadListener
    ) {
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
                    onLoadListener.onSuccess()
                    view.setImageResource(0)
                    return false
                }
            })
            .apply(options)
            .into(view)
    }

}

interface OnLoadListener {
    fun onSuccess()
}

interface OnBitmap {
    fun returnBitmap(bitmap: Bitmap)
}
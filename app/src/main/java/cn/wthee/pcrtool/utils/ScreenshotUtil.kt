package cn.wthee.pcrtool.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.collection.LruCache
import androidx.recyclerview.widget.RecyclerView
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 截图保存
 */
object ScreenshotUtil {

    /**
     * [view] 转 [Bitmap]
     */
    fun getBitmap(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    /**
     * 保存 RecyclerView
     */
    fun shotRecyclerView(view: RecyclerView): Bitmap? {
        try {
            val adapter = view.adapter
            var bigBitmap: Bitmap? = null
            if (adapter != null) {
                val size = adapter.itemCount
                var height = 0
                val paint = Paint()
                var iHeight = 0
                val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

                // Use 1/8th of the available memory for this memory cache.
                val cacheSize = maxMemory / 8
                val bitmaCache: LruCache<String, Bitmap> = LruCache(cacheSize)
                for (i in 0 until size) {
                    val holder = adapter.createViewHolder(view, adapter.getItemViewType(i))
                    adapter.onBindViewHolder(holder, i)
                    holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    holder.itemView.layout(
                        0, 0, holder.itemView.measuredWidth,
                        holder.itemView.measuredHeight
                    )
                    holder.itemView.isDrawingCacheEnabled = true
                    holder.itemView.buildDrawingCache()
                    val drawingCache = holder.itemView.drawingCache
                    if (drawingCache != null) {
                        bitmaCache.put(i.toString(), drawingCache)
                    }
                    height += holder.itemView.measuredHeight
                }
                bigBitmap = Bitmap.createBitmap(view.measuredWidth, height, Bitmap.Config.ARGB_8888)
                val bigCanvas = Canvas(bigBitmap)
                val lBackground: Drawable = view.background
                if (lBackground is ColorDrawable) {
                    val lColorDrawable: ColorDrawable = lBackground
                    val lColor: Int = lColorDrawable.color
                    bigCanvas.drawColor(lColor)
                }
                for (i in 0 until size) {
                    val bitmap = bitmaCache.get(i.toString())!!
                    bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                    iHeight += bitmap.height
                    bitmap.recycle()
                }
            }
            return bigBitmap
        } catch (e: Exception) {
            MainScope().launch {
                UMCrash.generateCustomLog(e, Constants.EXCEPTION_DOWNLOAD_PIC)
            }
            return null
        }

    }
}
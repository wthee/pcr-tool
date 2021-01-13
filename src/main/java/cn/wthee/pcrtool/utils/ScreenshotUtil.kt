package cn.wthee.pcrtool.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

object ScreenshotUtil {

    fun getBitmap(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}
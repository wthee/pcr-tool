package cn.wthee.pcrtool.utils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation

class Blur(context: Context, radius: Int) : Transformation {
    protected val context: Context
    protected var blurRadius = 0

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        val sourceBitmap: Bitmap = input
        val blurredBitmap: Bitmap
        blurredBitmap = Bitmap.createBitmap(sourceBitmap)
        val renderScript: RenderScript = RenderScript.create(context)
        val input: Allocation = Allocation.createFromBitmap(
            renderScript,
            sourceBitmap,
            Allocation.MipmapControl.MIPMAP_FULL,
            Allocation.USAGE_SCRIPT
        )
        val output: Allocation = Allocation.createTyped(renderScript, input.getType())
        val script: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(
            renderScript,
            Element.U8_4(renderScript)
        )
        script.setInput(input)
        script.setRadius(blurRadius.toFloat())
        script.forEach(output)
        output.copyTo(blurredBitmap)
        input.destroy()
        return blurredBitmap
    }

    companion object {
        protected const val UP_LIMIT = 25
        protected const val LOW_LIMIT = 1
    }

    init {
        this.context = context
        if (radius < LOW_LIMIT) {
            blurRadius = LOW_LIMIT
        } else if (radius > UP_LIMIT) {
            blurRadius = UP_LIMIT
        } else blurRadius = radius
    }

    override fun key(): String {
        return "blurred"
    }

}
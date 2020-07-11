package cn.wthee.pcrtool.utils

import android.graphics.Bitmap
import androidx.palette.graphics.Palette

object PaletteHelper {

    fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
}
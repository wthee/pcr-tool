package cn.wthee.pcrtool.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.wthee.pcrtool.MyApplication

object Dimen {
    /**
     * 10dp
     */
    val cardRadius = 10.dp

    /**
     * 4dp
     */
    val cardElevation = 4.dp

    /**
     * 8dp
     */
    val fabElevation = 8.dp

    /**
     * 40dp
     */
    val fabSize = 40.dp

    /**
     * 24dp
     */
    val fabIconSize = 24.dp

    /**
     * 16dp
     */
    val fabMargin = 16.dp

    /**
     * 70dp
     */
    val fabMarginEnd = 65.dp

    /**
     * 14dp
     */
    val fabSmallMarginEnd = 9.dp

    /**
     * 6dp
     */
    val fabPadding = 6.dp

    /**
     * 3dp
     */
    val smallPadding = 3.dp

    /**
     * 6dp
     */
    val mediuPadding = 6.dp

    /**
     * 12dp
     */
    val largePadding = 12.dp

    /**
     * 18dp
     */
    val smallIconSize = 18.dp

    /**
     * 60dp
     */
    val smallMenuHeight = 60.dp

    /**
     * 130dp
     */
    val largeMenuHeight = 130.dp

    /**
     * 48dp
     */
    val iconSize = 48.dp


    /**
     * 60dp
     */
    val sheetMarginBottom = 60.dp

    /**
     * 260dp
     */
    val sheetHeight = 260.dp

    /**
     * 40dp
     */
    val iconMinSize = 40.dp

    /**
     * 45dp
     */
    val lineWidth = 45.dp

    /**
     * 3dp
     */
    val lineHeight = 3.dp

    /**
     * 1dp
     */
    val divLineHeight = 1.dp

    /**
     * 2dp
     */
    val border = 2.dp

    /**
     * 48dp
     */
    val topBarHeight = 48.dp

    /**
     * 24dp
     */
    val topBarIconSize = 24.dp

    /**
     * 28dp
     */
    val slideHeight = 28.dp

    /**
     * 26dp
     */
    val starIconSize = 26.dp

    /**
     * 根据文字大小显示
     */
    fun getWordWidth(length: Float): Dp {
        return (15 * length).sp2dp
    }
}


/**
 * sp to dp
 */
val Float.sp2dp: Dp
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        val px = this.sp.value * scale
        val dpInt = (px / scale + 0.5f).toInt()
        return dpInt.dp
    }


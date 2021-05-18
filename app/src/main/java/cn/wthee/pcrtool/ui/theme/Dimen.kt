package cn.wthee.pcrtool.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.wthee.pcrtool.MyApplication

object Dimen {

    val cardRadius = 10.dp

    val cardElevation = 4.dp

    val fabElevation = 8.dp

    val fabSize = 40.dp

    val fabIconSize = 26.dp

    val fabMargin = 16.dp

    val fabMarginEnd = 65.dp

    val fabSmallMarginEnd = 9.dp

    val fabPadding = 6.dp

    val smallPadding = 3.dp

    val mediuPadding = 6.dp

    val largePadding = 12.dp

    val smallIconSize = 18.dp

    val largeMenuHeight = 155.dp

    val iconSize = 48.dp

    val sheetMarginBottom = 60.dp

    val iconMinSize = 40.dp

    val lineHeight = 3.dp

    val divLineHeight = 1.dp

    val border = 3.dp

    val topBarHeight = 36.dp

    val topBarIconSize = 20.dp

    val slideHeight = 28.dp

    val starIconSize = 26.dp

    val cardHeight = 52.dp

    val settingIconSize = 30.dp

    val statusExHeight = 8.dp

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


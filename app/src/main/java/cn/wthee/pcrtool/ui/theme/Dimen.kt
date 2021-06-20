package cn.wthee.pcrtool.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.wthee.pcrtool.MyApplication

object Dimen {

    val cardRadius = 10.dp

    val cardElevation = 4.dp

    val fabElevation = 8.dp

    val sheetElevation = 24.dp

    val fabSize = 40.dp

    val fabIconSize = 26.dp

    val fabMargin = 16.dp

    val fabMarginEnd = 65.dp

    val fabSmallMarginEnd = 9.dp

    val smallPadding = 4.dp

    val mediuPadding = 8.dp

    val largePadding = 14.dp

    val smallIconSize = 18.dp

    val iconSize = 48.dp

    val largeIconSize = 56.dp

    val sheetMarginBottom = 60.dp

    val lineHeight = 3.dp

    val divLineHeight = 1.dp

    val border = 3.dp

    val slideHeight = 28.dp

    val starIconSize = 26.dp

    val cardHeight = 52.dp

    val settingIconSize = 30.dp

    val toolMenuWidth = 100.dp

    val menuIconSize = 30.dp
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


package cn.wthee.pcrtool.ui.theme

import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.ui.components.getItemWidth

object Dimen {

    //阴影相关
    val cardElevation = 4.dp
    val fabElevation = 6.dp
    val popupMenuElevation = 8.dp
    val textElevation = 2.dp
    val chipElevation = 4.dp


    //边距相关
    val linePadding = 1.dp
    val exSmallPadding = 3.dp
    val smallPadding = 4.dp
    val mediumPadding = 8.dp
    val largePadding = 14.dp
    val commonItemPadding = 6.dp


    /**
     * fab相关
     */
    //fab大小
    val fabSize = 40.dp

    //fab图标大小
    val fabIconSize = 26.dp

    //fab默认边距
    val fabMargin = 16.dp

    //fab右边距（考虑主按钮）
    val fabMarginEnd = fabMargin + fabSize + mediumPadding

    //fab底部边距（第二行）
    val fabMarginLargeBottom = fabMargin * 2 + fabSize

    //fab内部文字边距
    val textFabMargin = 3.dp


    /**
     * 图标相关
     */
    //图标大小（极小）
    val exSmallIconSize = 12.dp

    //图标大小（更小）
    val smallerIconSize = 14.dp

    //图标大小（小）
    val smallIconSize = 18.dp

    //文本按钮图标大小
    val textIconSize = 20.dp

    //站位图标大小（单独显示时）
    val positionIconSize = 24.dp

    //功能菜单图标大小
    val menuIconSize = 30.dp

    //图标大小（中）
    val mediumIconSize = 32.dp

    //默认图标大小
    val iconSize = 48.dp

    //图标大小（大）
    val largeIconSize = 56.dp

    //网格图标宽度
    val iconItemWidth = iconSize + commonItemPadding * 2

    //首页图标大小（装备等）
    val homeIconItemWidth = iconSize + largePadding * 2


    /**
     * 尺寸相关
     */
    //分隔线
    val divLineHeight = 1.dp

    //卡片默认大小
    val cardHeight = 52.dp

    //菜单大小
    val menuItemSize = 64.dp

    //数据切换宽度
    val dataChangeItemWidth = 120.dp

    //用于选择的fab最下宽带
    val selectFabMinWidth = 100.dp

    //技能动作item最新高度
    val skillActionItemMinHeight = 16.dp

    //fab文本最大宽带
    val fabTextMaxWidth = 100.dp

    //属性item默认宽带
    val attrItemWidth = 165.dp

    //rank选择默认宽度
    val rankTextWidth = 56.dp

    //描边宽度（大）
    val largeStrokeWidth = 4.dp

    //描边宽度
    val strokeWidth = 3.dp

    //描边宽度（小）
    val smallStrokeWidth = 2.dp

    //直线进度条默认高度
    val linearProgressHeight = 6.dp

    //指示器大小
    val indicatorSize = 7.dp

    //子项最大宽度
    val itemMaxWidth = getItemWidth() * 1.3f

}
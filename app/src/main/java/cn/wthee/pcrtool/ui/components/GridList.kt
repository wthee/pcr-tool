package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.utils.spanCount
import kotlin.math.max

/**
 * 网格列表
 *
 * @param fixColumns 固定列数
 * @param minRowHeight 最小固有尺寸
 */
@Composable
fun VerticalGridList(
    modifier: Modifier = Modifier,
    itemCount: Int,
    itemWidth: Dp,
    fixColumns: Int = 0,
    contentPadding: Dp = 0.dp,
    verticalContentPadding: Dp = contentPadding,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    minRowHeight: Boolean = true,
    content: @Composable (Int) -> Unit
) {
    val context = LocalContext.current
    var size by remember { mutableStateOf(IntSize(width = ScreenUtil.getWidth(), height = 0)) }
    //列数
    val columns = max(
        1,
        if (fixColumns != 0) {
            fixColumns
        } else {
            spanCount(
                width = size.width,
                itemDp = itemWidth + contentPadding * 2,
                context = context
            )
        }
    )


    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (fixColumns == 0) {
                    Modifier.onSizeChanged {
                        size = it
                    }
                } else {
                    Modifier
                }
            )
    ) {
        var rows = (itemCount / columns)
        if (itemCount.mod(columns) > 0) {
            rows += 1
        }

        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (minRowHeight) {
                            Modifier.height(IntrinsicSize.Min)
                        } else {
                            Modifier
                        }
                    ),
                verticalAlignment = verticalAlignment
            ) {
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(
                                horizontal = contentPadding,
                                vertical = verticalContentPadding
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < itemCount) {
                            content(index)
                        }
                    }
                }
            }
        }
    }

}


/**
 * 角色、装备图标列表
 *
 * @param idList id列表
 * @param detailIdList 调整详情用id列表
 * @param iconResourceType 图标类型
 */
@Composable
fun GridIconList(
    modifier: Modifier = Modifier,
    idList: List<Int>?,
    detailIdList: List<Int> = arrayListOf(),
    iconResourceType: IconResourceType,
    itemWidth: Dp = Dimen.iconSize,
    contentPadding: Dp = Dimen.exSmallPadding,
    fixColumns: Int = 0,
    paddingValues: PaddingValues = PaddingValues(
        top = Dimen.smallPadding,
        start = Dimen.smallPadding,
        end = Dimen.smallPadding
    ),
    onClickItem: ((Int) -> Unit)? = null
) {
    VerticalGridList(
        modifier = modifier.padding(paddingValues),
        itemCount = idList?.size ?: 0,
        itemWidth = itemWidth,
        contentPadding = contentPadding,
        verticalContentPadding = Dimen.smallPadding,
        fixColumns = fixColumns
    ) {
        if (idList != null) {
            IconItem(
                id = idList[it],
                detailId = if (detailIdList.isNotEmpty()) {
                    detailIdList[it]
                } else {
                    null
                },
                iconResourceType = iconResourceType,
                onClickItem = onClickItem
            )
        }
    }
}

/**
 * 角色、装备图标
 * @param id 0，1，2显示位置图标
 */
@Composable
fun IconItem(
    id: Int,
    detailId: Int? = null,
    iconResourceType: IconResourceType,
    onClickItem: ((Int) -> Unit)? = null
) {
    val placeholder = id == ImageRequestHelper.UNKNOWN_EQUIP_ID

    var mId: Int = id
    val url: String

    when (iconResourceType) {
        IconResourceType.CHARACTER -> {
            mId = if (id / 10000 == 3) {
                //item 转 unit
                id % 10000 * 100 + 1
            } else {
                id
            }
            url = ImageRequestHelper.getInstance().getMaxIconUrl(mId)
        }

        IconResourceType.EQUIP, IconResourceType.UNIQUE_EQUIP -> {
            url = ImageRequestHelper.getInstance().getUrl(ImageRequestHelper.ICON_EQUIPMENT, id)
        }

        IconResourceType.EX_EQUIP -> {
            url =
                ImageRequestHelper.getInstance().getUrl(ImageRequestHelper.ICON_EXTRA_EQUIPMENT, id)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainIcon(
            modifier = Modifier.placeholder(placeholder),
            data = when (id) {
                0 -> R.drawable.ic_position_0
                1 -> R.drawable.ic_position_1
                2 -> R.drawable.ic_position_2
                else -> url
            },
            onClick = if (onClickItem != null && id > 2) {
                {
                    onClickItem(detailId ?: mId)
                }
            } else {
                null
            },
            size = if (id > 2) {
                Dimen.iconSize
            } else {
                Dimen.smallIconSize
            }
        )

        if (BuildConfig.DEBUG) {
            CaptionText(text = (detailId ?: mId).toString())
        }
    }

}

/**
 * 装备、角色图标布局，带标题
 */
@Composable
fun IconListContent(
    idList: List<Int>,
    title: String,
    iconResourceType: IconResourceType,
    onClickItem: ((Int) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.smallPadding)
            .verticalScroll(rememberScrollState())
    ) {
        //标题
        MainText(
            text = title,
            modifier = Modifier
                .padding(vertical = Dimen.largePadding)
                .align(Alignment.CenterHorizontally)
        )

        //图标
        GridIconList(
            idList = idList,
            iconResourceType = iconResourceType,
            onClickItem = onClickItem
        )

        CommonSpacer()
    }
}

/**
 * 网格布局
 * @param itemWidth 子项宽度
 * @param fixCount 固定列数
 * @param contentPadding 子项间距
 */
@Composable
fun VerticalStaggeredGrid(
    modifier: Modifier = Modifier,
    itemWidth: Dp? = null,
    fixCount: Int = 0,
    contentPadding: Dp = 0.dp,
    verticalContentPadding: Dp = contentPadding,
    children: @Composable () -> Unit
) {
    val contentPaddingPx = contentPadding.value.dp2px
    val verticalContentPaddingPx = verticalContentPadding.value.dp2px
    val context = LocalContext.current

    Layout(
        content = children,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }

        //列数
        val columns = if (fixCount != 0) {
            fixCount
        } else {
            val itemPx = dp2px((itemWidth!! + contentPadding * 2).value, context)
            (constraints.maxWidth / itemPx.toDouble()).toInt()
        }
        val columnWidth = (constraints.maxWidth / max(1, columns).toFloat()).toInt()
        val itemConstraints = constraints.copy(maxWidth = columnWidth - contentPaddingPx * 2)
        val colHeights = IntArray(max(1, columns)) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height + verticalContentPaddingPx * 2
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(max(1, columns)) { 0 }
            placeables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column + contentPaddingPx,
                    y = colY[column] + verticalContentPaddingPx
                )
                colY[column] += placeable.height + verticalContentPaddingPx * 2
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}


/**
 * 获取宽度
 */
fun getItemWidth(floatWindow: Boolean = false) = if (floatWindow) {
    Dimen.mediumIconSize + Dimen.mediumPadding * 2
} else {
    Dimen.iconSize + Dimen.mediumPadding * 2
} * 5


@CombinedPreviews
@Composable
fun IconListContentPreview() {
    val mockData = arrayListOf<Int>()
    for (i in 0..10) {
        mockData.add(i)
    }
    PreviewLayout {
        IconListContent(
            idList = mockData,
            title = stringResource(id = R.string.debug_short_text),
            iconResourceType = IconResourceType.CHARACTER,
            onClickItem = {}
        )
    }
}

package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.dp2px
import kotlin.math.max


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
    fixCount: Int = 0,
    paddingValues: PaddingValues = PaddingValues(
        top = Dimen.smallPadding,
        start = Dimen.smallPadding,
        end = Dimen.smallPadding
    ),
    onClickItem: ((Int) -> Unit)? = null
) {
    VerticalStaggeredGrid(
        modifier = modifier.padding(paddingValues),
        itemWidth = itemWidth,
        contentPadding = contentPadding,
        verticalContentPadding = Dimen.smallPadding,
        fixCount = if (LocalInspectionMode.current) 5 else fixCount
    ) {
        idList?.forEachIndexed { index, it ->
            IconItem(
                id = it,
                detailId = if (detailIdList.isNotEmpty()) {
                    detailIdList[index]
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
            data = url,
            onClick = if (onClickItem != null) {
                {
                    onClickItem(detailId ?: mId)
                }
            } else {
                null
            }
        )
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

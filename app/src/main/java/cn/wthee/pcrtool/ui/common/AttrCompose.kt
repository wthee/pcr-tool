package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.utils.int

/**
 * 属性列表
 */
@Composable
fun AttrList(attrs: List<AttrValue>, toInt: Boolean = true) {
    VerticalGrid(
        modifier = Modifier.padding(horizontal = Dimen.mediumPadding),
        spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px * 2
    ) {
        attrs.forEach {
            AttrItem(it.title, it.value, toInt)
        }
    }
}

/**
 * 属性
 */
@Composable
fun AttrItem(text: String, value: Double, toInt: Boolean) {
    val valueText = when (value.int) {
        in 100000000..Int.MAX_VALUE -> "${value.toInt() / 100000000f}亿"
        in 100000 until 100000000 -> "${value.toInt() / 10000}万"
        else -> if (toInt) value.int.toString() else value.toString()
    }
    Row(
        modifier = Modifier.padding(
            top = Dimen.smallPadding,
            start = Dimen.mediumPadding,
            end = Dimen.mediumPadding
        )
    ) {
        MainTitleText(
            text = text, modifier = Modifier
                .weight(0.3f)
        )
        MainContentText(
            text = valueText,
            modifier = Modifier
                .weight(0.2f)
        )
    }
}


/**
 * 属性列表
 */
@Preview
@Composable
private fun AttrListPreview() {
    val mockData = arrayListOf(AttrValue(), AttrValue(), AttrValue())
    PreviewBox {
        AttrList(attrs = mockData)
    }
}
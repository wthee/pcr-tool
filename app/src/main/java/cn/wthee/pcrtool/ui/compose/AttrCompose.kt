package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.int

/**
 * 属性列表
 */
@Composable
fun AttrList(attrs: List<AttrValue>, toInt: Boolean = true) {
    Column {
        attrs.forEachIndexed { index, it ->
            if (index % 2 == 0) {
                Row {
                    AttrItem(index, it.title, it.value, toInt, Modifier.weight(0.5f))
                    if (index + 1 < attrs.size) {
                        AttrItem(
                            index + 1,
                            attrs[index + 1].title,
                            attrs[index + 1].value,
                            toInt,
                            Modifier.weight(0.5f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(0.5f))
                    }
                }
            }
        }
    }
}

/**
 * 属性
 */
@Composable
fun AttrItem(index: Int, text: String, value: Double, toInt: Boolean, modifier: Modifier) {
    val valueText = when (value.int) {
        in 100000000..Int.MAX_VALUE -> "${value.toInt() / 100000000f}亿"
        in 100000 until 100000000 -> "${value.toInt() / 10000}万"
        else -> if (toInt) value.int.toString() else value.toString()
    }
    Row(
        modifier = modifier.padding(top = Dimen.smallPadding)
    ) {
        MainTitleText(
            text = text, modifier = Modifier
                .padding(start = Dimen.largePadding)
                .weight(0.3f)
        )
        MainContentText(
            text = valueText,
            modifier = Modifier
                .padding(end = if (index % 2 == 1) Dimen.largePadding else 0.dp)
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
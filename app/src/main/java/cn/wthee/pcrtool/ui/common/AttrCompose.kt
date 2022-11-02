package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.data.enums.AttrValueType
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.utils.int


/**
 * 属性列表
 */
@Composable
fun AttrList(attrs: List<AttrValue>, attrValueType: AttrValueType = AttrValueType.INT) {
    VerticalGrid(
        modifier = Modifier.padding(horizontal = Dimen.mediumPadding),
        spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px * 2
    ) {
        attrs.forEach {
            AttrItem(it, attrValueType)
        }
    }
}

/**
 * 属性
 */
@Composable
fun AttrItem(attrValue: AttrValue, attrValueType: AttrValueType) {
    val valueText = fixedAttrValueText(attrValue.value, attrValueType)
    Row(
        modifier = Modifier.padding(
            top = Dimen.smallPadding,
            start = Dimen.commonItemPadding,
            end = Dimen.commonItemPadding
        )
    ) {
        MainTitleText(
            text = attrValue.title, modifier = Modifier
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
 * 属性对比
 * @param isExtraEquip 是否为ex装备
 */
@Composable
fun AttrCompare(
    compareData: List<AttrCompareData>,
    isExtraEquip: Boolean,
    attrValueType: AttrValueType
) {

    Column(
        modifier = if (!isExtraEquip) {
            Modifier
                .padding(Dimen.mediumPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        } else {
            Modifier
                .padding(Dimen.mediumPadding)
                .fillMaxWidth()
        }
    ) {
        compareData.forEach {
            Row(modifier = Modifier.padding(Dimen.smallPadding)) {
                MainTitleText(
                    text = it.title,
                    modifier = Modifier.weight(0.3f)
                )
                MainContentText(
                    text = fixedAttrValueText(it.attr0, attrValueType),
                    modifier = Modifier.weight(0.2f)
                )
                MainContentText(
                    text = fixedAttrValueText(it.attr1, attrValueType),
                    modifier = Modifier.weight(0.2f)
                )
                if (!isExtraEquip) {
                    val color = when {
                        it.attrCompare.int > 0 -> colorGreen
                        it.attrCompare.int < 0 -> colorRed
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    Text(
                        text = fixedAttrValueText(it.attrCompare, attrValueType),
                        color = color,
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.2f)
                    )
                }
            }
        }
        if (!isExtraEquip) {
            CommonSpacer()
        }
    }
}

/**
 * 处理属性数值格式
 */
fun fixedAttrValueText(attrValue: Double, attrValueType: AttrValueType) =
    when (attrValue.int) {
        in 100000000..Int.MAX_VALUE -> "${attrValue.toInt() / 100000000f}亿"
        in 100000 until 100000000 -> "${attrValue.toInt() / 10000}万"
        else -> {
            when (attrValueType) {
                AttrValueType.INT -> attrValue.int.toString()
                AttrValueType.DOUBLE -> attrValue.toString()
                AttrValueType.PERCENT -> (attrValue / 100).toString() + "%"
            }
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
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.AttrValueType
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.theme.*
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
        spanCount = if (LocalInspectionMode.current) {
            2
        } else {
            ScreenUtil.getWidth() / getItemWidth().value.dp2px * 2
        }
    ) {
        attrs.forEach { attr ->
            val valueText = fixedAttrValueText(attr.value, attrValueType)
            CommonTitleContentText(attr.title, valueText)
        }
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
@Composable
fun fixedAttrValueText(attrValue: Double, attrValueType: AttrValueType) =
    when (attrValue.int) {
        in 100000000..Int.MAX_VALUE -> {
            stringResource(R.string.hp_100_m, (attrValue.toInt() / 100000000f).toString())
        }
        in 100000 until 100000000 -> {
            stringResource(R.string.hp_10_k, attrValue.toInt() / 10000)
        }
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
@CombinedPreviews
@Composable
private fun AttrListPreview() {
    val mockData = arrayListOf(AttrValue(), AttrValue(), AttrValue())
    PreviewLayout {
        AttrList(attrs = mockData)
    }
}
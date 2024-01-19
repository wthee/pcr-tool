package cn.wthee.pcrtool.ui.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.AttrValueType
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.int


/**
 * 属性列表
 */
@Composable
fun AttrList(
    attrs: List<AttrValue>,
    attrValueType: AttrValueType = AttrValueType.INT,
    itemWidth: Dp = Dimen.attrWidth
) {
    VerticalStaggeredGrid(
        modifier = Modifier.padding(horizontal = Dimen.commonItemPadding),
        itemWidth = itemWidth
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
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .fillMaxWidth()
            .then(
                if (!isExtraEquip) {
                    Modifier
                        .verticalScroll(rememberScrollState())
                } else {
                    Modifier
                }
            )
    ) {
        compareData.forEach {
            Row(modifier = Modifier.padding(Dimen.smallPadding)) {
                MainTitleText(
                    text = it.title,
                    modifier = Modifier.weight(0.3f)
                )
                MainContentText(
                    text = fixedAttrValueText(it.attr0, attrValueType, it.title),
                    modifier = Modifier.weight(0.2f)
                )
                MainContentText(
                    text = fixedAttrValueText(it.attr1, attrValueType, it.title),
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
        //rank对比时
        if (!isExtraEquip) {
            CommonSpacer()
            CommonSpacer()
        }
    }
}

/**
 * 处理属性数值格式
 * @param title 属性名称，用于判断ex装备非百分比提升属性判断
 */
@Composable
fun fixedAttrValueText(attrValue: Double, attrValueType: AttrValueType, title: String = "") =
    when (attrValue.int) {
        in 100000000..Int.MAX_VALUE -> {
            stringResource(R.string.value_100_m, (attrValue.toInt() / 100000000f).toString())
        }

        in 1000000 until 100000000 -> {
            stringResource(R.string.value_10_k, attrValue.toInt() / 10000)
        }

        else -> {
            when (attrValueType) {
                AttrValueType.INT -> attrValue.int.toString()
                AttrValueType.DOUBLE -> attrValue.toString()
                //ex装备用
                AttrValueType.PERCENT -> {
                    val extraEquipAttrPercent = arrayListOf(
                        stringResource(id = R.string.attr_hp),
                        stringResource(id = R.string.attr_atk),
                        stringResource(id = R.string.attr_magic_str),
                        stringResource(id = R.string.attr_def),
                        stringResource(id = R.string.attr_magic_def),
                        stringResource(id = R.string.attr_physical_critical),
                        stringResource(id = R.string.attr_magic_critical),
                    )
                    if (extraEquipAttrPercent.contains(title)) {
                        (attrValue / 100).toString() + "%"
                    } else {
                        attrValue.toInt().toString()
                    }
                }
            }
        }
    }


/**
 * 属性列表
 */
@CombinedPreviews
@Composable
private fun AttrListPreview() {
    val mockData = arrayListOf(
        AttrValue(stringResource(id = R.string.attr_atk), 23456.0),
        AttrValue(stringResource(id = R.string.attr_magic_def), 23456.0),
        AttrValue(stringResource(id = R.string.attr_def), 23456.0),
    )
    PreviewLayout {
        AttrList(attrs = mockData)
    }
}
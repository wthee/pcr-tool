package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.int

/**
 * 属性列表
 */
@Composable
fun AttrList(attrs: List<AttrValue>) {
    Column {
        attrs.forEachIndexed { index, it ->
            if (index % 2 == 0) {
                Row {
                    AttrItem(it.title, it.value.int, Modifier.weight(0.5f))
                    if (index + 1 < attrs.size) {
                        AttrItem(
                            attrs[index + 1].title,
                            attrs[index + 1].value.int,
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
fun AttrItem(text: String, value: Int, modifier: Modifier) {
    Row(modifier = modifier) {
        MainTitleText(
            text = text, modifier = Modifier
                .padding(Dimen.smallPadding)
                .weight(0.3f)
        )
        MainContentText(
            text = value.toString(),
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .weight(0.2f)
        )
    }
}
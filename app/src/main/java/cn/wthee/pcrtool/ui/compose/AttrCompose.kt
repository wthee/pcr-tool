package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.int

/**
 * 属性列表
 */
@ExperimentalFoundationApi
@Composable
fun AttrList(attrs: List<AttrValue>) {
    LazyVerticalGrid(cells = GridCells.Fixed(2)) {
        items(attrs) { attr ->
            AttrItem(attr.title, attr.value.int)
        }
    }
}

/**
 * 属性
 */
@Composable
fun AttrItem(text: String, value: Int) {
    Row {
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
package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants

/**
 * 角色图标列表
 */
@Composable
fun IconListCompose(
    icons: List<Int>,
    texts: List<String>? = null,
    toCharacterDetail: (Int) -> Unit
) {
    VerticalGrid(maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2) {
        icons.forEachIndexed { index, it ->
            val unitId: Int
            val iconId: Int
            if (it / 10000 == 3) {
                //item 转 unit
                iconId = it % 10000 * 100 + 11
                unitId = it % 10000 * 100 + 1
            } else {
                iconId = it + 30
                unitId = it
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        top = Dimen.mediumPadding
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCompose(
                    data = Constants.UNIT_ICON_URL + iconId + Constants.WEBP
                ) {
                    toCharacterDetail(unitId)
                }
                texts?.let { text ->
                    SelectionContainer {
                        Text(
                            text = text[index],
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun IconListComposePreview() {
    val mockData = arrayListOf<Int>()
    for (i in 0..10) {
        mockData.add(i)
    }
    PreviewBox {
        IconListCompose(icons = mockData, toCharacterDetail = {})
    }
}
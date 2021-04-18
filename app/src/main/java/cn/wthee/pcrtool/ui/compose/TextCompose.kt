package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes

/**
 * 蓝底白字
 */
@Composable
fun MainTitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.onPrimary,
        style = MaterialTheme.typography.body2,
        modifier = modifier
            .background(color = MaterialTheme.colors.primary, shape = Shapes.small)
            .padding(start = Dimen.mediuPadding, end = Dimen.mediuPadding)
    )
}

/**
 * 蓝底白字
 */
@Composable
fun MainContentText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.End
) {
    Text(
        text = text,
        color = MaterialTheme.colors.onBackground,
        textAlign = textAlign,
        style = MaterialTheme.typography.body2,
        modifier = modifier
    )
}
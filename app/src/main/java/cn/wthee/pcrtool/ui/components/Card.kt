package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.VibrateUtil


/**
 * 卡片布局
 * @param onClick 自带点击振动
 */
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    fillMaxWidth: Boolean = true,
    elevation: Dp = Dimen.cardElevation,
    content: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .then(
                if (fillMaxWidth) {
                    Modifier
                        .fillMaxWidth()
                } else {
                    Modifier
                }
            )
            .shadow(elevation, shape, true)
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        VibrateUtil(context).single()
                        onClick()
                    }
                } else {
                    Modifier
                }
            ),
        content = content,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    )
}


@CombinedPreviews
@Composable
private fun MainCardPreview() {
    PreviewLayout {
        MainCard {
            Spacer(modifier = Modifier.size(Dimen.cardHeight))
        }
    }
}
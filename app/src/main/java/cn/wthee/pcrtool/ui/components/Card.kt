package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
@OptIn(ExperimentalMaterial3Api::class)
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
    val mModifier = modifier
        .then(
            if (fillMaxWidth) {
                Modifier
                    .fillMaxWidth()
            } else {
                Modifier
            }
        )
    //阴影
    val cardElevation = CardDefaults.elevatedCardElevation(
        defaultElevation = elevation
    )

    if (onClick != null) {
        ElevatedCard(
            modifier = mModifier,
            onClick = {
                VibrateUtil(context).single()
                onClick()
            },
            content = content,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = cardElevation
        )
    } else {
        ElevatedCard(
            modifier = mModifier,
            content = content,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = cardElevation
        )
    }
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
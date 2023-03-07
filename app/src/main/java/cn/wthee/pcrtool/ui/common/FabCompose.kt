package cn.wthee.pcrtool.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.utils.VibrateUtil

/**
 * 通用悬浮按钮
 *
 * @param hasNavBarPadding 适配导航栏
 * @param extraContent 不为空时，将替换text内容
 */
@Composable
fun FabCompose(
    iconType: Any,
    modifier: Modifier = Modifier,
    text: String = "",
    hasNavBarPadding: Boolean = true,
    extraContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var mModifier = if (hasNavBarPadding) {
        modifier.navigationBarsPadding()
    } else {
        modifier
    }
    val isTextFab = text != "" && extraContent == null

    if (isTextFab) {
        mModifier = mModifier.padding(horizontal = Dimen.textfabMargin)
    }

    SmallFloatingActionButton(
        onClick = {
            VibrateUtil(context).single()
            onClick()
        },
        shape = CircleShape,
        modifier = mModifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = (if (isTextFab) {
                Modifier.padding(start = Dimen.largePadding)
            } else {
                Modifier.padding(start = 0.dp)
            }).animateContentSize(defaultTween())
        ) {

            if (extraContent == null) {
                IconCompose(
                    data = iconType,
                    size = Dimen.fabIconSize,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    modifier = if (isTextFab) {
                        Modifier.padding(start = Dimen.mediumPadding, end = Dimen.largePadding)
                    } else {
                        Modifier
                    }
                        .widthIn(max = Dimen.fabTextMaxWidth),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                extraContent()
            }
        }
    }

}

@CombinedPreviews
@Composable
private fun FabComposePreview() {
    PreviewLayout {
        Row {
            FabCompose(iconType = MainIconType.ANIMATION) {

            }
            FabCompose(iconType = MainIconType.ANIMATION, text = "fab") {

            }
        }
    }
}

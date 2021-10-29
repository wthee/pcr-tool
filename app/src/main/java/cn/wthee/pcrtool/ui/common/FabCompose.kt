package cn.wthee.pcrtool.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.accompanist.insets.navigationBarsPadding

/**
 * 通用悬浮按钮
 */
@Composable
fun FabCompose(
    iconType: MainIconType,
    modifier: Modifier = Modifier,
    text: String = "",
    defaultPadding: Boolean = true,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val mModifier = if (defaultPadding) {
        modifier.navigationBarsPadding()
    } else {
        modifier
    }

    FloatingActionButton(
        onClick = {
            VibrateUtil(context).single()
            onClick.invoke()
        },
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = mModifier.defaultMinSize(minWidth = Dimen.fabSize, minHeight = Dimen.fabSize),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = if (text != "") {
                Modifier.padding(start = Dimen.largePadding)
            } else {
                Modifier
            }
        ) {
            IconCompose(iconType.icon, size = Dimen.fabIconSize)
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = if (text != "") {
                    Modifier.padding(start = Dimen.mediumPadding, end = Dimen.largePadding)
                } else {
                    Modifier
                }.animateContentSize(defaultTween())
            )
        }
    }

}

@Preview
@Composable
private fun FabComposePreview() {
    PreviewBox {
        Row {
            FabCompose(iconType = MainIconType.ANIMATION) {

            }
            FabCompose(iconType = MainIconType.ANIMATION, text = "fab") {

            }
        }
    }
}

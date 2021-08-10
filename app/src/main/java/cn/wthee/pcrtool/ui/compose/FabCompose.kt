package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.navigationBarsPadding

/**
 * 通用悬浮按钮
 */
@ExperimentalCoilApi
@ExperimentalAnimationApi
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
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.primary,
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
                style = MaterialTheme.typography.subtitle2,
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

@ExperimentalCoilApi
@ExperimentalAnimationApi
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

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
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.vibrate
import com.google.accompanist.insets.navigationBarsPadding

/**
 * 通用悬浮按钮
 */
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
        onClick = onClick.vibrate {
            VibrateUtil(context).single()
        },
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
        modifier = mModifier.defaultMinSize(minWidth = Dimen.fabSize, minHeight = Dimen.fabSize),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = if (text != "") {
                Modifier.padding(start = Dimen.fabPadding)
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
                    Modifier.padding(start = Dimen.smallPadding, end = Dimen.mediuPadding)
                } else {
                    Modifier
                }.animateContentSize(defaultTween())
            )
        }
    }

}

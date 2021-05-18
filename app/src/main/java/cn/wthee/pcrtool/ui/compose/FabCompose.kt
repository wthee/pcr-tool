package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    val fabAnim = animateFloatAsState(targetValue = if (text == "") 0f else 1f)
    var mModifier = modifier
    if (defaultPadding) {
        mModifier = modifier.navigationBarsPadding()
    }
    mModifier = if (text != "") {
        mModifier.height(Dimen.fabSize)
    } else {
        mModifier.size(Dimen.fabSize)
    }

    FloatingActionButton(
        onClick = onClick.vibrate {
            VibrateUtil(context).single()
        },
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
        modifier = mModifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Dimen.fabPadding)
        ) {
            IconCompose(iconType.icon)
            ExtendedAnimation(visible = fabAnim.value == 1f) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(end = Dimen.fabPadding)
                )
            }
        }
    }
}

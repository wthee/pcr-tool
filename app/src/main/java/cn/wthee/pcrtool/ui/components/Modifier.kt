package cn.wthee.pcrtool.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.ui.theme.maskAlpha
import cn.wthee.pcrtool.ui.theme.maskStartAlpha
import cn.wthee.pcrtool.utils.VibrateUtil

/**
 * 点击组件之外内容关闭
 *
 */
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.clickClose(
    openDialog: Boolean,
    onClose: (()->Unit)? = null
): Modifier = composed {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    //背景遮罩
    val color = animateColorAsState(
        targetValue = if (openDialog) {
            MaterialTheme.colorScheme.surface.copy(alpha = maskAlpha)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = maskStartAlpha)
        },
        animationSpec = defaultTween(),
        label = "background mask"
    )

    if (openDialog) {
        Modifier
            .fillMaxSize()
            .background(color.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        VibrateUtil(context).single()
                        keyboardController?.hide()
                        if (onClose != null) {
                            onClose()
                        }
                    }
                )
            }
    } else {
        Modifier
    }
}
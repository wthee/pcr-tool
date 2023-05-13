package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

/**
 * 点击组件之外内容关闭
 */
fun Modifier.clickClose(
    openDialog: Boolean,
): Modifier = composed {
    val context = LocalContext.current
    if (openDialog) {
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        VibrateUtil(context).single()
                        navViewModel.fabCloseClick.postValue(true)
                    }
                )
            }
    } else {
        Modifier.fillMaxSize()
    }
}

/**
 * 通用 placeholder
 */
fun Modifier.commonPlaceholder(visible: Boolean): Modifier = composed {
    Modifier.placeholder(
        visible = visible,
        highlight = PlaceholderHighlight.shimmer()
    )
}
package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.theme.colorAlphaBlack
import cn.wthee.pcrtool.ui.theme.colorAlphaWhite
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

/**
 * 点击组件之外内容关闭
 *
 * @param isSettingPop 是否为设置弹出菜单
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.clickClose(
    openDialog: Boolean,
    isSettingPop: Boolean = false,
    onClose: (()->Unit)? = null
): Modifier = composed {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (openDialog) {
        Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        VibrateUtil(context).single()
                        if (isSettingPop) {
//                            navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
                        } else {
                            keyboardController?.hide()
                            navViewModel.fabCloseClick.postValue(true)
                        }
                        if (onClose != null) {
                            onClose()
                        }
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
@Suppress("DEPRECATION")
fun Modifier.commonPlaceholder(visible: Boolean): Modifier = composed {
    Modifier.placeholder(
        visible = visible,
        highlight = PlaceholderHighlight.shimmer()
    )
}
package cn.wthee.pcrtool.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.VibrateUtil


/**
 * 通用弹窗
 */
@Composable
fun MainAlertDialog(
    modifier: Modifier = Modifier,
    openDialog: MutableState<Boolean>,
    icon: MainIconType? = null,
    title: String? = null,
    text: String? = null,
    content: (@Composable () -> Unit)? = null,
    confirmText: String = stringResource(R.string.confirm),
    dismissText: String = stringResource(id = R.string.cancel),
    showButton: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
) {
    if (openDialog.value) {
        val context = LocalContext.current

        AlertDialog(
            modifier = modifier,
            icon = if (icon == null) {
                null
            } else {
                {
                    MainIcon(data = icon, wrapSize = true)
                }
            },
            title = if (title == null) {
                null
            } else {
                {
                    Text(text = title)
                }
            },
            text = {
                if (text != null) {
                    Text(text = text)
                }
                if (content != null) {
                    content()
                }
            },
            onDismissRequest = {
                if (onDismissRequest != null) {
                    onDismissRequest()
                }
                openDialog.value = false
            },
            confirmButton = {
                if (showButton) {
                    TextButton(
                        onClick = {
                            VibrateUtil(context).single()
                            if (onConfirm != null) {
                                onConfirm()
                            }
                            openDialog.value = false
                        }
                    ) {
                        Text(text = confirmText)
                    }
                }
            },
            dismissButton = {
                if (showButton) {
                    TextButton(
                        onClick = {
                            VibrateUtil(context).single()
                            if (onDismissRequest != null) {
                                onDismissRequest()
                            }
                            openDialog.value = false
                        }
                    ) {
                        Text(text = dismissText)
                    }
                }
            }
        )
    }

}

@CombinedPreviews
@Composable
private fun MainAlertDialogPreview() {
    val openDialog = remember {
        mutableStateOf(true)
    }

    PreviewLayout(themeType = 1) {
        MainAlertDialog(
            openDialog = openDialog, icon = MainIconType.DOWNLOAD,
            title = stringResource(id = R.string.debug_short_text),
            text = stringResource(id = R.string.debug_long_text)
        ) {

        }
    }
}

@CombinedPreviews
@Composable
private fun MainAlertDialog2Preview() {
    val openDialog = remember {
        mutableStateOf(true)
    }

    PreviewLayout(themeType = 2) {
        MainAlertDialog(
            openDialog = openDialog, icon = MainIconType.DOWNLOAD,
            title = stringResource(id = R.string.debug_short_text),
            text = stringResource(id = R.string.debug_long_text)
        ) {

        }
    }
}
package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.deleteSpace

/**
 * 等级输入框
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.LevelInputText(
    text: String,
    onDone: (Int) -> Unit,
    minLevel: Int = 1,
    maxLevel: Int,
    placeholder: String = stringResource(id = R.string.input_level)
) {
    val context = LocalContext.current
    //等级
    val inputLevel = remember {
        mutableStateOf("")
    }
    //输入框管理
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    //输入完成
    var done by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(done) {
        if (done) {
            keyboardController?.hide()
            focusManager.clearFocus()
        } else {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    //键盘可见
    val isImeVisible = WindowInsets.isImeVisible
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            done = true
        }
    }

    //等级
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(0.3f)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                VibrateUtil(context).single()
                done = !done
            }
            .padding(vertical = Dimen.mediumPadding)
    )

    //等级输入框
    ExpandAnimation(!done) {
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = Dimen.smallPadding)
                .focusRequester(focusRequester),
            value = inputLevel.value,
            onValueChange = {
                var filterStr = ""
                it.deleteSpace.forEach { ch ->
                    if (Regex("\\d").matches(ch.toString())) {
                        filterStr += ch
                    }
                }
                inputLevel.value = when {
                    filterStr == "" -> ""
                    filterStr.toInt() < minLevel -> minLevel.toString()
                    filterStr.toInt() in minLevel..maxLevel -> filterStr
                    else -> maxLevel.toString()
                }
            },
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = {
                MainIcon(
                    data = MainIconType.OK,
                    size = Dimen.fabIconSize,
                    onClick = {
                        done = true
                        if (inputLevel.value != "") {
                            onDone(inputLevel.value.toInt())
                        }
                    }
                )
            },
            placeholder = {
                Text(
                    text = placeholder
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    done = true
                    if (inputLevel.value != "") {
                        onDone(inputLevel.value.toInt())
                    }
                }
            )
        )
    }
}

/**
 * 通用输入框
 *
 * @param hasImePadding 是否适配键盘边距
 */
@Composable
fun MainInputText(
    modifier: Modifier = Modifier,
    textState: MutableState<String>,
    onDone: (String) -> Unit,
    leadingIcon: MainIconType? = null,
    trailingIcon: MainIconType = MainIconType.SEARCH,
    label: String? = null,
    placeholder: String? = null,
    hasImePadding: Boolean = false
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = Dimen.itemMaxWidth)
            .then(
                if (hasImePadding) {
                    Modifier.imePadding()
                } else {
                    Modifier
                }
            ),
        value = textState.value,
        shape = MaterialTheme.shapes.medium,
        onValueChange = { textState.value = it.deleteSpace },
        textStyle = MaterialTheme.typography.labelLarge,
        leadingIcon = if (leadingIcon != null) {
            {
                MainIcon(
                    data = leadingIcon,
                    size = Dimen.fabIconSize
                )
            }
        } else {
            null
        },
        trailingIcon = {
            MainIcon(
                data = trailingIcon,
                size = Dimen.fabIconSize,
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (textState.value != "") {
                        onDone(textState.value)
                    }
                }
            )
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                if (textState.value != "") {
                    onDone(textState.value)
                }
            }
        ),
        maxLines = 1,
        singleLine = true,
        label = if (label != null) {
            {
                Text(
                    text = label
                )
            }
        } else {
            null
        },
        placeholder = if (placeholder != null) {
            {
                Text(
                    text = placeholder
                )
            }
        } else {
            null
        },
    )
}
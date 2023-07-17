package cn.wthee.pcrtool.ui.tool.uniqueequip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.deleteSpace


/**
 * 专武信息
 * @param currentValueState 当前属性
 * @param uniqueEquipLevelMax 等级
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@OptIn(
    ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun UniqueEquip(
    currentValueState: MutableState<CharacterProperty>,
    uniqueEquipLevelMax: Int,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
) {
    val inputLevel = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible
    val context = LocalContext.current

    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier.padding(top = Dimen.largePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //名称
            MainText(
                text = it.equipmentName, selectable = true
            )
            //专武等级
            Text(text = currentValueState.value.uniqueEquipmentLevel.toString(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(Dimen.mediumPadding)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        VibrateUtil(context).single()
                        if (isImeVisible) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        } else {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    })
            //等级输入框
            OutlinedTextField(
                value = inputLevel.value,
                shape = MaterialTheme.shapes.medium,
                onValueChange = {
                    var filterStr = ""
                    it.deleteSpace.forEach { ch ->
                        if (Regex("\\d").matches(ch.toString())) {
                            filterStr += ch
                        }
                    }
                    inputLevel.value = when {
                        filterStr == "" -> ""
                        filterStr.toInt() < 1 -> "1"
                        filterStr.toInt() in 1..uniqueEquipLevelMax -> filterStr
                        else -> uniqueEquipLevelMax.toString()
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = {
                    MainIcon(
                        data = MainIconType.OK, size = Dimen.fabIconSize
                    ) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (inputLevel.value != "") {
                            currentValueState.value = currentValueState.value.update(
                                uniqueEquipmentLevel = inputLevel.value.toInt()
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (inputLevel.value != "") {
                        currentValueState.value = currentValueState.value.update(
                            uniqueEquipmentLevel = inputLevel.value.toInt()
                        )
                    }
                }),
                modifier = if (isImeVisible) {
                    Modifier
                        .focusRequester(focusRequester)
                        .padding(Dimen.smallPadding)
                } else {
                    Modifier
                        .focusRequester(focusRequester)
                        .height(1.dp)
                        .alpha(0f)
                },
                maxLines = 1,
                singleLine = true
            )
            //图标描述
            Row(
                modifier = Modifier
                    .padding(
                        start = Dimen.largePadding,
                        end = Dimen.largePadding,
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth()
            ) {
                MainIcon(
                    data = ImageRequestHelper.getInstance().getEquipPic(it.equipmentId)
                )
                Subtitle2(
                    text = it.getDesc(),
                    modifier = Modifier.padding(start = Dimen.mediumPadding),
                    selectable = true
                )
            }
            //技能等级超过tp限制等级的，添加标识
            if (uniqueEquipmentMaxData.isTpLimitAction) {
                IconTextButton(
                    icon = MainIconType.HELP,
                    text = stringResource(R.string.tp_limit_level_action_desc)
                )
            }
            //属性
            AttrList(attrs = it.attr.allNotZero(isPreview = LocalInspectionMode.current))
        }
    }

}


@CombinedPreviews
@Composable
private fun UniqueEquipPreview() {
    val currentValueState = remember {
        mutableStateOf(CharacterProperty())
    }
    PreviewLayout {
        UniqueEquip(
            currentValueState = currentValueState,
            uniqueEquipLevelMax = 100,
            uniqueEquipmentMaxData = UniqueEquipmentMaxData(
                equipmentName = stringResource(id = R.string.debug_short_text),
                description = stringResource(id = R.string.debug_long_text),
                attr = Attr().random()
            )
        )
    }
}
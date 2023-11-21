package cn.wthee.pcrtool.ui.tool.uniqueequip

import androidx.compose.animation.animateContentSize
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
import cn.wthee.pcrtool.data.db.view.getIndex
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
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.deleteSpace


/**
 * 专武信息
 * @param slot 装备槽 1、 2
 * @param currentValue 当前属性
 * @param uniqueEquipLevelMax 等级
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@OptIn(
    ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun UniqueEquipContent(
    slot: Int,
    currentValue: CharacterProperty,
    uniqueEquipLevelMax: Int,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
    updateCurrentValue: ((CharacterProperty) -> Unit)
) {
    val inputLevel = remember(uniqueEquipLevelMax) {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember(uniqueEquipLevelMax) { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible
    val context = LocalContext.current
    val minLv = if (slot == 1) 1 else 0


    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier.padding(top = Dimen.largePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //名称
            MainText(
                text = it.equipmentName,
                selectable = true
            )
            //专武等级
            Text(
                text = if (slot == 1) {
                    currentValue.uniqueEquipmentLevel.toString()
                } else {
                    currentValue.uniqueEquipmentLevel2.toString()
                },
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
                        filterStr.toInt() < minLv -> minLv.toString()
                        filterStr.toInt() in minLv..uniqueEquipLevelMax -> filterStr
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
                            if (slot == 1) {
                                updateCurrentValue(currentValue.copy(uniqueEquipmentLevel = inputLevel.value.toInt()))
                            } else {
                                updateCurrentValue(currentValue.copy(uniqueEquipmentLevel2 = inputLevel.value.toInt()))
                            }
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
                        if (slot == 1) {
                            updateCurrentValue(currentValue.copy(uniqueEquipmentLevel = inputLevel.value.toInt()))
                        } else {
                            updateCurrentValue(currentValue.copy(uniqueEquipmentLevel2 = inputLevel.value.toInt()))
                        }
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
                }.animateContentSize(defaultTween()),
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
                    text = getIndex(it.equipmentId % 10) + it.description.deleteSpace,
                    modifier = Modifier.padding(start = Dimen.mediumPadding),
                    selectable = true
                )
            }
            //等级超过tp限制等级的，添加标识
            if (uniqueEquipmentMaxData.isTpLimitAction) {
                IconTextButton(
                    icon = MainIconType.INFO,
                    text = stringResource(R.string.tp_limit_level_action_desc)
                )
            }
            //等级超过限制等级的，添加标识
            if (uniqueEquipmentMaxData.isOtherLimitAction) {
                IconTextButton(
                    icon = MainIconType.INFO,
                    text = stringResource(R.string.other_limit_level_action_desc)
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
    PreviewLayout {
        UniqueEquipContent(
            1,
            currentValue = CharacterProperty(),
            uniqueEquipLevelMax = 100,
            uniqueEquipmentMaxData = UniqueEquipmentMaxData(
                equipmentName = stringResource(id = R.string.debug_short_text),
                description = stringResource(id = R.string.debug_long_text),
                attr = Attr().random()
            ),
            updateCurrentValue = {}
        )
    }
}
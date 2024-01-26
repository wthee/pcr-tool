package cn.wthee.pcrtool.ui.tool.uniqueequip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.db.view.getIndex
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LevelInputText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.deleteSpace


/**
 * 专武信息
 * @param slot 装备槽 1、 2
 * @param currentValue 当前属性
 * @param uniqueEquipLevelMax 等级
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@Composable
fun UniqueEquipDetail(
    slot: Int,
    currentValue: CharacterProperty,
    uniqueEquipLevelMax: Int,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
    updateCurrentValue: ((CharacterProperty) -> Unit)
) {
    val context = LocalContext.current


    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier.padding(top = Dimen.largePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //装备名称
            MainText(
                text = it.equipmentName,
                selectable = true
            )

            //装备等级输入框
            LevelInputText(
                text = if (slot == 1) {
                    currentValue.uniqueEquipmentLevel.toString()
                } else {
                    currentValue.uniqueEquipmentLevel2.toString()
                },
                onDone = {
                    if (slot == 1) {
                        updateCurrentValue(currentValue.copy(uniqueEquipmentLevel = it))
                    } else {
                        updateCurrentValue(currentValue.copy(uniqueEquipmentLevel2 = it))
                    }
                },
                minLevel = if (slot == 1) 1 else 0,
                maxLevel = uniqueEquipLevelMax
            )

            //装备图标描述
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
                    data = ImageRequestHelper.getInstance()
                        .getUrl(ImageRequestHelper.ICON_EQUIPMENT, it.equipmentId)
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
                    text = stringResource(R.string.other_limit_level_action_desc_unique)
                )
            }
            //属性
            AttrList(attrs = it.attr.allNotZero(context))
        }
    }

}


@CombinedPreviews
@Composable
private fun UniqueEquipPreview() {
    PreviewLayout {
        UniqueEquipDetail(
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
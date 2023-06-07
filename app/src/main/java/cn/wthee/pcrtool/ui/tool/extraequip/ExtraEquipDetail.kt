package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentData
import cn.wthee.pcrtool.data.enums.AttrValueType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import cn.wthee.pcrtool.ui.components.AttrCompare
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.UnitList
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.SlideAnimation
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.ICON_EXTRA_EQUIPMENT_CATEGORY
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel


/**
 * ex装备详情
 *
 * @param equipId 装备编号
 */
@Composable
fun ExtraEquipDetail(
    equipId: Int,
    toExtraEquipUnit: (Int) -> Unit,
    toExtraEquipDrop: (Int) -> Unit,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val extraEquipmentData =
        extraEquipmentViewModel.getExtraEquip(equipId)
            .collectAsState(initial = ExtraEquipmentData()).value

    val unitIds = extraEquipmentViewModel.getExtraEquipUnitList(extraEquipmentData.category)
        .collectAsState(initial = arrayListOf()).value
    //收藏状态
    val starIds = FilterExtraEquipment.getStarIdList()
    val loved = remember {
        mutableStateOf(starIds.contains(equipId))
    }

    Box(
        modifier = Modifier
            .padding(top = Dimen.largePadding)
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //基本信息
            if (extraEquipmentData.equipmentId != UNKNOWN_EQUIP_ID) {
                ExtraEquipBasicInfo(extraEquipmentData, loved)
            }
            //被动技能
            SlideAnimation(visible = extraEquipmentData.getPassiveSkillIds().isNotEmpty()) {
                ExtraEquipSkill(extraEquipmentData.getPassiveSkillIds())
            }

            CommonSpacer()
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
            horizontalArrangement = Arrangement.End
        ) {
            //装备收藏
            MainSmallFab(
                iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
            ) {
                FilterExtraEquipment.addOrRemove(equipId)
                loved.value = !loved.value
            }
            //关联角色
            MainSmallFab(
                iconType = MainIconType.CHARACTER,
                text = unitIds.size.toString()
            ) {
                toExtraEquipUnit(extraEquipmentData.category)
            }
            //掉落信息
            MainSmallFab(
                iconType = MainIconType.EXTRA_EQUIP_DROP
            ) {
                toExtraEquipDrop(extraEquipmentData.equipmentId)
            }
        }

    }
}

/**
 * ex装备基本信息
 */
@Composable
private fun ExtraEquipBasicInfo(
    extraEquipmentData: ExtraEquipmentData,
    loved: MutableState<Boolean>
) {
    if (BuildConfig.DEBUG) {
        Subtitle1(
            text = extraEquipmentData.equipmentId.toString()
        )
    }
    MainText(
        text = extraEquipmentData.equipmentName,
        color = if (loved.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        selectable = true
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ICON_EXTRA_EQUIPMENT_CATEGORY, extraEquipmentData.category),
            size = Dimen.smallIconSize,
        )
        Subtitle2(
            text = extraEquipmentData.categoryName,
            modifier = Modifier.padding(start = Dimen.smallPadding)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.largePadding)
    ) {
        //图标
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ImageRequestHelper.ICON_EXTRA_EQUIPMENT, extraEquipmentData.equipmentId)
        )
        //描述
        Subtitle2(
            text = extraEquipmentData.getDesc(),
            modifier = Modifier.padding(start = Dimen.mediumPadding),
            selectable = true
        )
    }

    //属性变化
    Row(
        modifier = Modifier.padding(
            horizontal = Dimen.mediumPadding + Dimen.smallPadding
        )
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        Subtitle1(
            text = stringResource(id = R.string.extra_equip_default_value),
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(0.2f)
                .padding(0.dp)
        )
        Subtitle1(
            text = stringResource(id = R.string.extra_equip_max_value),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.2f)
        )
    }
    AttrCompare(
        compareData = extraEquipmentData.fixAttrList(isPreview = LocalInspectionMode.current),
        isExtraEquip = true,
        attrValueType = AttrValueType.PERCENT
    )
}

/**
 * 装备被动技能列表
 */
@Composable
private fun ExtraEquipSkill(
    skillIds: List<Int>,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    val skills = skillViewModel.getExtraEquipPassiveSkills(skillIds)
        .collectAsState(initial = arrayListOf()).value

    if (skills.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.largePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //技能信息标题
            MainText(
                text = stringResource(R.string.extra_equip_passive_skill),
                modifier = Modifier
                    .padding(top = Dimen.largePadding)
            )

            skills.forEach { skillDetail ->
                SkillItem(
                    skillDetail = skillDetail,
                    unitType = UnitType.CHARACTER,
                    property = CharacterProperty(),
                    toSummonDetail = null,
                    isExtraEquipSKill = true
                )
            }
        }

    }
}

/**
 * 可使用的ex装备角色
 */
@Composable
fun ExtraEquipUnitList(
    category: Int,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val unitIds = extraEquipmentViewModel.getExtraEquipUnitList(category)
        .collectAsState(initial = arrayListOf()).value

    UnitList(unitIds)
}


@CombinedPreviews
@Composable
private fun ExtraEquipBasicInfoPreview() {
    val loved = remember {
        mutableStateOf(true)
    }

    PreviewLayout {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ExtraEquipBasicInfo(
                ExtraEquipmentData(
                    equipmentId = 1,
                    equipmentName = stringResource(id = R.string.debug_short_text),
                    description = stringResource(id = R.string.debug_long_text),
                ),
                loved
            )
        }
    }
}
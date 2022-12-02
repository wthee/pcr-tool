package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.SlideAnimation
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_EXTRA_EQUIPMENT_CATEGORY
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
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
        extraEquipmentViewModel.getEquip(equipId)
            .collectAsState(initial = ExtraEquipmentData()).value

    val unitIds = extraEquipmentViewModel.getEquipUnitList(extraEquipmentData.category)
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

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            if (extraEquipmentData.equipmentId != UNKNOWN_EQUIP_ID) {
                if (BuildConfig.DEBUG) {
                    Subtitle1(
                        text = extraEquipmentData.equipmentId.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
                MainText(
                    text = extraEquipmentData.equipmentName,
                    color = if (loved.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    selectable = true
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconCompose(
                        data = ImageResourceHelper.getInstance()
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
                    IconCompose(
                        data = ImageResourceHelper.getInstance()
                            .getUrl(ImageResourceHelper.ICON_EXTRA_EQUIPMENT, equipId)
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
                    compareData = extraEquipmentData.fixAttrList(),
                    isExtraEquip = true,
                    attrValueType = AttrValueType.PERCENT
                )
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
            FabCompose(
                iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
            ) {
                FilterExtraEquipment.addOrRemove(equipId)
                loved.value = !loved.value
            }
            //关联角色
            FabCompose(
                iconType = MainIconType.CHARACTER,
                text = unitIds.size.toString()
            ) {
                toExtraEquipUnit(extraEquipmentData.category)
            }
            //掉落信息
            FabCompose(
                iconType = MainIconType.EXTRA_EQUIP_DROP
            ) {
                toExtraEquipDrop(extraEquipmentData.equipmentId)
            }
        }

    }
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
                text = stringResource(R.string.extra_equip_poassive_skill),
                modifier = Modifier
                    .padding(top = Dimen.largePadding)
            )

            skills.forEachIndexed { index, skillDetail ->
                SkillItem(
                    skillIndex = index,
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
    val unitIds = extraEquipmentViewModel.getEquipUnitList(category)
        .collectAsState(initial = arrayListOf()).value

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = rememberLazyListState()
    ) {
        //标题
        item {
            MainTitleText(
                text = stringResource(R.string.extra_equip_unit),
                modifier = Modifier
                    .padding(Dimen.largePadding)
            )
        }

        //角色图标
        item {
            GridIconListCompose(unitIds) {}
        }

        item {
            CommonSpacer()
        }
    }

}
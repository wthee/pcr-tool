package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
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
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.SlideAnimation
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper
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
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val extraEquipmentData =
        extraEquipmentViewModel.getEquip(equipId)
            .collectAsState(initial = ExtraEquipmentData()).value

    val unitIds = extraEquipmentViewModel.getEquipUnitList(extraEquipmentData.category)
        .collectAsState(initial = arrayListOf()).value
    //收藏状态
    val filter = navViewModel.filterExtraEquip.observeAsState()

    val loved = remember {
        mutableStateOf(filter.value?.starIds?.contains(equipId) ?: false)
    }
    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EXTRA_EQUIP, ""))
                ?: arrayListOf()
        loved.value = filterValue.starIds.contains(equipId)
    }
    val loveText = if (loved.value) "" else stringResource(id = R.string.title_love)

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.largePadding)
                ) {
                    //图标
                    IconCompose(
                        data = ImageResourceHelper.getInstance().getExtraEquipPic(equipId)
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
                        vertical = Dimen.mediumPadding,
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
                text = loveText
            ) {
                filter.value?.addOrRemove(equipId)
                loved.value = !loved.value
            }
            //关联角色
            FabCompose(
                iconType = MainIconType.CHARACTER,
                text = unitIds.size.toString()
            ) {
                toExtraEquipUnit(extraEquipmentData.category)
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

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        //标题
        MainTitleText(
            text = stringResource(R.string.extra_equip_unit),
            modifier = Modifier
                .padding(Dimen.largePadding)
        )

        //角色图标
        GridIconListCompose(unitIds) {

        }

        CommonSpacer()
    }

}
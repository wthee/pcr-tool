package cn.wthee.pcrtool.ui.tool.extraequip.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentData
import cn.wthee.pcrtool.data.enums.AttrValueType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.ui.components.AttrCompare
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.skill.SkillItemContent
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.ICON_EXTRA_EQUIPMENT_CATEGORY
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID
import kotlinx.coroutines.launch


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
    extraEquipDetailViewModel: ExtraEquipDetailViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by extraEquipDetailViewModel.uiState.collectAsStateWithLifecycle()

    //初始收藏信息
    LifecycleEffect(Lifecycle.Event.ON_RESUME) {
        extraEquipDetailViewModel.reloadFavoriteList()
    }

    MainScaffold(
        fab = {
            //装备收藏
            MainSmallFab(
                iconType = if (uiState.favorite) MainIconType.FAVORITE_FILL else MainIconType.FAVORITE_LINE,
            ) {
                scope.launch {
                    extraEquipDetailViewModel.updateFavoriteId()
                }
            }

            //关联角色
            if (uiState.unitIdList.isNotEmpty()) {
                MainSmallFab(
                    iconType = MainIconType.CHARACTER,
                    text = uiState.unitIdList.size.toString()
                ) {
                    toExtraEquipUnit(uiState.category)
                }
            }

            //掉落信息
            MainSmallFab(
                iconType = MainIconType.EXTRA_EQUIP_DROP
            ) {
                toExtraEquipDrop(equipId)
            }
        }
    ) {
        Column {
            StateBox(stateType = uiState.loadingState) {
                uiState.equipData?.let { extraEquipmentData ->
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //基本信息
                        if (extraEquipmentData.equipmentId != UNKNOWN_EQUIP_ID) {
                            ExtraEquipBasicInfo(extraEquipmentData, uiState.favorite)
                        }
                        //被动技能
                        uiState.skillList?.let { ExtraEquipSkill(it) }

                        CommonSpacer()
                    }
                }
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
    favorite: Boolean
) {
    if (BuildConfig.DEBUG) {
        Subtitle1(
            text = extraEquipmentData.equipmentId.toString()
        )
    }
    MainText(
        text = extraEquipmentData.equipmentName,
        color = if (favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
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
        compareData = extraEquipmentData.fixAttrList(LocalContext.current),
        isExtraEquip = true,
        attrValueType = AttrValueType.PERCENT
    )
}

/**
 * 装备被动技能列表
 */
@Composable
private fun ExtraEquipSkill(
    skillList: List<SkillDetail>
) {

    if (skillList.isNotEmpty()) {
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

            skillList.forEach { skillDetail ->
                SkillItemContent(
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


@CombinedPreviews
@Composable
private fun ExtraEquipBasicInfoPreview() {
    PreviewLayout {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ExtraEquipBasicInfo(
                ExtraEquipmentData(
                    equipmentId = 1,
                    equipmentName = stringResource(id = R.string.debug_short_text),
                    description = stringResource(id = R.string.debug_long_text),
                ),
                true
            )
        }
    }
}
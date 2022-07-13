package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.RankCompareData
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import kotlinx.coroutines.launch


/**
 * 角色 RANK 对比
 *
 * @param unitId 角色编号
 * @param maxRank 角色rank最大值
 * @param level 角色等级
 * @param rarity 角色星级
 * @param uniqueEquipLevel 角色专武等级
 */
@OptIn(ExperimentalMaterialApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RankCompare(
    unitId: Int,
    maxRank: Int,
    level: Int,
    rarity: Int,
    uniqueEquipLevel: Int,
    navViewModel: NavViewModel,
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
) {
    val curRank = navViewModel.curRank.value
    val targetRank = navViewModel.targetRank.value
    val rank0 = remember {
        mutableStateOf(maxRank)
    }
    val rank1 = remember {
        mutableStateOf(maxRank)
    }
    if (curRank != 0) {
        rank0.value = curRank ?: 1
    }
    if (targetRank != 0) {
        rank1.value = targetRank ?: maxRank
    }
    val attrCompareData = attrViewModel.getUnitAttrCompare(
        unitId,
        level,
        rarity,
        uniqueEquipLevel,
        rank0.value,
        rank1.value
    ).collectAsState(initial = arrayListOf()).value
    // dialog 状态
    val bottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    if (!bottomSheetState.isVisible && !bottomSheetState.isAnimationRunning) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKCilck.postValue(false)
    }
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
        sheetShape = if (bottomSheetState.offset.value == 0f) {
            Shapes.None
        } else {
            ShapeTop()
        },
        sheetContent = {
            //RANK 选择
            RankSelectCompose(rank0, rank1, maxRank, bottomSheetState, navViewModel)
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Dimen.largePadding)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = Dimen.largePadding)
                ) {
                    //头像
                    IconCompose(
                        data = ImageResourceHelper.getInstance().getMaxIconUrl(unitId),
                        size = Dimen.largeIconSize
                    )
                    Column(modifier = Modifier.padding(start = Dimen.mediumPadding)) {
                        //等级
                        Text(
                            text = "$level",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = Dimen.smallPadding)
                        )
                        StarCompose(rarity)
                    }
                }
                MainCard(
                    shape = ShapeTop(),
                    modifier = Modifier
                        .padding(top = Dimen.largePadding)
                        .fillMaxSize(),
                ) {
                    Column {
                        Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
                            Spacer(modifier = Modifier.weight(0.3f))
                            RankText(
                                rank = rank0.value,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .weight(0.2f)
                                    .padding(0.dp)
                            )
                            RankText(
                                rank = rank1.value,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(0.2f)
                            )
                            Text(
                                text = stringResource(id = R.string.result),
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(0.2f)
                            )
                        }
                        AttrCompare(attrCompareData)
                    }
                }
            }
            FabCompose(
                iconType = MainIconType.RANK_SELECT,
                text = stringResource(id = R.string.rank_select),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
            ) {
                coroutineScope.launch {
                    if (bottomSheetState.isVisible) {
                        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                        bottomSheetState.hide()
                    } else {
                        navViewModel.fabMainIcon.postValue(MainIconType.OK)
                        bottomSheetState.show()
                    }
                }
            }
        }

    }
}

/**
 * 属性对比
 */
@Composable
fun AttrCompare(compareData: List<RankCompareData>) {
    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        compareData.forEach {
            Row(modifier = Modifier.padding(Dimen.smallPadding)) {
                MainTitleText(
                    text = it.title,
                    modifier = Modifier.weight(0.3f)
                )
                MainContentText(
                    text = it.attr0.int.toString(),
                    modifier = Modifier.weight(0.2f)
                )
                MainContentText(
                    text = it.attr1.int.toString(),
                    modifier = Modifier.weight(0.2f)
                )
                val color = when {
                    it.attrCompare.int > 0 -> colorGreen
                    it.attrCompare.int < 0 -> colorRed
                    else -> Color.Unspecified
                }
                Text(
                    text = it.attrCompare.int.toString(),
                    color = color,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }
        CommonSpacer()
    }
}

/**
 * 星级显示
 */
@Composable
private fun StarCompose(
    rarity: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        for (i in 1..rarity) {
            val iconId = when (i) {
                6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            IconCompose(
                data = iconId,
                size = Dimen.mediumIconSize,
                modifier = Modifier.padding(Dimen.smallPadding)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val data = listOf(RankCompareData(), RankCompareData(), RankCompareData())
    PreviewBox {
        AttrCompare(compareData = data)
    }
}
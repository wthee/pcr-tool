package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.RankCompareData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.ui.theme.noShape
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
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
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
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
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    if (!state.isVisible && !state.isAnimationRunning) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKCilck.postValue(false)
    }
    //关闭监听
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false

    ModalBottomSheetLayout(
        sheetState = state,
        scrimColor = colorResource(id = if (MaterialTheme.colors.isLight) R.color.alpha_white else R.color.alpha_black),
        sheetElevation = Dimen.sheetElevation,
        sheetShape = if (state.offset.value == 0f) {
            noShape
        } else {
            MaterialTheme.shapes.large
        },
        sheetContent = {
            //RANK 选择
            RankSelectCompose(rank0, rank1, maxRank, coroutineScope, state, navViewModel)
        }
    ) {

        if (ok) {
            coroutineScope.launch {
                state.hide()
            }
            navViewModel.fabOKCilck.postValue(false)
        }

        FadeAnimation(visible = attrCompareData.isNotEmpty()) {
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
                            data = CharacterIdUtil.getMaxIconUrl(
                                unitId,
                                MainActivity.r6Ids.contains(unitId)
                            ),
                            size = Dimen.largeIconSize
                        )
                        Column(modifier = Modifier.padding(start = Dimen.mediuPadding)) {
                            //等级
                            Text(
                                text = "$level",
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(start = Dimen.smallPadding)
                            )
                            StarCompose(rarity)
                        }
                    }
                    Card(
                        shape = CardTopShape,
                        elevation = Dimen.cardElevation,
                        modifier = Modifier
                            .padding(top = Dimen.largePadding)
                            .fillMaxSize()
                    ) {
                        Column {
                            Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                                Spacer(modifier = Modifier.weight(0.3f))
                                RankText(
                                    rank = rank0.value,
                                    style = MaterialTheme.typography.subtitle1,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .weight(0.2f)
                                        .padding(0.dp)
                                )
                                RankText(
                                    rank = rank1.value,
                                    style = MaterialTheme.typography.subtitle1,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(0.2f)
                                )
                                Text(
                                    text = stringResource(id = R.string.result),
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.subtitle1,
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
                        if (state.isVisible) {
                            navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                            state.hide()
                        } else {
                            navViewModel.fabMainIcon.postValue(MainIconType.OK)
                            state.show()
                        }
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
            .padding(Dimen.mediuPadding)
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
                    it.attrCompare.int > 0 -> colorResource(id = R.color.color_rank_21)
                    it.attrCompare.int < 0 -> colorResource(id = R.color.color_rank_18_20)
                    else -> Color.Unspecified
                }
                Text(
                    text = it.attrCompare.int.toString(),
                    color = color,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.body2,
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
            Image(
                painter = rememberImagePainter(data = iconId),
                contentDescription = null,
                modifier = Modifier
                    .padding(Dimen.divLineHeight)
                    .size(Dimen.starIconSize)
                    .clip(CircleShape)
                    .padding(Dimen.smallPadding)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PcrtoolcomposeTheme {
        val data = listOf(RankCompareData(), RankCompareData(), RankCompareData())
        AttrCompare(compareData = data)
    }
}
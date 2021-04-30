package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.RankCompareData
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.launch


/**
 * 角色 RANK 对比
 */
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
    attrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel()
) {
    val rank0 = remember {
        mutableStateOf(maxRank)
    }
    val rank1 = remember {
        mutableStateOf(maxRank)
    }
    attrViewModel.getUnitAttrCompare(
        unitId,
        level,
        rarity,
        uniqueEquipLevel,
        rank0.value,
        rank1.value
    )
    val attrCompareData = attrViewModel.attrCompareData.observeAsState().value ?: arrayListOf()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
        navViewModel.fabOK.postValue(false)
    }


    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            //RANK 选择
            RankSelectCompose(rank0, rank1, maxRank, coroutineScope, state, navViewModel)
        }
    ) {
        //关闭监听
        val close = navViewModel.fabClose.observeAsState().value ?: false
        if (close) {
            coroutineScope.launch {
                state.hide()
            }
            navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
            navViewModel.fabClose.postValue(false)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Dimen.largePadding)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainText(text = "$level")
                StarCompose(rarity)
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
            ExtendedFabCompose(
                icon = painterResource(id = R.drawable.ic_select),
                text = stringResource(id = R.string.rank_select),
                textWidth = Dimen.getWordWidth(4.5f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
            ) {
                coroutineScope.launch {
                    if (state.isVisible) {
                        navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
                        state.hide()
                    } else {
                        navViewModel.fabMainIcon.postValue(R.drawable.ic_ok)
                        state.show()
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
                    it.attrCompare.int > 0 -> colorResource(id = R.color.cool_apk)
                    it.attrCompare.int < 0 -> colorResource(id = R.color.color_rank_18)
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
                painter = rememberCoilPainter(request = iconId),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = Dimen.smallPadding, bottom = Dimen.smallPadding)
                    .clip(CircleShape)
                    .size(Dimen.starIconSize)
            )
        }
    }
}
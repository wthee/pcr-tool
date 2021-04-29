package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.CharacterSelectInfo
import cn.wthee.pcrtool.data.model.RankCompareData
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
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
    selectedInfo: String,
    navViewModel: NavViewModel,
    attrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel()
) {
    val selected = Gson().fromJson(selectedInfo, CharacterSelectInfo::class.java)
    val rank0 = remember {
        mutableStateOf(selected.rank)
    }
    val rank1 = remember {
        mutableStateOf(selected.rank)
    }
    selected.rank = rank0.value
    attrViewModel.getUnitAttrCompare(unitId, selected, rank0.value, rank1.value)
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
            RankSelect(rank0, rank1, maxRank, coroutineScope, state, navViewModel)
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
                .padding(top = Dimen.fabIconSize)
        ) {
            Column {
                Row {
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
                iconId = R.drawable.ic_select,
                text = stringResource(id = R.string.rank_select),
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
 * RANK 选择页面
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun RankSelect(
    rank0: MutableState<Int>,
    rank1: MutableState<Int>,
    maxRank: Int,
    coroutineScope: CoroutineScope,
    sheetState: ModalBottomSheetState,
    navViewModel: NavViewModel
) {
    val rankList = arrayListOf<Int>()
    for (i in maxRank downTo 1) {
        rankList.add(i)
    }
    val ok = navViewModel.fabOK.observeAsState().value ?: false
    val select0 = remember {
        mutableStateOf(rank0.value)
    }
    val select1 = remember {
        mutableStateOf(rank1.value)
    }
    Column {
        Row {
            MainText(
                text = stringResource(id = R.string.cur_rank),
                modifier = Modifier.weight(0.45f)
            )
            Spacer(modifier = Modifier.weight(0.1f))
            MainText(
                text = stringResource(id = R.string.target_rank),
                modifier = Modifier.weight(0.45f)
            )
        }
        Row {
            //RANK 选择
            if (ok) {
                coroutineScope.launch {
                    sheetState.hide()
                }
                navViewModel.fabOK.postValue(false)
                navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
                rank0.value = select0.value
                rank1.value = select1.value
            }
            RankSelectItem(select = select0, rankList = rankList, modifier = Modifier.weight(0.45f))
            Spacer(modifier = Modifier.weight(0.1f))
            RankSelectItem(select = select1, rankList = rankList, modifier = Modifier.weight(0.45f))
        }
    }
}


/**
 * RANK 选择器
 */
@ExperimentalFoundationApi
@Composable
fun RankSelectItem(select: MutableState<Int>, rankList: List<Int>, modifier: Modifier) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        modifier = modifier
    ) {
        itemsIndexed(rankList) { index, it ->
            Box(
                modifier = Modifier
                    .shadow(elevation = 0.dp, shape = Shapes.large, clip = true)
                    .clickable {
                        select.value = it
                    }
                    .background(
                        if (it == select.value) {
                            colorResource(id = R.color.alpha_primary)
                        } else {
                            MaterialTheme.colors.surface
                        }
                    )
                    .padding(Dimen.mediuPadding)
            ) {
                RankText(
                    rank = it,
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}
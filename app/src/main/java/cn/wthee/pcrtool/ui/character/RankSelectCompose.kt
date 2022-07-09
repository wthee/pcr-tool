package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.common.CaptionText
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.common.VerticalGrid
import cn.wthee.pcrtool.ui.common.getRankColor
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * RANK 选择页面
 * @param rank0 当前rank
 * @param rank1 目标rank
 * @param maxRank rank最大值
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RankSelectCompose(
    rank0: MutableState<Int>,
    rank1: MutableState<Int>,
    maxRank: Int,
    coroutineScope: CoroutineScope,
    sheetState: ModalBottomSheetState,
    navViewModel: NavViewModel,
    type: RankSelectType = RankSelectType.DEFAULT
) {
    val rankList = arrayListOf<Int>()
    for (i in maxRank downTo 1) {
        rankList.add(i)
    }
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    //选择
    val selectIndex0 = remember {
        mutableStateOf(maxRank - rank0.value)
    }
    val selectIndex1 = remember {
        mutableStateOf(maxRank - rank1.value)
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(
                bottom = Dimen.sheetMarginBottom,
                top = Dimen.largePadding,
                start = Dimen.mediumPadding,
            )
    ) {
        //RANK 选择
        if (ok) {
            coroutineScope.launch {
                sheetState.hide()
            }
            navViewModel.fabOKCilck.postValue(false)
            navViewModel.fabMainIcon.postValue(MainIconType.BACK)
            rank0.value = maxRank - selectIndex0.value
            rank1.value = maxRank - selectIndex1.value
            if (type == RankSelectType.DEFAULT) {
                navViewModel.curRank.postValue(maxRank - selectIndex0.value)
                navViewModel.targetRank.postValue(maxRank - selectIndex1.value)
            } else {
                navViewModel.curRank1.postValue(maxRank - selectIndex0.value)
                navViewModel.targetRank1.postValue(maxRank - selectIndex1.value)
            }

        }

        //当前
        MainText(text = stringResource(id = R.string.cur_rank))
        RankSelectItem(
            selectIndex = selectIndex0,
            rankList = rankList
        )
        //目标
        MainText(text = stringResource(id = R.string.target_rank))
        if (type == RankSelectType.LIMIT) {
            //只可选择比当前大的值
            val newRankList = arrayListOf<Int>()
            for (i in maxRank downTo maxRank - selectIndex0.value) {
                newRankList.add(i)
            }
            RankSelectItem(
                selectIndex = selectIndex1,
                rankList = newRankList
            )
        } else {
            RankSelectItem(
                selectIndex = selectIndex1,
                rankList = rankList
            )
        }

    }
}

/**
 * RANK 选择器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankSelectItem(
    selectIndex: MutableState<Int>,
    rankList: List<Int>
) {
    val context = LocalContext.current

    Box {
        VerticalGrid(spanCount = 6) {
            rankList.forEachIndexed { index, rank ->
                val rankColor = getRankColor(rank = rank)
                val selected = selectIndex.value == index
                FilterChip(
                    selected = selected,
                    onClick = {
                        VibrateUtil(context).single()
                        selectIndex.value = index
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = rankColor
                    ),
                    label = {
                        CaptionText(
                            text = rankFillBlank(rank),
                            color = if (selected) Color.White else rankColor
                        )
                    }
                )
            }
        }
    }

}

/**
 * 填充空格
 */
private fun rankFillBlank(rank: Int): String {
    return when (rank) {
        in 0..9 -> "0$rank"
        else -> "$rank"
    }
}

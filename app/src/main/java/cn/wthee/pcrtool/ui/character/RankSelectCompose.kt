package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.VibrateUtil


/**
 * RANK 选择页面
 * @param rank0 当前rank
 * @param rank1 目标rank
 * @param maxRank rank最大值
 */
@Composable
fun RankSelectCompose(
    rank0: MutableState<Int>,
    rank1: MutableState<Int>,
    maxRank: Int,
    dialogState: MutableState<Boolean>,
    type: RankSelectType = RankSelectType.DEFAULT
) {
    val rankList = arrayListOf<Int>()
    for (i in maxRank downTo 1) {
        rankList.add(i)
    }

    //选择
    val selectIndex0 = remember {
        mutableStateOf(maxRank - rank0.value)
    }
    val selectIndex1 = remember {
        mutableStateOf(maxRank - rank1.value)
    }

    rank0.value = maxRank - selectIndex0.value
    rank1.value = maxRank - selectIndex1.value


    AlertDialog(
        onDismissRequest = { dialogState.value = false },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                //当前
                MainText(text = stringResource(id = R.string.cur_rank))
                RankSelectItem(
                    selectIndex = selectIndex0,
                    rankList = rankList,
                    targetType = RankSelectType.DEFAULT,
                    currentRank = maxRank - selectIndex0.value
                )
                //目标
                MainText(
                    text = stringResource(id = R.string.target_rank),
                    modifier = Modifier.padding(top = Dimen.mediumPadding)
                )
                RankSelectItem(
                    selectIndex = selectIndex1,
                    rankList = rankList,
                    targetType = type,
                    currentRank = maxRank - selectIndex0.value
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconTextButton(
                    icon = MainIconType.CLOSE,
                    text = stringResource(id = R.string.close),
                    iconSize = Dimen.fabIconSize,
                    textStyle = MaterialTheme.typography.titleMedium
                ) {
                    dialogState.value = false
                }
            }
        }
    )

}

/**
 * RANK 选择器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankSelectItem(
    selectIndex: MutableState<Int>,
    rankList: List<Int>,
    targetType: RankSelectType,
    currentRank: Int
) {
    val context = LocalContext.current

    Box {
        VerticalGrid(spanCount = 5) {
            rankList.forEachIndexed { index, rank ->
                val rankColor = getRankColor(rank = rank)
                val selected = selectIndex.value == index
                val enabled = targetType == RankSelectType.DEFAULT ||
                        (targetType == RankSelectType.LIMIT && rank >= currentRank)

                FilterChip(
                    selected = selected,
                    enabled = enabled,
                    onClick = {
                        VibrateUtil(context).single()
                        selectIndex.value = index
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (enabled) {
                            rankColor
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    ),
                    label = {
                        CaptionText(
                            text = rankFillBlank(rank),
                            color = if (enabled) {
                                if (selected) colorWhite else rankColor
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
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


@CombinedPreviews
@Composable
private fun RankSelectItemPreview() {
    val selectIndex = remember {
        mutableStateOf(0)
    }
    PreviewLayout {
        RankSelectItem(
            selectIndex = selectIndex,
            rankList = arrayListOf(24, 23, 22, 21,20,19),
            targetType = RankSelectType.DEFAULT,
            currentRank = 24
        )
    }
}
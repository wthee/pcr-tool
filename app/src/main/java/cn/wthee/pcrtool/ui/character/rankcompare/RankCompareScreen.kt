package cn.wthee.pcrtool.ui.character.rankcompare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.AttrValueType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.navigation.navigateUpSheet
import cn.wthee.pcrtool.ui.components.AttrCompare
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.RankRangePickerCompose
import cn.wthee.pcrtool.ui.components.RankText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import kotlinx.coroutines.launch


/**
 * 角色 RANK 对比
 */
@Composable
fun RankCompareScreen(
    rankCompareViewModel: RankCompareViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by rankCompareViewModel.uiState.collectAsStateWithLifecycle()


    MainScaffold(
        modifier = Modifier.padding(top = Dimen.largePadding),
        fab = {
            MainSmallFab(
                iconType = MainIconType.RANK_COMPARE,
                text = stringResource(id = R.string.rank_compare)
            )
        },
        secondLineFab = {
            //RANK 选择
            RankRangePickerCompose(
                rank0 = uiState.rank0,
                rank1 = uiState.rank1,
                maxRank = uiState.maxRank,
                openDialog = uiState.openDialog,
                updateRank = rankCompareViewModel::updateRank,
                changeDialog = rankCompareViewModel::changeDialog
            )
        },
        onMainFabClick = {
            scope.launch {
                if (uiState.openDialog) {
                    rankCompareViewModel.changeDialog(false)
                } else {
                    navigateUpSheet()
                }
            }
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            rankCompareViewModel.changeDialog(false)
        }
    ) {
        RankCompareContent(
            rank0 = uiState.rank0,
            rank1 = uiState.rank1,
            attrCompareDataList = uiState.attrCompareDataList
        )
    }
}

@Composable
private fun RankCompareContent(
    rank0: Int,
    rank1: Int,
    attrCompareDataList: List<AttrCompareData>
) {
    Column {
        Row(
            modifier = Modifier.padding(
                vertical = Dimen.mediumPadding,
                horizontal = Dimen.mediumPadding + Dimen.smallPadding
            )
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            RankText(
                rank = rank0,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(0.2f)
                    .padding(0.dp)
            )
            RankText(
                rank = rank1,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.2f)
            )
            Subtitle2(
                text = stringResource(id = R.string.result),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.2f),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        AttrCompare(
            compareData = attrCompareDataList,
            isExtraEquip = false,
            attrValueType = AttrValueType.INT
        )
    }
}


@CombinedPreviews
@Composable
private fun RankCompareContentPreview() {
    val data = listOf(AttrCompareData(), AttrCompareData(), AttrCompareData())
    PreviewLayout {
        RankCompareContent(
            rank0 = 1,
            rank1 = 22,
            attrCompareDataList = data
        )
    }
}
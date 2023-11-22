package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.db.view.MockGachaProData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainAlertDialog
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.launch

/**
 * 历史卡池
 */
@Composable
fun MockGachaHistory(
    scrollState: LazyGridState,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val uiState by mockGachaViewModel.uiState.collectAsStateWithLifecycle()


    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        columns = GridCells.Adaptive(getItemWidth())
    ) {
        items(
            items = uiState.historyList,
            key = {
                it.gachaId
            }
        ) {
            MockGachaHistoryItem(it)
        }
        item {
            CommonSpacer()
        }
        item {
            CommonSpacer()
        }
    }
}

/**
 * 卡池历史记录 item
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MockGachaHistoryItem(
    gachaData: MockGachaProData,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val scope = rememberCoroutineScope()
    val openDialog = remember {
        mutableStateOf(false)
    }

    var upCount = 0
    var start3Count = 0
    val pickUpUnitIds = gachaData.pickUpIds.intArrayList
    val resultIds = gachaData.resultUnitIds.intArrayList
    val resultCount = resultIds.size / 10

    resultIds.forEachIndexed { index, unitId ->
        if (pickUpUnitIds.contains(unitId)) {
            upCount++
        }
        if (gachaData.resultUnitRaritys.intArrayList[index] == 3) {
            start3Count++
        }
    }

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.smallPadding),
            verticalArrangement = Arrangement.Center
        ) {
            MainTitleText(
                text = gachaData.createTime.formatTime.substring(0, 10)
            )
            //up个数
            if (upCount > 0) {
                MainTitleText(
                    text = "UP：$upCount",
                    backgroundColor = colorRed,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
            //3星个数
            if (start3Count > 0) {
                MainTitleText(
                    text = "★3：$start3Count",
                    backgroundColor = colorGold,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            //去抽卡
            IconTextButton(
                icon = MainIconType.MOCK_GACHA_PAY,
                text = stringResource(id = R.string.go_to_mock)
            ) {
                scope.launch {
                    mockGachaViewModel.changeGachaId(gachaData.gachaId)
                    //卡池详情
                    val newPickUpList = arrayListOf<GachaUnitInfo>()
                    gachaData.pickUpIds.intArrayList.forEach {
                        newPickUpList.add(
                            GachaUnitInfo(
                                it,
                                "",
                                -1,
                                3
                            )
                        )
                    }
                    mockGachaViewModel.updatePickUpList(newPickUpList)
                    mockGachaViewModel.changeSelect(gachaData.gachaType)
                    //显示卡池结果
                    mockGachaViewModel.changeShowResult(true)
                }
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.smallPadding)) {
                //up 角色
                val idList = arrayListOf<Int>()
                gachaData.pickUpIds.intArrayList.forEach { unitId ->
                    idList.add(unitId + 30)
                }
                GridIconList(
                    icons = idList
                )
                Row(
                    modifier = Modifier
                        .padding(start = Dimen.smallPadding, end = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //删除操作
                    IconTextButton(
                        icon = MainIconType.DELETE,
                        text = stringResource(R.string.delete_gacha),
                        contentColor = colorRed
                    ) {
                        openDialog.value = true
                    }
                    //日期
                    CaptionText(
                        text = stringResource(
                            id = R.string.last_gacha_date,
                            resultCount,
                            gachaData.lastUpdateTime
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        }
    }

    //删除卡池提示
    MainAlertDialog(
        openDialog = openDialog,
        icon = MainIconType.DELETE,
        title = stringResource(id = R.string.title_dialog_delete),
        text = stringResource(id = R.string.tip_delete_gacha),
    ) {
        mockGachaViewModel.deleteGachaByGachaId(gachaData.gachaId)
    }

}


@CombinedPreviews
@Composable
private fun MockGachaHistoryItemPreview() {
    PreviewLayout {
        MockGachaHistoryItem(
            MockGachaProData(
                gachaType = 1,
                pickUpIds = "1-2",
                resultUnitIds = "1-2-3-4-5-5-4-3-2-1",
                resultUnitRaritys = "1-2-3-1-2-3-1-2-3-1",
                createTime = "2020/01/01 00:00:00"
            )
        )
    }
}
package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.db.view.MockGachaProData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.utils.spanCount
import cn.wthee.pcrtool.viewmodel.MockGachaViewModel
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * 历史卡池
 */
@Composable
fun MockGachaHistory(mockGachaViewModel: MockGachaViewModel = hiltViewModel()) {
    //历史记录
    mockGachaViewModel.getHistory()
    val historyData = mockGachaViewModel.historyList.observeAsState().value ?: arrayListOf()
    val spanCount = getItemWidth().spanCount

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyGridState(),
        columns = GridCells.Adaptive(getItemWidth())
    ) {
        items(
            items = historyData,
            key = {
                it.gachaId
            }
        ) {
            MockGachaHistoryItem(it, spanCount)
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
    spanCount: Int,
    mockGachaViewModel: MockGachaViewModel? = hiltViewModel()
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
            verticalAlignment = Alignment.CenterVertically
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
                    MainActivity.navViewModel.gachaId.postValue(gachaData.gachaId)
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
                    MainActivity.navViewModel.pickUpList.postValue(newPickUpList)
                    MainActivity.navViewModel.mockGachaType.postValue(
                        MockGachaType.getByValue(
                            gachaData.gachaType
                        )
                    )
                    //显示卡池结果
                    MainActivity.navViewModel.showMockGachaResult.postValue(true)
                }
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.smallPadding)) {
                //up 角色
                VerticalGrid(
                    modifier = Modifier.padding(top = Dimen.mediumPadding),
                    spanCount = ((Dimen.iconSize + Dimen.mediumPadding * 2) * max(
                        1,
                        spanCount
                    )).spanCount
                ) {
                    gachaData.pickUpIds.intArrayList.forEach { unitId ->
                        IconCompose(
                            data = ImageResourceHelper.getInstance().getUrl(
                                ImageResourceHelper.ICON_UNIT,
                                unitId + 30
                            ),
                            modifier = Modifier.padding(Dimen.mediumPadding)
                        )
                    }
                }
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
    if (openDialog.value) {
        AlertDialog(
            title = {
                Column(
                    modifier = Modifier
                        .heightIn(max = Dimen.minSheetHeight)
                        .verticalScroll(rememberScrollState())
                ) {
                    MainText(
                        text = stringResource(id = R.string.tip_delete_gacha),
                        textAlign = TextAlign.Start,
                        selectable = true
                    )
                }
            },
            modifier = Modifier.padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding),
            onDismissRequest = {
                openDialog.value = false
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
            confirmButton = {
                //确认
                MainButton(text = stringResource(R.string.delete_gacha)) {
                    mockGachaViewModel?.deleteGachaByGachaId(gachaData.gachaId)
                    openDialog.value = false
                }
            },
            dismissButton = {
                //取消
                SubButton(
                    text = stringResource(id = R.string.cancel)
                ) {
                    openDialog.value = false
                }
            })
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
            ),
            2,
            mockGachaViewModel = null
        )
    }
}
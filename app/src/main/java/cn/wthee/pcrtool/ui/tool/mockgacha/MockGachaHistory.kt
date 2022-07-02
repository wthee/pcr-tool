package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.db.view.MockGachaProData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.MockGachaViewModel
import kotlinx.coroutines.launch

/**
 * 历史卡池
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MockGachaHistory(mockGachaViewModel: MockGachaViewModel = hiltViewModel()) {
    //历史记录
    mockGachaViewModel.getHistory()
    val historyData = mockGachaViewModel.historyList.observeAsState().value ?: arrayListOf()

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
@Composable
private fun MockGachaHistoryItem(
    gachaData: MockGachaProData,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
        Row(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            verticalAlignment = Alignment.Bottom
        ) {
            MainTitleText(
                text = gachaData.createTime.formatTime.substring(0, 10)
            )
            //内容
            if (resultCount > 0) {
                MainTitleText(
                    text = "$resultCount",
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
            if (upCount > 0) {
                MainTitleText(
                    text = "UP：$upCount",
                    backgroundColor = colorResource(id = R.color.color_rank_18_20),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
            if (start3Count > 0) {
                MainTitleText(
                    text = "★3：$start3Count",
                    backgroundColor = colorResource(id = R.color.color_rank_7_10),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconCompose(
                data = MainIconType.MOCK_GACHA_PAY,
                size = Dimen.fabIconSize
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
                    MainActivity.navViewModel.gachaType.postValue(gachaData.gachaType)
                    //显示卡池结果
                    MainActivity.navViewModel.showMockGachaResult.postValue(true)
                }
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {

                //up 角色
                Row {
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
                        .padding(horizontal = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Subtitle2(
                        text = stringResource(R.string.delete_gacha),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            VibrateUtil(context).single()
                            mockGachaViewModel.deleteGachaByGachaId(gachaData.gachaId)
                        })
                    //日期
                    CaptionText(
                        text = "上次抽卡 ${gachaData.lastUpdateTime}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = Dimen.mediumPadding)

                    )
                }

            }
        }
    }
}
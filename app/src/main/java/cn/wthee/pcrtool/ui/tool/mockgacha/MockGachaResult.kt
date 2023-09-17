package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecordData
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.enums.MockGachaType
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorSilver
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.utils.simpleDateFormat
import cn.wthee.pcrtool.viewmodel.MockGachaViewModel


/**
 * 抽取结果详情
 */
@Composable
fun MockGachaResult(
    gachaId: String,
    pickUpUnitIds: List<Int>,
    mockGachaType: MockGachaType,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel()
) {
    mockGachaViewModel.getResult(gachaId = gachaId)
    val resultRecordList =
        mockGachaViewModel.resultRecordList.observeAsState().value ?: arrayListOf()
    val tipToPay = stringResource(id = R.string.tip_no_gacha_record)
    //统计抽中角色的次数
    var upCount = 0
    var start3Count = 0
    resultRecordList.forEach { record ->
        record.unitIds.intArrayList.forEachIndexed { index, unitId ->
            //单up时仅计算最后一个角色
            if ((mockGachaType == MockGachaType.PICK_UP_SINGLE && pickUpUnitIds.last() == unitId)
                || (mockGachaType != MockGachaType.PICK_UP_SINGLE && pickUpUnitIds.contains(unitId))
            ) {
                upCount++
            }
            if (record.unitRaritys.intArrayList[index] == 3) {
                start3Count++
            }
        }
    }

    //显示相关记录
    if (resultRecordList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            MainText(text = tipToPay)
        }
    } else {
        //显示相关信息
        val payCount = resultRecordList.size
        val sumText = stringResource(id = R.string.gacha_used_gem, payCount * 1500)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(
                    top = Dimen.mediumPadding,
                    bottom = Dimen.smallPadding
                )
            ) {
                MainTitleText(
                    text = "UP：$upCount",
                    backgroundColor = colorRed,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
                MainTitleText(
                    text = "★3：$start3Count",
                    backgroundColor = colorGold,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
                MainTitleText(
                    text = sumText,
                    backgroundColor = colorSilver,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyGridState(),
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                itemsIndexed(
                    items = resultRecordList
                ) { index, resultRecord ->
                    MockGachaResultRecordItem(
                        resultRecordList.size - index,
                        pickUpUnitIds,
                        resultRecord,
                        mockGachaType
                    )
                }
                item {
                    CommonSpacer()
                }
                item {
                    CommonSpacer()
                }
            }
        }

    }
}

/**
 * 卡池抽取记录列表项
 */
@Composable
private fun MockGachaResultRecordItem(
    order: Int,
    pickUpUnitIds: List<Int>,
    recordData: MockGachaResultRecordData,
    mockGachaType: MockGachaType,
) {
    val formatResult = arrayListOf<GachaUnitInfo>()
    //pickUp 标记
    val pickUpIndexList = arrayListOf<Int>()
    val rarity3List = arrayListOf<Int>()

    recordData.unitIds.intArrayList.forEachIndexed { index, unitId ->
        val rarity = recordData.unitRaritys.intArrayList[index]
        if ((mockGachaType == MockGachaType.PICK_UP_SINGLE && pickUpUnitIds.last() == unitId)
            || (mockGachaType != MockGachaType.PICK_UP_SINGLE && pickUpUnitIds.contains(unitId))
        ) {
            pickUpIndexList.add(index)
        }
        if (rarity == 3) {
            rarity3List.add(index)
        }
        formatResult.add(GachaUnitInfo(unitId, "", -1, rarity))
    }

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        Row(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding)
        ) {
            MainTitleText(
                text = stringResource(id = R.string.gacha_order, order)
            )
            //抽中 pickUp 角色，添加标注
            if (pickUpIndexList.isNotEmpty()) {
                MainTitleText(
                    text = "PICK UP",
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    backgroundColor = colorRed
                )
            } else if (rarity3List.isNotEmpty()) {
                MainTitleText(
                    text = "★3",
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    backgroundColor = colorGold
                )
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(vertical = Dimen.mediumPadding)) {
                MockGachaResultRecordIconLine(
                    0,
                    formatResult.subList(0, 5),
                    pickUpIndexList
                )
                MockGachaResultRecordIconLine(
                    1,
                    formatResult.subList(5, 10),
                    pickUpIndexList
                )
                //日期
                CaptionText(
                    text = recordData.createTime.simpleDateFormat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = Dimen.mediumPadding)

                )
            }
        }
    }
}

/**
 * 角色图标列表（5个/行）
 */
@Composable
private fun MockGachaResultRecordIconLine(
    line: Int,
    icons: List<GachaUnitInfo>,
    pickUpIndex: ArrayList<Int> = arrayListOf()
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.mediumPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        icons.forEachIndexed { index, gachaUnitInfo ->
            Box(contentAlignment = Alignment.Center) {
                val iconId = gachaUnitInfo.unitId + (if (gachaUnitInfo.rarity == 1) 10 else 30)
                Column(
                    modifier = Modifier
                        .padding(
                            bottom = Dimen.mediumPadding
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance()
                            .getUrl(ImageRequestHelper.ICON_UNIT, iconId)
                    )
                    val textColor = when {
                        pickUpIndex.contains(index + line * 5) -> {
                            colorRed
                        }

                        gachaUnitInfo.rarity == 3 -> {
                            colorGold
                        }

                        else -> {
                            MaterialTheme.colorScheme.onSurface
                        }
                    }
                    MainText(text = "★${gachaUnitInfo.rarity}", color = textColor)
                }
            }
        }
    }
}


@CombinedPreviews
@Composable
private fun MockGachaResultRecordPreview() {
    PreviewLayout {
        MockGachaResultRecordItem(
            1,
            arrayListOf(1, 2),
            MockGachaResultRecordData(
                unitIds = "1-2-3-4-5-5-4-3-2-1",
                unitRaritys = "1-2-3-1-2-3-1-2-3-1",
                createTime = "2020/01/01 00:00:00",
            ),
            MockGachaType.PICK_UP
        )
    }
}
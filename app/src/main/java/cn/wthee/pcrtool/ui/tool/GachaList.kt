package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.enums.GachaType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

/**
 * 角色卡池页面
 */
@Composable
fun GachaList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    toMockGacha: () -> Unit,
    gachaViewModel: GachaViewModel = hiltViewModel()
) {
    val gachaList = gachaViewModel.getGachaHistory().collectAsState(initial = arrayListOf()).value
    val fesUnitIds =
        gachaViewModel.getGachaFesUnitList().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (gachaList.isNotEmpty()) {
            LazyColumn(
                state = scrollState
            ) {
                items(
                    items = gachaList,
                    key = {
                        it.gachaId
                    }
                ) {
                    GachaItem(
                        gachaInfo = it,
                        fesUnitIds = fesUnitIds,
                        toCharacterDetail = toCharacterDetail,
                        toMockGacha = toMockGacha
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.GACHA,
            text = stringResource(id = R.string.tool_gacha),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (_: Exception) {
                }
            }
        }
    }


}

/**
 * 单个卡池
 */
@Composable
fun GachaItem(
    gachaInfo: GachaInfo,
    fesUnitIds: List<Int>,
    toCharacterDetail: (Int) -> Unit,
    toMockGacha: () -> Unit
) {
    val icons = gachaInfo.unitIds.intArrayList
    val type = gachaInfo.getType()
    val color = when (type) {
        GachaType.LIMIT, GachaType.NORMAL -> colorRed
        GachaType.RE_LIMIT, GachaType.RE_NORMAL -> colorGold
        GachaType.FES -> colorGreen
        GachaType.ANNIV -> colorOrange
        GachaType.UNKNOWN -> MaterialTheme.colorScheme.primary
    }
    //是否普通角色、fes混合卡池
    val isMixedGachaPool =
        icons.find { !fesUnitIds.contains(it) } != null && icons.find { fesUnitIds.contains(it) } != null
    val gachaType = when {
        icons.find { !fesUnitIds.contains(it) } == null -> 1
        else -> 0
    }

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            //类型
            MainTitleText(
                text = if (type.stringId != R.string.unknown) {
                    stringResource(id = type.stringId)
                } else {
                    gachaInfo.fixTypeName()
                },
                backgroundColor = color,
                modifier = Modifier.padding(end = Dimen.smallPadding)
            )
            EventTitle(startTime = gachaInfo.startTime, endTime = gachaInfo.endTime)
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
                //图标/描述
                if (icons.isEmpty()) {
                    MainContentText(
                        text = gachaInfo.getDesc(),
                        modifier = Modifier.padding(
                            top = Dimen.mediumPadding,
                            start = Dimen.mediumPadding,
                            end = Dimen.mediumPadding
                        ),
                        textAlign = TextAlign.Start
                    )
                } else {
                    GridIconListCompose(icons = icons, onClickItem = toCharacterDetail)
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //模拟抽卡 fixme 暂不支持普通和fes角色混合池
                    if (!isMixedGachaPool) {
                        IconTextButton(
                            icon = MainIconType.MOCK_GACHA,
                            text = stringResource(R.string.tool_mock_gacha)
                        ) {
                            navViewModel.gachaType.postValue(gachaType)
                            navViewModel.pickUpList.postValue(gachaInfo.getMockGachaUnitList())
                            //跳转
                            toMockGacha()
                        }
                    }
                    //结束日期
                    CaptionText(
                        text = gachaInfo.endTime.formatTime.fixJpTime,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

}

@CombinedPreviews
@Composable
private fun GachaItemPreview() {
    PreviewLayout {
        GachaItem(
            gachaInfo = GachaInfo(),
            toCharacterDetail = {},
            toMockGacha = {},
            fesUnitIds = arrayListOf()
        )
    }
}
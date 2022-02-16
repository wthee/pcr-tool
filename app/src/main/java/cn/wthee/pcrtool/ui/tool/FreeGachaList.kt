package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.FreeGachaInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.EventViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

/**
 * 免费十连页面
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun FreeGachaList(
    scrollState: LazyListState,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val gachas = eventViewModel.getFreeGachaHistory().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (gachas.isNotEmpty()) {
            LazyColumn(
                state = scrollState
            ) {
                items(gachas) {
                    FreeGachaItem(it)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.FREE_GACHA,
            text = stringResource(id = R.string.tool_free_gacha),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (e: Exception) {
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


}

/**
 * 免费十连
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun FreeGachaItem(freeGachaInfo: FreeGachaInfo) {
    val regionType = getRegion()
    val today = getToday()
    val sd = fixJpTime(freeGachaInfo.startTime, regionType)
    val ed = fixJpTime(freeGachaInfo.endTime, regionType)
    val inProgress = isInProgress(today, freeGachaInfo.startTime, freeGachaInfo.endTime, regionType)
    val comingSoon = isComingSoon(today, freeGachaInfo.startTime, regionType)

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
            MainTitleText(
                text = sd.substring(0, 10),
                modifier = Modifier.padding(start = Dimen.smallPadding),
            )
            MainTitleText(
                text = ed.days(sd),
                modifier = Modifier.padding(start = Dimen.smallPadding)
            )

            //计时
            Row(
                modifier = Modifier.padding(start = Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (inProgress) {
                    IconCompose(
                        data = MainIconType.TIME_LEFT.icon,
                        size = Dimen.smallIconSize,
                    )
                    MainContentText(
                        text = stringResource(R.string.progressing, ed.dates(today)),
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (comingSoon) {
                    IconCompose(
                        data = MainIconType.COUNTDOWN.icon,
                        size = Dimen.smallIconSize,
                        tint = colorResource(id = R.color.news_system)
                    )
                    MainContentText(
                        text = stringResource(R.string.coming_soon, sd.dates(today)),
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.news_system)
                    )
                }
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
                //描述
                MainContentText(
                    text = "「免费十连」 ${freeGachaInfo.getCount(regionType)} 次",
                    modifier = Modifier.padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding
                    ),
                    textAlign = TextAlign.Start
                )

                //结束日期
                CaptionText(
                    text = ed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = Dimen.mediumPadding)

                )
            }
        }
    }

}

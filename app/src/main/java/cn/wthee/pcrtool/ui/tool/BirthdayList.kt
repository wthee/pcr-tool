package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.BirthdayData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.BirthdayViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch


/**
 * 生日日程
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BirthdayList(
    scrollState: LazyStaggeredGridState,
    toCharacterDetail: (Int) -> Unit,
    birthdayViewModel: BirthdayViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val dataList = birthdayViewModel.getBirthDayList().collectAsState(initial = arrayListOf()).value


    //日程列表
    Box(modifier = Modifier.fillMaxSize()) {
        if (dataList.isNotEmpty()) {
            LazyVerticalStaggeredGrid(
                state = scrollState, columns = StaggeredGridCells.Adaptive(getItemWidth())
            ) {
                items(items = dataList, key = {
                    "${it.month}/${it.day}"
                }) {
                    BirthdayItem(it, toCharacterDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.BIRTHDAY,
            text = stringResource(id = R.string.tool_birthday),
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
 * 具体角色
 */
@Composable
fun BirthdayItem(
    data: BirthdayData,
    toCharacterDetail: (Int) -> Unit
) {
    val today = getToday()
    val sd = data.getStartTime().formatTime
    val comingSoon = isComingSoon(today, sd, false)
    val icons = data.getOrderUnitIdList()


    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            MainTitleText(
                text = stringResource(id = R.string.title_birth),
                backgroundColor = colorRed,
                modifier = Modifier.padding(end = Dimen.smallPadding)
            )
            MainTitleText(
                text = stringResource(
                    id = R.string.date_m_d,
                    data.month.toString().fixedStr,
                    data.day.toString().fixedStr
                ),
                backgroundColor = colorRed,
                modifier = Modifier.padding(end = Dimen.smallPadding),
            )

            //计时
            if (data.month != 999) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (comingSoon) {
                        IconCompose(
                            data = MainIconType.COUNTDOWN,
                            size = Dimen.smallIconSize,
                            tint = colorPurple
                        )
                        MainContentText(
                            text = stringResource(R.string.coming_soon, sd.days(today)),
                            modifier = Modifier.padding(start = Dimen.smallPadding),
                            textAlign = TextAlign.Start,
                            color = colorPurple
                        )
                    }
                }
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
                //图标
                GridIconListCompose(
                    icons = icons,
                    onClickItem = toCharacterDetail
                )
            }
        }
    }
}


@CombinedPreviews
@Composable
private fun BirthdayItemPreview() {
    PreviewLayout {
        BirthdayItem(
            BirthdayData(
                unitIds = "1-2",
                unitNames = "x-x"
            ),
        ) {}
    }
}
package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import cn.wthee.pcrtool.data.db.view.BirthdayData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.BirthdayViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch


/**
 * 生日日程
 */
@Composable
fun BirthdayList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    birthdayViewModel: BirthdayViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val dataList =
        birthdayViewModel.getBirthDayList().collectAsState(initial = arrayListOf()).value

    //日程列表
    Box(modifier = Modifier.fillMaxSize()) {
        if (dataList.isNotEmpty()) {
            LazyColumn {
                items(dataList) {
                    BirthdayItem(it, toCharacterDetail)
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
                } catch (e: Exception) {
                }
            }
        }
    }

}

/**
 * 具体角色
 */
@Composable
fun BirthdayItem(data: BirthdayData, toCharacterDetail: (Int) -> Unit) {
    val today = getToday()
    val sd = data.getStartTime().formatTime
    val ed = data.getEndTime().formatTime
    val inProgress = isInProgress(today, sd, ed, false)
    val comingSoon = isComingSoon(today, sd, false)
    val icons = data.unitIds.intArrayList


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
                text = stringResource(id = R.string.title_birth),
                backgroundColor = colorResource(id = R.color.news_update)
            )
            MainTitleText(
                text = if (data.month == 999) {
                    "??/??"
                } else {
                    "${data.month.toString().fillZero()}/${data.day.toString().fillZero()}"
                },
                modifier = Modifier.padding(start = Dimen.smallPadding),
                backgroundColor = colorResource(id = R.color.news_update)
            )

            //计时
            if (data.month != 999) {
                Row(
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (inProgress) {
                        IconCompose(
                            data = MainIconType.BIRTHDAY,
                            size = Dimen.smallIconSize,
                            tint = colorResource(id = R.color.news_update)
                        )
                    }
                    if (comingSoon) {
                        IconCompose(
                            data = MainIconType.COUNTDOWN,
                            size = Dimen.smallIconSize,
                            tint = colorResource(id = R.color.news_system)
                        )
                        MainContentText(
                            text = stringResource(R.string.coming_soon, sd.days(today)),
                            modifier = Modifier.padding(start = Dimen.smallPadding),
                            textAlign = TextAlign.Start,
                            color = colorResource(id = R.color.news_system)
                        )
                    }
                }
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
                //图标/描述
                GridIconListCompose(icons = icons, onClickItem = toCharacterDetail)
            }
        }
    }
}
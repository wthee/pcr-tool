package cn.wthee.pcrtool.ui.tool.birthday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.BirthdayData
import cn.wthee.pcrtool.data.db.view.startTime
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.fixedStr
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.isComingSoon
import kotlinx.coroutines.launch


/**
 * 生日日程
 */
@Composable
fun BirthdayListScreen(
    toCharacterDetail: (Int) -> Unit,
    birthdayListViewModel: BirthdayListViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyStaggeredGridState()
    val uiState by birthdayListViewModel.uiState.collectAsStateWithLifecycle()


    //生日列表
    MainScaffold(
        fab = {
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.BIRTHDAY,
                text = stringResource(id = R.string.tool_birthday)
            ) {
                coroutineScope.launch {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (_: Exception) {
                    }
                }
            }
        }
    ) {
        StateBox(stateType = uiState.loadingState) {
            LazyVerticalStaggeredGrid(
                state = scrollState, columns = StaggeredGridCells.Adaptive(getItemWidth())
            ) {
                items(items = uiState.birthdayList, key = {
                    "${it.month}/${it.day}"
                }) {
                    BirthdayItem(it, toCharacterDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        }
    }
}

/**
 * 具体角色
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BirthdayItem(
    data: BirthdayData,
    toCharacterDetail: (Int) -> Unit
) {
    val today = getToday()
    val sd = data.startTime.formatTime
    val comingSoon = isComingSoon(today, sd, false)
    val idList = data.getOrderUnitIdList()


    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            verticalArrangement = Arrangement.Center
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
                    if (comingSoon && !LocalInspectionMode.current) {
                        MainIcon(
                            data = MainIconType.COUNTDOWN,
                            size = Dimen.smallIconSize,
                            tint = colorPurple
                        )
                        MainContentText(
                            text = sd.days(today),
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
                GridIconList(
                    idList = idList,
                    iconResourceType = IconResourceType.CHARACTER,
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
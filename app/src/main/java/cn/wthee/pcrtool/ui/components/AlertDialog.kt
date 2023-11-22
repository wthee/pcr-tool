package cn.wthee.pcrtool.ui.components

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.*


/**
 * 通用弹窗
 */
@Composable
fun MainAlertDialog(
    openDialog: MutableState<Boolean>,
    icon: MainIconType,
    title: String,
    text: String,
    confirmText: String = stringResource(R.string.confirm),
    dismissText: String = stringResource(id = R.string.cancel),
    onDismissRequest: (() -> Unit)? = null,
    onConfirm: () -> Unit,
) {
    if (openDialog.value) {
        val context = LocalContext.current

        AlertDialog(
            icon = {
                MainIcon(data = icon, wrapSize = true)
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            },
            onDismissRequest = {
                if (onDismissRequest != null) {
                    onDismissRequest()
                }
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        VibrateUtil(context).single()
                        onConfirm()
                        openDialog.value = false
                    }
                ) {
                    Text(text = confirmText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        VibrateUtil(context).single()
                        if (onDismissRequest != null) {
                            onDismissRequest()
                        }
                        openDialog.value = false
                    }
                ) {
                    Text(text = dismissText)
                }
            }
        )
    }

}

/**
 * 选择日期范围组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerCompose(
    dateRangePickerState: DateRangePickerState,
    dateRange: DateRange,
    openDialog: Boolean,
    changeDialog: (Boolean) -> Unit,
    changeRange: (DateRange) -> Unit,
) {
    val context = LocalContext.current


    //更新日期
    LaunchedEffect(
        dateRangePickerState.selectedStartDateMillis,
        dateRangePickerState.selectedEndDateMillis
    ) {
        //日期字符串处理
        val startDate = dateRangePickerState.selectedStartDateMillis?.simpleDateFormatUTC
        var endDate = dateRangePickerState.selectedEndDateMillis?.simpleDateFormatUTC
        if (endDate != null) {
            endDate = endDate.replace("00:00:00", "23:59:59")
        }


        changeRange(
            DateRange(
                startDate = startDate ?: "",
                endDate = endDate ?: ""
            )
        )
    }

    //日期选择布局
    SmallFloatingActionButton(
        modifier = Modifier
            .animateContentSize(defaultSpring())
            .padding(
                start = Dimen.fabMargin,
                end = Dimen.fabMargin,
                bottom = Dimen.fabMargin * 2 + Dimen.fabSize,
                top = Dimen.largePadding
            )
            .padding(start = Dimen.textFabMargin, end = Dimen.textFabMargin),
        shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
        onClick = {
            //点击展开布局
            if (!openDialog) {
                VibrateUtil(context).single()
                changeDialog(true)
            }
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (openDialog) {
                Dimen.popupMenuElevation
            } else {
                Dimen.fabElevation
            }
        ),
    ) {
        if (openDialog) {
            //日期选择
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = Dimen.mediumPadding,
                        vertical = Dimen.largePadding
                    )
                    .imePadding(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                //日期选择
                DateRangePicker(
                    state = dateRangePickerState,
                    showModeToggle = true,
                    title = {
                        //标题
                        MainText(
                            text = stringResource(id = R.string.filter_by_start_date),
                            modifier = Modifier.padding(start = Dimen.mediumPadding)
                        )
                    },
                    headline = {
                        DateRangePickerDefaults.DateRangePickerHeadline(
                            state = dateRangePickerState,
                            dateFormatter = DatePickerFormatter(
                                selectedDateSkeleton = "yyyy/MM/dd"
                            ),
                            modifier = Modifier.padding(Dimen.smallPadding)
                        )
                    }
                )

            }
        } else {
            //fab
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Dimen.largePadding)
            ) {
                MainIcon(
                    data = if (dateRange.hasFilter()) {
                        MainIconType.DATE_RANGE_PICKED
                    } else {
                        MainIconType.DATE_RANGE_NONE
                    },
                    size = Dimen.fabIconSize
                )
                Text(
                    text = if (dateRange.hasFilter()) {
                        stringResource(id = R.string.picked_date)
                    } else {
                        stringResource(id = R.string.pick_date)
                    },
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding, end = Dimen.largePadding
                    )
                )
            }
        }
    }
}

/**
 * 选择RANK范围
 */
@Composable
fun RankRangePickerCompose(
    rank0: Int,
    rank1: Int,
    maxRank: Int,
    openDialog: MutableState<Boolean>,
    type: RankSelectType = RankSelectType.DEFAULT,
    updateRank: (Int, Int) -> Unit
) {
    val context = LocalContext.current

    val rankList = arrayListOf<Int>()
    for (i in maxRank downTo 1) {
        rankList.add(i)
    }

    //选择
    val selectIndex0 = remember {
        mutableIntStateOf(maxRank - rank0)
    }
    val selectIndex1 = remember {
        mutableIntStateOf(maxRank - rank1)
    }
    LaunchedEffect(selectIndex0.intValue, selectIndex1.intValue) {
        updateRank(maxRank - selectIndex0.intValue, maxRank - selectIndex1.intValue)
    }

    //关闭监听
    BackHandler(openDialog.value) {
        openDialog.value = false
    }


    //选择布局
    SmallFloatingActionButton(
        modifier = Modifier
            .animateContentSize(defaultSpring())
            .padding(
                start = Dimen.fabMarginSecondLineEnd,
                end = Dimen.fabMarginSecondLineEnd,
                bottom = Dimen.fabMarginLargeBottom
            ),
        shape = if (openDialog.value) MaterialTheme.shapes.medium else CircleShape,
        onClick = {
            //点击展开布局
            if (!openDialog.value) {
                VibrateUtil(context).single()
                openDialog.value = true
            }
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (openDialog.value) {
                Dimen.popupMenuElevation
            } else {
                Dimen.fabElevation
            }
        ),
    ) {
        if (openDialog.value) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = Dimen.mediumPadding,
                        vertical = Dimen.largePadding
                    )
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Bottom
            ) {
                //当前
                MainText(text = stringResource(id = R.string.cur_rank))
                RankSelectItem(
                    selectIndex = selectIndex0,
                    rankList = rankList,
                    targetType = RankSelectType.DEFAULT,
                    currentRank = maxRank - selectIndex0.intValue
                )
                //目标
                MainText(
                    text = stringResource(id = R.string.target_rank),
                    modifier = Modifier.padding(top = Dimen.largePadding)
                )
                RankSelectItem(
                    selectIndex = selectIndex1,
                    rankList = rankList,
                    targetType = type,
                    currentRank = maxRank - selectIndex0.intValue
                )
            }
        } else {
            //fab
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Dimen.largePadding)
            ) {
                MainIcon(
                    data = MainIconType.RANK_SELECT,
                    size = Dimen.fabIconSize
                )
                Text(
                    text = stringResource(id = R.string.rank_select),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding, end = Dimen.largePadding
                    )
                )
            }
        }
    }

}


/**
 * 日期选择弹窗
 *
 * @param pickStartLimit    限制开始时间可选择范围，开始时间必须小于结束时间
 * @param pickEndLimit      限制结束时间可选择范围，结束时间必须大于开始时间
 */
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainDatePickerDialog(
    datePickerState: DatePickerState,
    openDialog: MutableState<Boolean>,
    pickStartLimit: Long? = null,
    pickEndLimit: Long? = null,
) {

    if (openDialog.value) {
        //日期是否可确认选择判断
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }

        DatePickerDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text(stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                dateValidator = {
                    var pickStartLimitFlag = true
                    var pickEndLimitFlag = true
                    //选择开始日期时
                    if (pickStartLimit != null) {
                        pickStartLimitFlag = it < pickStartLimit
                    }
                    //选择结束日期时
                    if (pickEndLimit != null) {
                        pickEndLimitFlag = it > pickEndLimit
                    }
                    return@DatePicker pickEndLimitFlag && pickStartLimitFlag
                }
            )
        }
    }
}

/**
 * 日期范围
 */
data class DateRange(
    val startDate: String = "",
    val endDate: String = ""
) {
    /**
     * 是否筛选判断
     */
    fun hasFilter() = startDate != "" || endDate != ""

    /**
     * 筛选条件，判断开始时间是否在范围内
     */
    fun predicate(start: String): Boolean {
        var startFlag = true
        var endFlag = true
        //大于开始时间
        if (startDate != "") {
            startFlag = start.formatTime.fixJpTime.second(startDate) > 0
        }
        //小于结束时间
        if (endDate != "") {
            endFlag = start.formatTime.fixJpTime.second(endDate) < 0
        }

        return startFlag && endFlag
    }

    override fun toString(): String {
        return "$startDate|$endDate"
    }
}

/**
 * 获取日期选择年份范围
 */
fun getDatePickerYearRange(): IntRange {
    val maxYear = getYear()
    return IntRange(2018, maxYear)
}


/**
 * RANK 选择器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankSelectItem(
    selectIndex: MutableState<Int>,
    rankList: List<Int>,
    targetType: RankSelectType,
    currentRank: Int
) {
    val context = LocalContext.current

    VerticalGrid(
        itemWidth = Dimen.rankTextWidth,
        contentPadding = Dimen.mediumPadding,
        isSubLayout = true
    ) {

        rankList.filter {
            targetType == RankSelectType.DEFAULT ||
                    (targetType == RankSelectType.LIMIT && it >= currentRank)
        }.forEachIndexed { index, rank ->
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

                val rankColor = getRankColor(rank = rank)
                val selected = selectIndex.value == index


                ElevatedFilterChip(
                    selected = selected,
                    onClick = {
                        VibrateUtil(context).single()
                        selectIndex.value = index
                    },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        selectedContainerColor = rankColor
                    ),
                    label = {
                        CaptionText(
                            text = rankFillBlank(rank),
                            color = if (selected) colorWhite else rankColor
                        )
                    }
                )
            }
        }

    }

}

/**
 * 填充空格
 */
private fun rankFillBlank(rank: Int): String {
    return when (rank) {
        in 0..9 -> "0$rank"
        else -> "$rank"
    }
}
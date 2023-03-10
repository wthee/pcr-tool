package cn.wthee.pcrtool.ui.common

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.theme.Dimen
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
                IconCompose(data = icon, wrapSize = true)
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
    dateRange: MutableState<DateRange>
) {
    val context = LocalContext.current
    val openDialog = navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val yearRange = getDatePickerYearRange()
    val dateRangePickerState = rememberDateRangePickerState(yearRange = yearRange)

    //关闭监听
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.BACK
    if (close) {
        navViewModel.openChangeDataDialog.postValue(false)
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.BACK) {
        navViewModel.openChangeDataDialog.postValue(false)
    }


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

        dateRange.value = DateRange(
            startDate = startDate ?: "",
            endDate = endDate ?: ""
        )
    }


    Box(modifier = Modifier.clickClose(openDialog)) {
        //日期选择布局
        SmallFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .animateContentSize(defaultSpring())
                .padding(
                    start = Dimen.fabMargin,
                    end = Dimen.fabMargin,
                    bottom = Dimen.fabMargin * 2 + Dimen.fabSize
                )
                .padding(start = Dimen.textfabMargin, end = Dimen.textfabMargin),
            shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
            onClick = {
                //点击展开布局
                if (!openDialog) {
                    VibrateUtil(context).single()
                    navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                    navViewModel.openChangeDataDialog.postValue(true)
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
                        .fillMaxHeight(0.85f)
                        .imePadding(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    //日期选择
                    DateRangePicker(
                        state = dateRangePickerState,
                        showModeToggle = false,
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                //标题
                                DateRangePickerDefaults.DateRangePickerTitle(
                                    state = dateRangePickerState,
                                    contentPadding = PaddingValues(Dimen.smallPadding)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                //重置选择
                                if (dateRange.value.hasFilter()) {
                                    IconTextButton(
                                        icon = MainIconType.RESET,
                                        text = stringResource(id = R.string.reset),
                                    ) {
                                        dateRange.value = DateRange()
                                        //重置选择器状态
                                        dateRangePickerState.setSelection(null, null)
                                        navViewModel.fabCloseClick.postValue(true)
                                    }
                                }
                            }
                        },
                        headline = {
                            DateRangePickerDefaults.DateRangePickerHeadline(
                                state = dateRangePickerState,
                                dateFormatter = DatePickerFormatter(
                                    selectedDateSkeleton = "yyyy/MM/dd"
                                ),
                                contentPadding = PaddingValues(Dimen.smallPadding)
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
                    IconCompose(
                        data = if (dateRange.value.hasFilter()) {
                            MainIconType.DATE_RANGE_PICKED
                        } else {
                            MainIconType.DATE_RANGE_NONE
                        },
                        size = Dimen.fabIconSize
                    )
                    Text(
                        text = if (dateRange.value.hasFilter()) {
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
private fun getDatePickerYearRange(): IntRange {
    val maxYear = getYear() + 1
    return IntRange(2018, maxYear)
}
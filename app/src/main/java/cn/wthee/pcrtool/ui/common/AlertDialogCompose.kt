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
fun BoxScope.DateRangePickerCompose(
    dateRange: MutableState<DateRange>
) {
    val openLayout = navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.BACK
    //关闭监听
    if (close) {
        navViewModel.openChangeDataDialog.postValue(false)
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.BACK) {
        navViewModel.openChangeDataDialog.postValue(false)
    }

    val context = LocalContext.current
    val openStartDateDialog = remember { mutableStateOf(false) }
    val openEndDateDialog = remember { mutableStateOf(false) }
    val yearRange = getDatePickerYearRange()
    val startDatePickerState = rememberDatePickerState(yearRange = yearRange)
    val endDatePickerState = rememberDatePickerState(yearRange = yearRange)

    //更新日期
    LaunchedEffect(openStartDateDialog.value, openEndDateDialog.value) {
        dateRange.value = DateRange(
            startDate = startDatePickerState.selectedDateMillis?.simpleDateFormatUTC ?: "",
            endDate = endDatePickerState.selectedDateMillis?.simpleDateFormatUTC ?: ""
        )
    }


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
        shape = if (openLayout) MaterialTheme.shapes.medium else CircleShape,
        onClick = {
            //点击展开布局
            if (!openLayout) {
                VibrateUtil(context).single()
                navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                navViewModel.openChangeDataDialog.postValue(true)
            }
        }
    ) {
        if (openLayout) {
            Box(
                modifier = Modifier.padding(
                    horizontal = Dimen.mediumPadding,
                    vertical = Dimen.largePadding
                )
            ) {
                Column {
                    //标题
                    MainText(text = stringResource(id = R.string.range_date))

                    //选择开始日期
                    SubButton(
                        text = if (dateRange.value.startDate == "") {
                            stringResource(id = R.string.pick_start_date)
                        } else {
                            dateRange.value.startDate
                        },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = Dimen.mediumPadding)
                            .fillMaxWidth()
                    ) {
                        openStartDateDialog.value = true
                    }

                    //选择结束日期
                    SubButton(
                        text = if (dateRange.value.endDate == "") {
                            stringResource(id = R.string.pick_end_date)
                        } else {
                            dateRange.value.endDate
                        },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = Dimen.mediumPadding)
                            .fillMaxWidth()
                    ) {
                        openEndDateDialog.value = true
                    }
                }

                //重置
                if (dateRange.value.hasFilter()) {
                    IconTextButton(
                        icon = MainIconType.RESET,
                        text = stringResource(id = R.string.reset),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        dateRange.value = DateRange()
                        navViewModel.fabCloseClick.postValue(true)
                    }
                }
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

    //开始时间，需小于结束时间
    if (openStartDateDialog.value) {
        MainDatePickerDialog(
            startDatePickerState,
            openStartDateDialog,
            pickStartLimit = endDatePickerState.selectedDateMillis
        )
    }

    //结束时间，需大于开始时间
    if (openEndDateDialog.value) {
        MainDatePickerDialog(
            endDatePickerState,
            openEndDateDialog,
            pickEndLimit = startDatePickerState.selectedDateMillis
        )
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
                datePickerState = datePickerState,
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
}

/**
 * 获取日期选择年份范围
 */
private fun getDatePickerYearRange(): IntRange {
    val maxYear = getYear() + 1
    return IntRange(2018, maxYear)
}
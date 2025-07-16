package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.fixTimeZone
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getYear
import cn.wthee.pcrtool.utils.second
import cn.wthee.pcrtool.utils.simpleDateFormatUTC


/**
 * 选择日期范围组件
 * fixme 放到fab时背景颜色调整
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
    ExpandableFab(
        expanded = openDialog,
        onClick = {
            changeDialog(true)
        },
        icon = if (dateRange.hasFilter()) {
            MainIconType.DATE_RANGE_PICKED
        } else {
            MainIconType.DATE_RANGE_NONE
        },
        text = if (dateRange.hasFilter()) {
            stringResource(id = R.string.picked_date)
        } else {
            stringResource(id = R.string.pick_date)
        },
        isSecondLineFab = true
    ) {
        //日期选择
        DateRangePicker(
            modifier = Modifier.padding(Dimen.smallPadding),
            colors = DatePickerDefaults.colors(
                //设置内部透明
                containerColor = Color.Transparent
            ),
            state = dateRangePickerState,
            showModeToggle = true,
            title = {},
            headline = {
                //调整字体大小
                ProvideTextStyle(
                    value = MaterialTheme.typography.titleMedium
                ) {
                    DateRangePickerDefaults.DateRangePickerHeadline(
                        selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                        selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                        displayMode = dateRangePickerState.displayMode,
                        dateFormatter = remember { DatePickerDefaults.dateFormatter() },
                        modifier = Modifier.padding(
                            PaddingValues(
                                start = Dimen.largePadding,
                                end = Dimen.mediumPadding,
                                bottom = Dimen.mediumPadding
                            )
                        )
                    )
                }

            }
        )
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
     * 筛选条件，判断开始时间~结束时间，是否在选择的开始时间结束时间范围内
     */
    fun predicate(start: String, end: String): Boolean {
        var inRange = false

        if (startDate != "" && endDate == "") {
            //进选择开始时间，只展示当前日期的
            //开始时间<= 选择的时间 <=结束时间
            inRange = start.formatTime.fixTimeZone.second(startDate) <= 0
                    && end.formatTime.fixTimeZone.second(startDate) >= 0
        } else if (startDate != "") {
            //结束时间在选择范围内：选择的开始时间<= 结束时间 <= 选择的结束时间
            var flag1 = (end.formatTime.fixTimeZone.second(endDate) <= 0
                    && end.formatTime.fixTimeZone.second(startDate) >= 0)
            //开始时间在选择范围内：选择的开始时间<= 开始时间 <= 选择的结束时间
            var flag2 = (start.formatTime.fixTimeZone.second(endDate) <= 0
                    && end.formatTime.fixTimeZone.second(startDate) >= 0)

            inRange = flag1 || flag2
        }

        return inRange
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

@OptIn(ExperimentalMaterial3Api::class)
@CombinedPreviews
@Composable
private fun DateRangePickerComposePreview() {
    PreviewLayout(themeType = 1) {
        DateRangePickerCompose(
            dateRangePickerState = rememberDateRangePickerState(),
            dateRange = DateRange(),
            openDialog = true,
            changeRange = {},
            changeDialog = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@CombinedPreviews
@Composable
private fun DateRangePickerCompose2Preview() {
    PreviewLayout(themeType = 2) {
        DateRangePickerCompose(
            dateRangePickerState = rememberDateRangePickerState(),
            dateRange = DateRange(),
            openDialog = true,
            changeRange = {},
            changeDialog = {}
        )
    }
}
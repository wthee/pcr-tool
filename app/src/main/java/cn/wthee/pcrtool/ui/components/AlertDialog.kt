package cn.wthee.pcrtool.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
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
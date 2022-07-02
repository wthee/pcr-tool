package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.intArrayList
import java.util.*

/**
 *  生日
 */
data class BirthdayData(
    @ColumnInfo(name = "birth_month_int") val month: Int = 0,
    @ColumnInfo(name = "birth_day_int") val day: Int = 0,
    @ColumnInfo(name = "unit_ids") val unitIds: String = "100101",
    @ColumnInfo(name = "unit_names") val unitNames: String = "",
) {

    private fun getDate(): String {
        val c = Calendar.getInstance()
        c.time = Date(System.currentTimeMillis())
        c.timeZone = TimeZone.getTimeZone("GMT+8:00");
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH) + 1
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        if (month < mMonth || (month == mMonth && day < mDay) || month == 999) {
            //过去的日期
            return "${mYear + 1}/$month/$day"
        } else {
            return "${mYear}/$month/$day"

        }
    }

    fun getStartTime() = getDate() + " 00:00:00"

    fun getEndTime() = getDate() + " 23:59:59"
}

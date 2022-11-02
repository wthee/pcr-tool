package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.utils.stringArrayList
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

    /**
     * 按角色名重新升序排序
     */
    fun getOrderUnitIdList(): ArrayList<Int> {
        val icons = unitIds.intArrayList
        val names = unitNames.stringArrayList
        val list = arrayListOf<UnitInfo>()
        icons.forEachIndexed { index, id ->
            list.add(UnitInfo(id, names[index]))
        }
        list.sortWith() { o1, o2 ->
            o1.unitName.compareTo(o2.unitName)
        }
        val idList = arrayListOf<Int>()
        list.forEach {
            idList.add(it.unitId)
        }
        return idList
    }

}

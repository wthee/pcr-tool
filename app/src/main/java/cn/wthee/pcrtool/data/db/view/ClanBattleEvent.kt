package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.df1
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getString
import java.util.*

/**
 * 公会战日程
 */
data class ClanBattleEvent(
    @ColumnInfo(name = "id") var id: Int = 1,
    @ColumnInfo(name = "release_month") var releaseMonth: Int = 0,
    @ColumnInfo(name = "start_time") var startTime: String = "2020/01/01 00:00:00",
    @Ignore var endTime: String = "2020/01/07 00:00:00"
) {
    /**
     * 获取id
     */
    fun getClanBattleId() = id - 60000

    /**
     * 获取结束日期，开始加5天
     */
    fun getFixedEndTime(): String {
        val st = startTime.formatTime

        return calcClanBattleEndTime(st)
    }

    /**
     * 获取描述
     */
    fun getDesc(): String {
        return getString(R.string.clan_battle_month, releaseMonth)
    }


    /**
     * 计算公会战结束日期
     */
    private fun calcClanBattleEndTime(date: String): String {
        val now = Calendar.getInstance()
        df1.parse(date)?.let {
            now.time = it
        }
        now[Calendar.DATE] = now[Calendar.DATE] + 5
        now[Calendar.HOUR_OF_DAY] = now[Calendar.HOUR_OF_DAY] - 5
        now[Calendar.SECOND] = now[Calendar.SECOND] - 1

        return df1.format(now.time)
    }
}
package cn.wthee.pcrtool.data.db.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getString

/**
 * 免费十连卡池记录
 */
data class FreeGachaInfo(
    @ColumnInfo(name = "id") val id: Int = 1,
    @ColumnInfo(name = "max_count") val maxCount: Int = 0,
    @ColumnInfo(name = "start_time") val startTime: String = "2020/01/01 00:00:00",
    @ColumnInfo(name = "end_time") val endTime: String = "2020/01/07 00:00:00"
) {

    /**
     * 获取描述
     */
    fun getDesc() = getString(R.string.free_gacha_content, getCount())

    /**
     * 获取描述
     */
    @Composable
    fun getDescComposable() = stringResource(R.string.free_gacha_content, getCount())

    /**
     * 获取次数
     */
    private fun getCount() = if (maxCount != 0) {
        maxCount
    } else {
        val st = startTime.formatTime
        val ed = endTime.formatTime
        ed.days(st, false).toInt()
    }
}
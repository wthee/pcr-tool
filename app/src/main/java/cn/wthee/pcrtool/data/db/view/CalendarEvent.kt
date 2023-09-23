package cn.wthee.pcrtool.data.db.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.CalendarEventType
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 活动信息
 */
data class CalendarEvent(
    @ColumnInfo(name = "type") val type: String = "",
    @ColumnInfo(name = "value") val value: Int = 1500,
    @ColumnInfo(name = "start_time") val startTime: String = "2021-01-01 00:00:00",
    @ColumnInfo(name = "end_time") val endTime: String = "2021-01-07 00:00:00",
) {
    /*
     * 去零
     */
    private fun getFixedValue() = if (value % 1000 != 0) {
        (value / 1000f)
    } else {
        (value / 1000).toFloat()
    }

    private fun getTypeInt() = try {
        type.toInt()
    } catch (_: Exception) {
        404
    }

    @Composable
    fun getEventList(): ArrayList<CalendarEventData> {
        val events = arrayListOf<CalendarEventData>()
        when (CalendarEventType.getByValue(getTypeInt())) {
            CalendarEventType.TOWER -> {
                //露娜塔
                events.add(
                    CalendarEventData(
                        stringResource(id = R.string.tower),
                        "",
                        ""
                    )
                )
            }

            CalendarEventType.SP_DUNGEON -> {
                //特殊地下城
                events.add(
                    CalendarEventData(
                        stringResource(id = R.string.sp_dungeon),
                        "",
                        ""
                    )
                )
            }

            CalendarEventType.TDF -> {
                //次元断层
                events.add(
                    CalendarEventData(
                        stringResource(id = R.string.fault),
                        "",
                        ""
                    )
                )
            }

            CalendarEventType.COLOSSEUM -> {
                //次元断层
                events.add(
                    CalendarEventData(
                        stringResource(id = R.string.colosseum),
                        "",
                        ""
                    )
                )
            }

            else -> {
                //正常活动
                val list = type.intArrayList
                list.forEach { type ->
                    val title = when (CalendarEventType.getByValue(type)) {
                        CalendarEventType.DAILY -> stringResource(id = R.string.daily_mission)
                        CalendarEventType.LOGIN -> stringResource(id = R.string.daily_login)
                        CalendarEventType.FORTUNE -> getString(R.string.fortune_event)
                        CalendarEventType.N_DROP, CalendarEventType.N_MANA -> stringResource(id = R.string.normal)
                        CalendarEventType.H_DROP, CalendarEventType.H_MANA -> stringResource(id = R.string.hard)
                        CalendarEventType.VH_DROP, CalendarEventType.VH_MANA -> stringResource(id = R.string.very_hard)
                        CalendarEventType.EXPLORE -> stringResource(id = R.string.explore)
                        CalendarEventType.SHRINE -> stringResource(id = R.string.shrine)
                        CalendarEventType.TEMPLE -> stringResource(id = R.string.temple)
                        CalendarEventType.DUNGEON -> stringResource(id = R.string.dungeon)
                        else -> ""
                    }

                    val dropMumColor = when (getFixedValue()) {
                        1.5f, 2.0f -> colorGold
                        2.5f, 3f -> colorRed
                        4f -> colorGreen
                        5f -> colorOrange
                        else -> MaterialTheme.colorScheme.primary
                    }
                    val multiple = getFixedValue()
                    events.add(
                        CalendarEventData(
                            title,
                            when (CalendarEventType.getByValue(type)) {
                                CalendarEventType.LOGIN -> {
                                    //登录宝石
                                    value.toString()
                                }

                                CalendarEventType.FORTUNE -> {
                                    //兰德索尔杯
                                    ""
                                }

                                else -> stringResource(
                                    R.string.multiple,
                                    if ((multiple * 10).toInt() % 10 == 0) {
                                        multiple.toInt().toString()
                                    } else {
                                        multiple.toString()
                                    }
                                )
                            },
                            when (CalendarEventType.getByValue(type)) {
                                CalendarEventType.DAILY, CalendarEventType.LOGIN, CalendarEventType.FORTUNE -> ""
                                else -> stringResource(id = if (type > 40) R.string.mana else R.string.drop)
                            },
                            dropMumColor
                        )
                    )
                }
            }
        }

        return events
    }

}

data class CalendarEventData(
    val title: String,
    val multiple: String,
    val info: String,
    val color: Color = Color.Unspecified,
)
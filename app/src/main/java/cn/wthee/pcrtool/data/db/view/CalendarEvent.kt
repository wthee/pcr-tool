package cn.wthee.pcrtool.data.db.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 活动信息
 */
data class CalendarEvent(
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "type") val type: String = "31",
    @ColumnInfo(name = "value") val value: Int = 1500,
    @ColumnInfo(name = "start_time") val startTime: String = "2021-01-01 00:00:00",
    @ColumnInfo(name = "end_time") val endTime: String = "2021-01-07 00:00:00",
) {
    /*
     * 去零
     */
    fun getFixedValue() = if (value % 1000 != 0) {
        (value / 1000f)
    } else {
        (value / 1000).toFloat()
    }

    @Composable
    fun getEventList(): ArrayList<CalendarEventData> {
        val events = arrayListOf<CalendarEventData>()
        when (type) {
            "1" -> {
                //露娜塔
                events.add(
                    CalendarEventData(
                        stringResource(id = R.string.tower),
                        "",
                        ""
                    )
                )
            }
            "-1" -> {
                //特殊地下城
                events.add(
                    CalendarEventData(
                        stringResource(id = R.string.sp_dungeon),
                        "",
                        ""
                    )
                )
            }
            else -> {
                //正常活动
                val list = type.intArrayList
                list.forEach { type ->
                    val title = when (type) {
                        18 -> stringResource(id = R.string.daily_mission)
                        19 -> stringResource(id = R.string.daily_login)
                        20 -> getString(R.string.fortune_event)
                        31, 41 -> stringResource(id = R.string.normal)
                        32, 42 -> stringResource(id = R.string.hard)
                        39, 49 -> stringResource(id = R.string.very_hard)
                        34 -> stringResource(id = R.string.explore)
                        37 -> stringResource(id = R.string.shrine)
                        38 -> stringResource(id = R.string.temple)
                        45 -> stringResource(id = R.string.dungeon)
                        else -> ""
                    }

                    val dropMumColor = when (getFixedValue()) {
                        1.5f, 2.0f -> colorGold
                        3f -> colorRed
                        4f -> colorGreen
                        else -> MaterialTheme.colorScheme.primary
                    }
                    val multiple = getFixedValue()
                    events.add(
                        CalendarEventData(
                            title,
                            when (type) {
                                19 -> {
                                    //每日登录宝石
                                    value.toString()
                                }
                                20 -> {
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
                            when (type) {
                                18, 19, 20 -> ""
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

    override fun toString(): String {
        var eventTitle = ""
        when (type) {
            "1" -> {
                //露娜塔
                eventTitle = getString(R.string.tower)
            }
            "-1" -> {
                //特殊地下城
                eventTitle = getString(R.string.sp_dungeon)
            }
            else -> {
                //正常活动
                val list = type.intArrayList
                list.forEachIndexed { index, type ->
                    val title = when (type) {
                        18 -> getString(R.string.daily_mission)
                        19 -> getString(R.string.daily_login)
                        20 -> getString(R.string.fortune_event)
                        31, 41 -> getString(R.string.normal)
                        32, 42 -> getString(R.string.hard)
                        39, 49 -> getString(R.string.very_hard)
                        34 -> getString(R.string.explore)
                        37 -> getString(R.string.shrine)
                        38 -> getString(R.string.temple)
                        45 -> getString(R.string.dungeon)
                        else -> ""
                    }
                    val multiple = getFixedValue()
                    val typeName = when (type) {
                        18, 19, 20 -> ""
                        else -> getString(if (type > 40) R.string.mana else R.string.drop)
                    }
                    val multipleText = getString(
                        R.string.multiple,
                        if ((multiple * 10).toInt() % 10 == 0) {
                            multiple.toInt().toString()
                        } else {
                            multiple.toString()
                        }
                    )
                    eventTitle += when (type) {
                        19 -> {
                            //每日登录宝石
                            title + value
                        }
                        20 -> {
                            //兰德索尔杯
                            title
                        }
                        else -> title + typeName + multipleText
                    }
                    if (index != list.size - 1) {
                        eventTitle += "\n"
                    }
                }
            }
        }
        return eventTitle
    }
}

data class CalendarEventData(
    val title: String,
    val multiple: String,
    val info: String,
    val color: Color = Color.Unspecified,
)
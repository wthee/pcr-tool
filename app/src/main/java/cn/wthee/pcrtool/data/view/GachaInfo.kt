package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo

/**
 * 卡池记录
 */
data class GachaInfo(
    @ColumnInfo(name = "gacha_id") val gachaId: Int,
    @ColumnInfo(name = "gacha_name") val gachaName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "unit_ids") val unitIds: String
) {

    /**
     * 获取卡池描述
     */
    fun getDesc() = description.replace("\\n", "\n")

    /**
     * 获取开始结束时间
     */
    fun getDate() = startTime.substring(0, 10) + " ~ " + endTime.substring(0, 10)

    /**
     * 获取卡池类型
     */
    fun getType() = when (gachaName) {
        "ピックアップガチャ" -> "PICK UP 扭蛋"
        "プライズガチャ" -> "复刻扭蛋"
        "プリンセスフェス" -> "公主庆典"
        else -> gachaName
    }.replace("ガチャ", " 扭蛋")
}
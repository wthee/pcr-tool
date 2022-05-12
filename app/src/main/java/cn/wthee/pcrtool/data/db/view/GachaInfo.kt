package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.data.enums.GachaType
import cn.wthee.pcrtool.utils.deleteSpace

/**
 * 卡池记录
 */
data class GachaInfo(
    @ColumnInfo(name = "gacha_id") val gachaId: Int = 1,
    @ColumnInfo(name = "gacha_name") val gachaName: String = "???",
    @ColumnInfo(name = "description") val description: String = "???",
    @ColumnInfo(name = "start_time") val startTime: String = "2020/01/01 00:00:00",
    @ColumnInfo(name = "end_time") val endTime: String = "2020/01/07 00:00:00",
    @ColumnInfo(name = "unit_ids") val unitIds: String = "100101"
) {

    /**
     * 获取卡池描述
     */
    fun getDesc() = description.deleteSpace

    /**
     * 获取卡池类型
     */
    fun getType() = when (gachaName) {
        "ピックアップガチャ", "精選轉蛋", "限定精選轉蛋" -> GachaType.PICK_UP
        "プライズガチャ", "獎勵轉蛋", "附奖扭蛋" -> GachaType.RE
        "プリンセスフェス", "公主祭典" -> GachaType.FES
        else -> {
            if (gachaName.contains("Anniversary") || gachaName.contains("周年")) {
                GachaType.ANNIV
            } else {
                GachaType.UNKNOWN
            }
        }
    }

    fun fixTypeName() = gachaName.replace("ガチャ", "").replace("扭蛋", "").replace("轉蛋", "")
}
package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import cn.wthee.pcrtool.data.db.dao.limitedIds
import cn.wthee.pcrtool.data.enums.GachaType
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 卡池记录
 */
data class GachaInfo(
    @ColumnInfo(name = "gacha_id") val gachaId: Int = 1,
    @ColumnInfo(name = "gacha_name") val gachaName: String = "???",
    @ColumnInfo(name = "description") val description: String = "???",
    @ColumnInfo(name = "start_time") val startTime: String = "2020/01/01 00:00:00",
    @ColumnInfo(name = "end_time") val endTime: String = "2020/01/07 00:00:00",
    @ColumnInfo(name = "unit_ids") val unitIds: String = "100101",
    @ColumnInfo(name = "is_limiteds") val isLimiteds: String = "0-0",
) {

    /**
     * 获取卡池描述
     */
    fun getDesc() = description.deleteSpace

    /**
     * 获取卡池类型
     */
    fun getType() = when (gachaName) {
        "ピックアップガチャ", "精選轉蛋", "限定精選轉蛋", "精选扭蛋", "PICK UP扭蛋" -> {
            if (isLimited()) {
                GachaType.LIMIT
            } else {
                GachaType.NORMAL
            }
        }
        "プライズガチャ", "獎勵轉蛋", "附奖扭蛋" -> {
            if (isLimited()) {
                GachaType.RE_LIMIT
            } else {
                GachaType.RE_NORMAL
            }
        }
        "プリンセスフェス", "公主祭典" -> GachaType.FES
        else -> {
            if (gachaName.contains("Anniversary") || gachaName.contains("周年")) {
                GachaType.ANNIV
            } else {
                GachaType.UNKNOWN
            }
        }
    }

    /**
     * 调整卡池类型文本
     */
    fun fixTypeName() = gachaName.replace("ガチャ", "").replace("扭蛋", "").replace("轉蛋", "")

    /**
     * 是否包含限定 fixme 8月台服更新后，可取消判断
     */
    fun isLimited(): Boolean {
        val limitIdsTw = arrayListOf(
            106101,
            107001,
            107101,
            107501,
            107701,
            107801,
            107901,
            108101,
            108301,
            108401,
            108601,
            108701,
            108801,
            109101,
            110001,
            110301,
            110401,
            110601
        )
        var isLimit = false
        unitIds.intArrayList.forEach {
            isLimit = limitIdsTw.contains(it)
            if (isLimit) return@forEach
        }
        return isLimit || isLimiteds.intArrayList.contains(1)
    }
}

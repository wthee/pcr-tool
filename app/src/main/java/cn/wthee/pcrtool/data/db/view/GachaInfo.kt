package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.data.enums.GachaType
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.utils.stringArrayList

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
    @ColumnInfo(name = "unit_names") val unitNames: String = "",
    @ColumnInfo(name = "is_limiteds") val isLimiteds: String = "0-0",
    @ColumnInfo(name = "is_ups") val isUps: String = "0-0",
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
        "プリンセスフェス", "公主祭典", "公主庆典" -> GachaType.FES
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
     * 是否包含限定  国服环奈
     * 调整时需注意同步调整
     * @see [cn.wthee.pcrtool.data.db.dao.UnitDao]
     */
    private fun isLimited(): Boolean {
        val limitIdsCn = arrayListOf(
            170101,
            170201
        )
        var isLimit = false
        unitIds.intArrayList.forEach {
            isLimit = limitIdsCn.contains(it)
            if (isLimit) return@forEach
        }
        return isLimit || isLimiteds.intArrayList.contains(1)
    }

    /**
     * 获取模拟抽卡角色信息
     */
    fun getMockGachaUnitList(): List<GachaUnitInfo> {
        val ids = unitIds.intArrayList
        val names = unitNames.stringArrayList
        val isLimits = isLimiteds.intArrayList
        val upIds = isUps.intArrayList
        val list = arrayListOf<GachaUnitInfo>()
        ids.forEachIndexed { index, id ->
            if (ids.size <= 4 || upIds[index] > 0) {
                //正常卡池、或fes卡池up的角色
                list.add(
                    GachaUnitInfo(id, names[index], isLimits[index], 3)
                )
            }
        }
        return list
    }
}

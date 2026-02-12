package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import cn.wthee.pcrtool.data.enums.GachaType
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 卡池记录
 */
open class GachaHistoryInfo(
    @ColumnInfo(name = "gacha_id") var gachaId: Int = -1,
    @ColumnInfo(name = "gacha_name") var gachaName: String = "???",
    @ColumnInfo(name = "description") var description: String = "???",
    @ColumnInfo(name = "start_time") var startTime: String = "2020/01/01 00:00:00",
    @ColumnInfo(name = "end_time") var endTime: String = "2020/01/07 00:00:00",
    @ColumnInfo(name = "ids") var ids: String = "100101",
    @ColumnInfo(name = "unit_ids") var unitIds: String = "100101",
    @ColumnInfo(name = "unit_names") var unitNames: String = "",
    @ColumnInfo(name = "is_limiteds") var isLimiteds: String = "0-0",
    @ColumnInfo(name = "is_ups") var isUps: String = "0-0"
) {
    /**
     * 转换数据
     */
    fun covertData(): GachaInfo {
        val unitList = arrayListOf<GachaExchangeUnit>()
        val ids = this.ids.intArrayList
        val unitIds = this.unitIds.intArrayList
        val unitNames = this.unitNames.split("-")
        val isLimiteds = this.isLimiteds.intArrayList
        val isUps = this.isUps.intArrayList
        // 首先，我们需要获取keys数组中元素的索引，然后按照keys数组中的值进行排序
        val sortedIndices = ids.withIndex().sortedBy { it.value }.map { it.index }
        sortedIndices.forEach {
            unitList.add(
                GachaExchangeUnit(
                    id = ids[it],
                    unitId = unitIds[it],
                    unitName = unitNames[it],
                    isLimited = isLimiteds[it],
                    isUp = isUps[it],
                )
            )
        }

        return GachaInfo(
            unitList = unitList,
            gachaId = gachaId,
            gachaName = gachaName,
            description = description,
            startTime = startTime,
            endTime = endTime,
        )
    }
}


/**
 * 卡池记录
 */
class GachaInfo(
    //重新排序后的角色列表
    var unitList: List<GachaExchangeUnit> = emptyList(),
    var gachaId: Int = 0,
    var gachaName: String = "",
    var description: String = "",
    var startTime: String = "",
    var endTime: String = "",
) {


    /**
     * 获取卡池描述 fixme 优化判断逻辑
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
            } else if (gachaName.contains("選べるプライズ")
                || gachaName.contains("选择")
                || gachaName.contains("自选")
                || gachaName.contains("自選")
            ) {
                if (gachaName.contains("自选精选")) {
                    GachaType.LIMIT_PICK
                } else {
                    GachaType.RE_LIMIT_PICK
                }
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
        val isLimiteds = unitList.map { it.isLimited }
        unitList.map { it.unitId }.forEach {
            isLimit = limitIdsCn.contains(it)
            if (isLimit) return@forEach
        }
        return isLimit || isLimiteds.contains(1)
    }

    /**
     * 获取模拟抽卡up角色信息
     */
    fun getMockGachaPickUpUnitList(): List<GachaUnitInfo> {
        val upIds = unitList.map { it.isUp }
        val list = arrayListOf<GachaUnitInfo>()
        //无大于0的，全部添加
        val addAll = upIds.none { it > 0 }
        //遍历卡池角色
        unitList.forEach { unitInfo ->
            if (addAll || unitInfo.isUp > 0) {
                //正常卡池、或fes卡池up的角色
                list.add(
                    GachaUnitInfo(unitInfo.unitId, unitInfo.unitName, unitInfo.isLimited, 3)
                )
            }
        }
        return list
    }
}

/**
 * 卡池中的角色相关信息
 */
data class GachaExchangeUnit(
    @Ignore
    var id: Int = 0,
    @Ignore
    var unitId: Int = 0,
    @Ignore
    var unitName: String = "",
    @Ignore
    var isLimited: Int = 0,
    @Ignore
    var isUp: Int = 0
)

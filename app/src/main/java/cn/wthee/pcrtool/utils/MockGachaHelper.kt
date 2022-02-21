package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.model.UnitsInGacha
import java.util.*

/**
 * 模拟抽卡
 * fixme 调整权重
 */
class MockGachaHelper(
    star3Sum: Double,
    pickUpType: Int,
    pickUpList: List<GachaUnitInfo>,
    unitListData: UnitsInGacha
) {
    private var categorys: MutableList<UnitWeight> = ArrayList()
    private val random = Random()
    private val totalWeight = 10000
    private val star3Weight = (star3Sum * totalWeight).toInt()
    private val pickUpWeight = 1

    init {
        //初始权重
        when (pickUpType) {
            //单
            0 -> {
                categorys.add(UnitWeight(unitListData.normal1, 60));
                categorys.add(UnitWeight(unitListData.normal2, 37));
                categorys.add(UnitWeight(unitListData.normal3, star3Weight - pickUpWeight));
                categorys.add(UnitWeight(pickUpList, pickUpWeight));
            }
            //多
            1 -> {

            }
            //FES
            2 -> {

            }
            else -> {

            }
        }
    }

    fun giveMe1500Gems(): ArrayList<GachaUnitInfo> {
        val reslutList = arrayListOf<GachaUnitInfo>()
        for (i in 0 until 10) {
            reslutList.add(getSingleResult())
        }
        return reslutList
    }

    /**
     * 随机返回
     */
    private fun getSingleResult(): GachaUnitInfo {
        var weightSum = 0
        for (wc in categorys) {
            weightSum += wc.weight
        }
        val randomNum = random.nextInt(weightSum)
        var m = 0
        for (wc in categorys) {
            if (m <= randomNum && randomNum < m + wc.weight) {
                return wc.unitBox.random()
            }
            m += wc.weight
        }
        return getSingleResult()
    }

    data class UnitWeight(var unitBox: List<GachaUnitInfo>, var weight: Int)

}
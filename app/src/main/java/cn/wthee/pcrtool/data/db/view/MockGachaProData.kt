package cn.wthee.pcrtool.data.db.view

import androidx.room.PrimaryKey

/**
 * 模拟卡池信息
 */
data class MockGachaProData(
    @PrimaryKey
    val gachaId: String = "",
    val region: Int = 0,
    val gachaType: Int = 0,
    val pickUpIds: String = "",
    val createTime: String = "",
    var lastUpdateTime: String = "",
    var resultUnitIds: String = "",
    var resultUnitRaritys: String = "",
)
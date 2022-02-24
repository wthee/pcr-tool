package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 模拟卡池信息
 */
@Entity(tableName = "gacha_data")
data class MockGachaData(
    @PrimaryKey
    val gachaId: String,
    val region: Int,
    val gachaType: Int,
    val pickUpIds: String,
    val createTime: String,
    val lastUpdateTime: String
)
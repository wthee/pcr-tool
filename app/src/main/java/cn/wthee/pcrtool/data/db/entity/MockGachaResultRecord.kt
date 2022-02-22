package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 模拟卡池抽取结果记录信息
 */
@Entity(tableName = "gacha_result_record")
data class MockGachaResultRecord(
    @PrimaryKey
    val resultId: String,
    val gachaId: String,
    val unitIds: String,
    val unitRaritys: String,
    val createTime: String,
)

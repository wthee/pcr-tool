package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * 道具掉落信息
 */
@Entity(tableName = "odds_name_data")
data class OddsNameData(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "odds_file") val oddsFile: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon_type") val iconType: Int,
    @ColumnInfo(name = "description") val description: String,
) : Serializable
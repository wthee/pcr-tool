package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 小屋交流基本信息
 */
data class RoomCommentData(
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "unit_name") val unitName: String = "",
    @ColumnInfo(name = "room_comments") val roomComment: String = ""
) {
    fun getCommentList() = roomComment.replace("\\n", "").split("-")
}
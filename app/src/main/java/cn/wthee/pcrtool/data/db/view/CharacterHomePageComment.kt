package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo


/**
 * 角色交流信息
 */
data class CharacterHomePageComment(
    @ColumnInfo(name = "unit_id") val unitId: Int = 100101,
    @ColumnInfo(name = "comments") val comments: String = "",
) {

    /**
     * 交流
     */
    fun getCommentList() =
        comments.replace("\\n", "").split("-")


}
package cn.wthee.pcrtool.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.db.entity.ComicData

/**
 * 推特信息 DAO
 */
@Dao
interface ComicDao {

    /**
     * 更新
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<ComicData>)

    /**
     * 获取
     */
    @Query("SELECT * FROM comic WHERE title LIKE '%' || :query || '%' ORDER BY id DESC")
    fun pagingSource(query: String): PagingSource<Int, ComicData>

    /**
     * 清空数据
     */
    @Query("DELETE FROM comic WHERE title LIKE '%' || :query || '%'")
    suspend fun deleteByQuery(query: String)

}
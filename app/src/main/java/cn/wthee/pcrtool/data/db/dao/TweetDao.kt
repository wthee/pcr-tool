package cn.wthee.pcrtool.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.db.entity.TweetData

/**
 * 推特信息 DAO
 */
@Dao
interface TweetDao {

    /**
     * 更新
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<TweetData>)

    /**
     * 获取
     */
    @Query("SELECT * FROM tweet WHERE tweet LIKE '%' || :query || '%' ORDER BY id DESC")
    fun pagingSource(query: String): PagingSource<Int, TweetData>

    /**
     * 清空数据
     */
    @Query("DELETE FROM tweet WHERE tweet LIKE '%' || :query || '%'")
    suspend fun deleteByQuery(query: String)

}
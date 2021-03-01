package cn.wthee.pcrtool.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.entity.NewsTable

/**
 * 公告信息 DAO
 */
@Dao
interface NewsDao {

    /**
     * 更新公告
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsTable>)

    /**
     * 获取公告 [NewsTable]
     */
    @Query("SELECT * FROM news WHERE id LIKE :query")
    fun pagingSource(query: String): PagingSource<Int, NewsTable>

    /**
     * 根据 [region] 清空数据
     */
    @Query("DELETE FROM news WHERE id LIKE :region")
    suspend fun clearAll(region: String)
}
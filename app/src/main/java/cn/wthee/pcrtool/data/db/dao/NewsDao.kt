package cn.wthee.pcrtool.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.db.entity.NewsTable

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
     * 获取公告
     * @param query 查询筛选条件（区服区分）
     */
    @Query("SELECT * FROM news WHERE title LIKE '%' || :query || '%'")
    fun pagingSource(query: String): PagingSource<Int, NewsTable>

    /**
     * 清空数据
     * @param region 区服
     */
    @Query("DELETE FROM news WHERE region = :region AND title LIKE '%' || :query || '%'")
    suspend fun deleteByRegionAndQuery(region: Int, query: String)

}
package cn.wthee.pcrtool.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.entity.NewsTable

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsTable>)

    @Query("SELECT * FROM news WHERE id LIKE :query")
    fun pagingSource(query: String): PagingSource<Int, NewsTable>

    @Query("DELETE FROM news WHERE id LIKE :region")
    suspend fun clearAll(region: String)
}
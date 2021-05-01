package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.db.entity.RemoteKey

/**
 * 公告分页 DAO
 */
@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM remote_key WHERE repoId = :id")
    suspend fun remoteKeys(id: String): RemoteKey?

    @Query("DELETE FROM remote_key WHERE repoId like :region")
    suspend fun clearRemoteKeys(region: String)

}
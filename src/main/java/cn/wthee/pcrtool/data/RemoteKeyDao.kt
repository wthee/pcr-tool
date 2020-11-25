package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.entity.RemoteKey

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM remote_key WHERE repoId = :id")
    suspend fun remoteKeys(id: String): RemoteKey?

    @Query("DELETE FROM remote_key")
    suspend fun clearRemoteKeys()

}
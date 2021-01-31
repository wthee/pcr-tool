package cn.wthee.pcrtool.utils


import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cn.wthee.pcrtool.MyApplication
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore 工具
 */
object DataStoreUtil {

    /**
     * 保存
     */
    suspend inline fun save(key: String, obj: String) {
        val NAME = stringPreferencesKey(key)
        MyApplication.dataStore.edit { main ->
            main[NAME] = obj
        }
    }

    /**
     * 读取
     */
    fun get(key: String): Flow<String?> {
        return MyApplication.dataStore.data.map {
            it[stringPreferencesKey(key)]
        }
    }

    /**
     * Gson 转化
     */
    inline fun <reified T : Any> fromJson(json: String?): T? {
        return Gson().fromJson(json, T::class.java)
    }
}

interface DataStoreRead {
    fun read(str: String?)
}


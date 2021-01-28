package cn.wthee.pcrtool.utils

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import cn.wthee.pcrtool.MyApplication
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

/**
 * DataStore 工具
 */
object DataStoreUtil {

    /**
     * 保存
     */
    suspend inline fun <reified T : Any> save(key: String, obj: T) {
        val NAME = preferencesKey<T>(key)
        MyApplication.dataStore.edit { main ->
            main[NAME] = obj
        }
    }

    /**
     * 读取
     */
    suspend inline fun <reified T : Any> get(key: String, listener: DataStoreRead<T>) {
        MyApplication.dataStore.data.map {
            it[preferencesKey<T>(key)]
        }.collect {
            listener.read(it)
        }
    }

    /**
     * Gson 转化
     */
    inline fun <reified T : Any> fromJson(json: String?): T? {
        return Gson().fromJson(json, T::class.java)
    }
}

interface DataStoreRead<T> {
    fun read(s: T?)
}
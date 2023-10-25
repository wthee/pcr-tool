package cn.wthee.pcrtool.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import cn.wthee.pcrtool.ui.dataStoreMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 编辑排序
 */
fun editOrder(
    context: Context,
    scope: CoroutineScope,
    id: Int,
    key: Preferences.Key<String>,
    onSuccess:((String)->Unit)? = null
) {
    //更新
    scope.launch {
        context.dataStoreMain.edit { preferences ->
            val orderStr = preferences[key] ?: ""
            val idStr = "$id-"
            val hasAdded = orderStr.intArrayList.contains(id)

            //新增或移除
            val edited = if (!hasAdded) {
                orderStr + idStr
            } else {
                orderStr.replace(idStr, "")
            }
            preferences[key] = edited
            onSuccess?.let {
                onSuccess(edited)
            }
        }
    }
}
package cn.wthee.pcrtool.data.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.utils.GsonUtil
import com.google.gson.Gson
import kotlinx.coroutines.flow.map

data class FilterEquipment(
    var all: Boolean = true,
    var craft: Int = 1,
    var colorType: Int = 0,
    var name: String = "",
    var starIds: ArrayList<Int> = arrayListOf()
)

/**
 * 获取装备收藏列表
 */
@Composable
fun getStarEquipIdList() : ArrayList<Int>{
    val context = LocalContext.current
    val data = remember {
        context.dataStoreMain.data.map {
            it[MainPreferencesKeys.SP_STAR_EQUIP]
        }
    }.collectAsState(initial = null).value
    return GsonUtil.toIntList(data)
}

/**
 * 更新收藏的装备id
 */
suspend fun updateStarEquipId(context: Context, id: Int) {
    context.dataStoreMain.edit { preferences ->
        val list = GsonUtil.toIntList(preferences[MainPreferencesKeys.SP_STAR_EQUIP])
        if (list.contains(id)) {
            list.remove(id)
        } else {
            list.add(id)
        }
        preferences[MainPreferencesKeys.SP_STAR_EQUIP] = Gson().toJson(list)
    }
}

fun FilterEquipment.isFilter(): Boolean {
    return !(all && colorType == 0 && name == "" && craft == 1)
}
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

data class FilterExtraEquipment(
    var all: Boolean = true,
    var flag: Int = 0,
    var rarity: Int = 0,
    var category: Int = 0,
    var name: String = "",
    var starIds: ArrayList<Int> = arrayListOf()
)

/**
 * 获取ex装备收藏列表
 */
@Composable
fun getStarExEquipIdList() : ArrayList<Int>{
    val context = LocalContext.current
    val data = remember {
        context.dataStoreMain.data.map {
            it[MainPreferencesKeys.SP_STAR_EXTRA_EQUIP]
        }
    }.collectAsState(initial = null).value
    return GsonUtil.toIntList(data)
}

/**
 * 更新收藏的ex装备id
 */
suspend fun updateStarExEquipId(context: Context, id: Int) {
    context.dataStoreMain.edit { preferences ->
        val list = GsonUtil.toIntList(preferences[MainPreferencesKeys.SP_STAR_EXTRA_EQUIP])
        if (list.contains(id)) {
            list.remove(id)
        } else {
            list.add(id)
        }
        preferences[MainPreferencesKeys.SP_STAR_EXTRA_EQUIP] = Gson().toJson(list)
    }
}

fun FilterExtraEquipment.isFilter(): Boolean {
    return !(all && rarity == 0 && name == "" && flag == 0 && category == 0)
}
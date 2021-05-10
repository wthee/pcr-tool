package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.viewmodel.PvpViewModel

@Composable
fun PvpFavorites(pvpViewModel: PvpViewModel = hiltNavGraphViewModel()) {
    val region = getRegion()
    pvpViewModel.getAllFavorites(region)
    val list = pvpViewModel.allFavorites.observeAsState()

    list.value?.let {
        LazyColumn {
            items(it) { data ->
                PvpFavoriteItem(data)
            }
        }
    }
}

@Composable
private fun PvpFavoriteItem(itemData: PvpFavoriteData) {

}
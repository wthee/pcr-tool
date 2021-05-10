package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import kotlinx.coroutines.launch

/**
 * 已收藏数据
 */
@Composable
fun PvpFavorites(pvpViewModel: PvpViewModel = hiltNavGraphViewModel()) {
    val region = getRegion()
    pvpViewModel.getAllFavorites(region)
    val list = pvpViewModel.allFavorites.observeAsState()
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (list.value != null) {
            LazyColumn(state = state) {
                items(list.value!!) { data ->
                    PvpFavoriteItem(region, data, pvpViewModel)
                }
            }
            //已收藏
            if (list.value!!.isNotEmpty()) {
                ExtendedFabCompose(
                    iconType = MainIconType.LOVE_FILL,
                    text = stringResource(
                        id = R.string.favorite_count,
                        list.value!!.size
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                ) {
                    scope.launch {
                        state.scrollToItem(0)
                    }
                }
            }
        } else {
            MainText(
                text = stringResource(id = R.string.tip_pvp_favorites),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

}

@Composable
private fun PvpFavoriteItem(region: Int, itemData: PvpFavoriteData, pvpViewModel: PvpViewModel) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.mediuPadding)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                //队伍角色图标
                Column(
                    modifier = Modifier
                        .padding(Dimen.mediuPadding)
                        .weight(0.8f)
                ) {
                    //进攻
                    //fixme 点击跳转角色详情
                    Row {
                        itemData.getAtkIds().forEach {
                            IconCompose(
                                data = CharacterIdUtil.getMaxIconUrl(
                                    it,
                                    MainActivity.r6Ids.contains(it)
                                ),
                                modifier = Modifier.padding(end = Dimen.largePadding)
                            )
                        }
                    }
                    //防守
                    Row {
                        itemData.getDefIds().forEach {
                            IconCompose(
                                data = CharacterIdUtil.getMaxIconUrl(
                                    it,
                                    MainActivity.r6Ids.contains(it)
                                ),
                                modifier = Modifier.padding(end = Dimen.largePadding)
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(0.2f)) {
                    IconCompose(
                        data = MainIconType.LOVE_FILL.icon,
                        modifier = Modifier
                            .padding(bottom = Dimen.mediuPadding)
                            .size(Dimen.fabIconSize)
                    ) {
                        //点击取消收藏，增加确认 dialog 操作
                        scope.launch {
                            pvpViewModel.delete(itemData.atks, itemData.defs, region)
                        }
                    }
                    //fixme 搜索
                    IconCompose(
                        data = MainIconType.PVP_SEARCH.icon,
                        modifier = Modifier
                            .padding(top = Dimen.mediuPadding)
                            .size(Dimen.fabIconSize)
                    ) {

                    }
                }
            }
        }
    }
}
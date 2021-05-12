package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import kotlinx.coroutines.launch

/**
 * 已收藏数据
 */
@Composable
fun PvpFavorites(
    toCharacter: (Int) -> Unit,
    toResult: (String) -> Unit,
    pvpViewModel: PvpViewModel = hiltNavGraphViewModel()
) {
    val region = getRegion()
    pvpViewModel.getAllFavorites(region)
    val list = pvpViewModel.allFavorites.observeAsState()
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (list.value != null) {
            LazyColumn(state = state) {
                items(list.value!!) { data ->
                    PvpFavoriteItem(toCharacter, toResult, region, data, pvpViewModel)
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
            } else {
                MainText(
                    text = stringResource(id = R.string.tip_pvp_favorites),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun PvpFavoriteItem(
    toCharacter: (Int) -> Unit,
    toResult: (String) -> Unit,
    region: Int,
    itemData: PvpFavoriteData,
    pvpViewModel: PvpViewModel
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.mediuPadding)
    ) {
        MainCard {
            //队伍角色图标
            Column(
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimen.mediuPadding)
                ) {
                    //搜索
                    TextButton(onClick = {
                        toResult(itemData.defs)
                    }) {
                        IconCompose(
                            data = MainIconType.PVP_SEARCH.icon,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(Dimen.fabIconSize)
                        )
                        MainContentText(text = stringResource(id = R.string.pvp_research))
                    }
                    //取消收藏
                    IconCompose(
                        data = MainIconType.LOVE_FILL.icon,
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.CenterEnd)
                            .size(Dimen.fabIconSize)
                    ) {
                        //点击取消收藏，增加确认 dialog 操作
                        scope.launch {
                            pvpViewModel.delete(itemData.atks, itemData.defs, region)
                        }
                    }
                }
                //进攻
                LazyRow {
                    items(itemData.getAtkIds()) {
                        IconCompose(
                            data = CharacterIdUtil.getMaxIconUrl(
                                it,
                                MainActivity.r6Ids.contains(it)
                            ),
                            modifier = Modifier.padding(end = Dimen.largePadding)
                        ) {
                            toCharacter(it)
                        }
                    }
                }
                //防守
                LazyRow {
                    items(itemData.getDefIds()) {
                        IconCompose(
                            data = CharacterIdUtil.getMaxIconUrl(
                                it,
                                MainActivity.r6Ids.contains(it)
                            ),
                            modifier = Modifier.padding(
                                top = Dimen.mediuPadding,
                                end = Dimen.largePadding
                            )
                        ) {
                            toCharacter(it)
                        }
                    }
                }
            }
        }
    }
}
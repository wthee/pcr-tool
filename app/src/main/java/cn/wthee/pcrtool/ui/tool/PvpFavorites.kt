package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import kotlinx.coroutines.launch

/**
 * 已收藏数据
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun PvpFavorites(
    scrollState: LazyListState,
    toCharacter: (Int) -> Unit,
    toResearch: () -> Boolean,
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    val region = getRegion()
    pvpViewModel.getAllFavorites(region)
    val list = pvpViewModel.allFavorites.observeAsState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (list.value != null) {
            LazyColumn(state = scrollState, contentPadding = PaddingValues(Dimen.mediuPadding)) {
                items(list.value!!) { data ->
                    PvpFavoriteItem(toCharacter, toResearch, region, data, pvpViewModel)
                }
                item {
                    CommonSpacer()
                }
            }
            //已收藏
            if (list.value!!.isNotEmpty()) {
                FabCompose(
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
                        scrollState.scrollToItem(0)
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

@ExperimentalMaterialApi
@Composable
private fun PvpFavoriteItem(
    toCharacter: (Int) -> Unit,
    toResearch: () -> Boolean,
    region: Int,
    itemData: PvpFavoriteData,
    pvpViewModel: PvpViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(end = Dimen.mediuPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //搜索
        TextButton(onClick = {
            //重置页面
            MainActivity.navViewModel.selectedIds.postValue(itemData.defs)
            pvpViewModel.pvpResult.postValue(null)
            VibrateUtil(context).single()
            toResearch()
        }) {
            IconCompose(
                data = MainIconType.PVP_SEARCH.icon,
                size = Dimen.fabIconSize
            )
            MainContentText(text = stringResource(id = R.string.pvp_research))
        }
        Spacer(modifier = Modifier.weight(1f))
        //取消收藏
        IconCompose(
            data = MainIconType.LOVE_FILL.icon,
            Dimen.fabIconSize
        ) {
            //点击取消收藏，增加确认 dialog 操作
            scope.launch {
                pvpViewModel.delete(itemData.atks, itemData.defs, region)
            }
        }
    }

    MainCard(modifier = Modifier.padding((Dimen.mediuPadding))) {
        //队伍角色图标
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.mediuPadding)
        ) {
            //进攻
            Row(
                modifier = Modifier
                    .padding(bottom = Dimen.largePadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                itemData.getAtkIds().forEachIndexed { index, it ->
                    IconCompose(
                        data = CharacterIdUtil.getMaxIconUrl(
                            it,
                            MainActivity.r6Ids.contains(it)
                        )
                    ) {
                        toCharacter(it)
                    }
                }
            }
            //防守
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                itemData.getDefIds().forEachIndexed { index, it ->
                    IconCompose(
                        data = CharacterIdUtil.getMaxIconUrl(
                            it,
                            MainActivity.r6Ids.contains(it)
                        )
                    ) {
                        toCharacter(it)
                    }
                }
            }
        }
    }

}
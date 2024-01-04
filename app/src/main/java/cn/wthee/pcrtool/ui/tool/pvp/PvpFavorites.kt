package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.utils.formatTime
import kotlinx.coroutines.launch


/**
 * 已收藏数据
 *
 */
@Composable
fun PvpFavorites(
    favoritesListState: LazyGridState,
    toCharacter: (Int) -> Unit,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel
) {
    val uiState by pvpViewModel.uiState.collectAsStateWithLifecycle()
    val itemWidth = getItemWidth(floatWindow)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (uiState.allFavoritesList.isNotEmpty()) {
            LazyVerticalGrid(
                state = favoritesListState,
                columns = GridCells.Adaptive(itemWidth)
            ) {
                items(
                    items = uiState.allFavoritesList,
                    key = {
                        it.id
                    }
                ) { data ->
                    PvpFavoriteItem(
                        data,
                        floatWindow,
                        toCharacter,
                        pvpViewModel
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            CenterTipText(
                text = stringResource(id = R.string.pvp_no_favorites)
            )
        }
    }
}

@Composable
private fun PvpFavoriteItem(
    itemData: PvpFavoriteData,
    floatWindow: Boolean,
    toCharacter: (Int) -> Unit,
    pvpViewModel: PvpViewModel?
) {
    val scope = rememberCoroutineScope()
    val largePadding = if (floatWindow) Dimen.mediumPadding else Dimen.largePadding
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding

    Column(
        modifier = Modifier.padding(
            horizontal = largePadding,
            vertical = mediumPadding
        )
    ) {
        Row(
            modifier = Modifier.padding(bottom = mediumPadding),
            verticalAlignment = Alignment.Bottom
        ) {
            //日期
            MainTitleText(
                text = itemData.date.formatTime.substring(0, 10)
            )
            Spacer(modifier = Modifier.weight(1f))
            //取消收藏
            MainIcon(
                data = MainIconType.FAVORITE_FILL,
                size = Dimen.fabIconSize
            ) {
                //点击取消收藏
                scope.launch {
                    pvpViewModel?.delete(itemData.atks, itemData.defs)
                }
            }
            Spacer(modifier = Modifier.width(largePadding))
            //搜索
            MainIcon(
                data = MainIconType.PVP_SEARCH,
                size = Dimen.fabIconSize
            ) {
                //重置页面
                scope.launch {
                    pvpViewModel?.resetResult()
                    val selectedData =
                        pvpViewModel?.getPvpCharacterByIds(itemData.getDefIds())
                    val selectedIds = selectedData as ArrayList<PvpCharacterData>?
                    selectedIds?.sortWith(comparePvpCharacterData())
                    navViewModel.selectedPvpData.postValue(selectedIds)
                    navViewModel.showResult.postValue(true)
                }
            }
        }
        MainCard {
            //队伍角色图标
            Column(
                modifier = Modifier.padding(top = mediumPadding, bottom = mediumPadding)
            ) {
                //进攻
                MainTitleText(
                    text = stringResource(id = R.string.team_win),
                    backgroundColor = colorGold,
                    modifier = Modifier.padding(start = mediumPadding)
                )
                PvpUnitIconLine(
                    itemData.getAtkIds(),
                    floatWindow,
                    toCharacter
                )
                //防守
                MainTitleText(
                    text = stringResource(id = R.string.team_lose),
                    backgroundColor = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = mediumPadding, top = mediumPadding)
                )
                PvpUnitIconLine(
                    itemData.getDefIds(),
                    floatWindow,
                    toCharacter
                )
            }
        }
    }

}


@CombinedPreviews
@Composable
private fun PvpFavoriteItemPreview() {
    val data = PvpFavoriteData(
        "id",
        "1-2-3-4-5",
        "1-2-3-4-5",
        "2020/01/01 00:00:00",
        2
    )
    PreviewLayout {
        PvpFavoriteItem(
            data,
            false,
            { },
            null,
        )
        PvpFavoriteItem(
            data,
            true,
            { },
            null,
        )
    }
}
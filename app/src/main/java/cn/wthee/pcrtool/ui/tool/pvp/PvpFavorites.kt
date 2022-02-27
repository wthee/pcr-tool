package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyGridState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import kotlinx.coroutines.launch


/**
 * 已收藏数据
 *
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun PvpFavorites(
    favoritesListState: LazyGridState,
    toCharacter: (Int) -> Unit,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel
) {
    val region = getRegion()
    pvpViewModel.getAllFavorites(region)
    val list = pvpViewModel.allFavorites.observeAsState()
    val itemWidth = getItemWidth(floatWindow)

    Box(modifier = Modifier.fillMaxSize()) {
        if (list.value != null && list.value!!.isNotEmpty()) {
            LazyVerticalGrid(
                state = favoritesListState,
                cells = GridCells.Adaptive(itemWidth)
            ) {
                items(list.value!!) { data ->
                    PvpFavoriteItem(
                        toCharacter,
                        region,
                        data,
                        floatWindow,
                        pvpViewModel
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            MainText(
                text = stringResource(id = R.string.pvp_no_favorites),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun PvpFavoriteItem(
    toCharacter: (Int) -> Unit,
    region: Int,
    itemData: PvpFavoriteData,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel,
    characterViewModel: CharacterViewModel = hiltViewModel(),
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
            IconCompose(
                data = MainIconType.LOVE_FILL,
                size = Dimen.fabIconSize
            ) {
                //点击取消收藏
                scope.launch {
                    pvpViewModel.delete(itemData.atks, itemData.defs, region)
                }
            }
            Spacer(modifier = Modifier.width(largePadding))
            //搜索
            IconCompose(
                data = MainIconType.PVP_SEARCH,
                size = Dimen.fabIconSize
            ) {
                //重置页面
                scope.launch {
                    pvpViewModel.pvpResult.postValue(null)
                    val selectedData =
                        characterViewModel.getPvpCharacterByIds(itemData.getDefIds())
                    val selectedIds = selectedData as ArrayList<PvpCharacterData>?
                    selectedIds?.sortByDescending { it.position }
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
                    backgroundColor = colorResource(id = R.color.color_rank_7_10),
                    modifier = Modifier.padding(start = mediumPadding)
                )
                PvpUnitIconLine(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    itemData.getAtkIds(),
                    floatWindow,
                    toCharacter
                )
                //防守
                MainTitleText(
                    text = stringResource(id = R.string.team_lose),
                    backgroundColor = colorResource(id = R.color.gray),
                    modifier = Modifier.padding(start = mediumPadding, top = mediumPadding)
                )
                PvpUnitIconLine(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    itemData.getDefIds(),
                    floatWindow,
                    toCharacter
                )
            }
        }
    }

}

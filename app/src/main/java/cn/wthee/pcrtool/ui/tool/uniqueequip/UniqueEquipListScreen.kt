package cn.wthee.pcrtool.ui.tool.uniqueequip

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
import cn.wthee.pcrtool.data.db.view.getIndex
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.BottomSearchBar
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.deleteSpace
import kotlinx.coroutines.launch


/**
 * 专用装备列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UniqueEquipListScreen(
    uniqueEquipListViewModel: UniqueEquipListViewModel = hiltViewModel(),
    toUniqueEquipDetail: (Int) -> Unit
) {
    val uiState by uniqueEquipListViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val uniqueEquips = uiState.uniqueEquipList

    //专用装备1
    val uniqueEquips1 = uniqueEquips?.filter {
        it.equipSlot == 1
    }
    //专用装备2
    val uniqueEquips2 = uniqueEquips?.filter {
        it.equipSlot == 2
    }

    //列表状态
    val gridState1 = rememberLazyGridState()
    val gridState2 = rememberLazyGridState()

    //计算页数
    var pagerCount = 0
    if (uniqueEquips1?.isNotEmpty() == true) {
        pagerCount = 1
    }
    if (uniqueEquips2?.isNotEmpty() == true) {
        pagerCount = 2
    }

    //页面状态
    val pagerState = rememberPagerState {
        pagerCount
    }


    MainScaffold(
        fabWithCustomPadding = {
            //搜索栏
            val count = uniqueEquips?.size ?: 0

            BottomSearchBar(
                labelStringId = R.string.search_unique_equip,
                keyword = uiState.keyword,
                openSearch = uiState.openSearch,
                leadingIcon = MainIconType.UNIQUE_EQUIP,
                defaultKeywordList = uiState.keywordList,
                showReset = uiState.keyword != "",
                changeSearchBar = uniqueEquipListViewModel::changeSearchBar,
                changeKeyword = uniqueEquipListViewModel::changeKeyword,
                fabText = count.toString(),
                onTopClick = {
                    scope.launch {
                        if (uniqueEquips1?.isNotEmpty() == true) {
                            gridState1.scrollToItem(0)
                        }
                        if (uniqueEquips2?.isNotEmpty() == true) {
                            gridState2.scrollToItem(0)
                        }
                    }
                },
                onResetClick = {
                    uniqueEquipListViewModel.reset()
                }
            )
        },
        enableClickClose = uiState.openSearch,
        onCloseClick = {
            uniqueEquipListViewModel.changeSearchBar(false)
        }
    ) {
        StateBox(stateType = uiState.loadState) {
            Column {
                if (pagerCount == 2) {
                    MainTabRow(
                        pagerState = pagerState,
                        tabs = arrayListOf(
                            TabData(tab = getIndex(1), count = uniqueEquips1!!.size),
                            TabData(tab = getIndex(2), count = uniqueEquips2!!.size)
                        ),
                        modifier = Modifier
                            .fillMaxWidth(RATIO_GOLDEN)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        if (it == 0) {
                            gridState1.scrollToItem(0)
                        } else {
                            gridState2.scrollToItem(0)
                        }
                    }
                }

                HorizontalPager(state = pagerState) { index ->
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(getItemWidth()),
                        modifier = Modifier.fillMaxHeight(),
                        state = if (index == 0) gridState1 else gridState2
                    ) {
                        items(
                            if (index == 0) uniqueEquips1!! else uniqueEquips2!!,
                            key = {
                                it.equipId
                            }
                        ) { uniqueEquip ->
                            //获取角色名
                            val flow = remember(uniqueEquip.unitId) {
                                uniqueEquipListViewModel.getCharacterBasicInfo(uniqueEquip.unitId)
                            }
                            val basicInfo by flow.collectAsState(initial = CharacterInfo())

                            basicInfo?.let {
                                UniqueEquipItem(
                                    uniqueEquip,
                                    it,
                                    toUniqueEquipDetail
                                )
                            }
                        }
                        item {
                            CommonSpacer()
                        }
                    }
                }

            }
        }
    }

}


/**
 * 装备
 */
@Composable
private fun UniqueEquipItem(
    equip: UniqueEquipBasicData,
    basicInfo: CharacterInfo,
    toUniqueEquipDetail: (Int) -> Unit
) {

    Row(
        modifier = Modifier.padding(
            top = Dimen.largePadding,
            start = Dimen.largePadding,
            end = Dimen.largePadding,
        )
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ImageRequestHelper.ICON_EQUIPMENT, equip.equipId),
            onClick = {
                toUniqueEquipDetail(equip.unitId)
            }
        )

        Column {

            MainTitleText(
                text = equip.equipName,
                modifier = Modifier.padding(start = Dimen.smallPadding),
                selectable = true
            )


            MainCard(
                modifier = Modifier.padding(
                    start = Dimen.mediumPadding,
                    top = Dimen.mediumPadding,
                    bottom = Dimen.mediumPadding
                ),
                onClick = {
                    toUniqueEquipDetail(equip.unitId)
                }
            ) {
                Subtitle2(
                    text = getIndex(equip.equipId % 10) + equip.description.deleteSpace,
                    modifier = Modifier.padding(Dimen.mediumPadding),
                    selectable = true
                )

                UnitIconAndTag(basicInfo = basicInfo, showUniqueEquipType = false)

            }
        }

    }

}

/**
 * 角色图标和标签
 */
@Composable
fun UnitIconAndTag(
    basicInfo: CharacterInfo?,
    showUniqueEquipType: Boolean
) {
    basicInfo?.let {
        Row(
            modifier = Modifier.padding(Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainIcon(
                data = ImageRequestHelper.getInstance().getMaxIconUrl(basicInfo.id)
            )

            Column(modifier = Modifier.padding(start = Dimen.smallPadding)) {
                //名称
                Subtitle2(
                    text = basicInfo.name,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    modifier = Modifier.padding(Dimen.smallPadding),
                    selectable = true
                )

                CharacterTagRow(
                    modifier = Modifier.padding(top = Dimen.smallPadding),
                    basicInfo = basicInfo,
                    showUniqueEquipType = showUniqueEquipType
                )
            }
        }
    }
}


@CombinedPreviews
@Composable
private fun UniqueEquipItemPreview() {
    PreviewLayout {
        UniqueEquipItem(
            UniqueEquipBasicData(
                equipName = stringResource(id = R.string.debug_short_text),
                description = stringResource(id = R.string.debug_long_text),
            ),
            CharacterInfo(
                name = stringResource(id = R.string.debug_short_text)
            )
        ) {}
    }
}
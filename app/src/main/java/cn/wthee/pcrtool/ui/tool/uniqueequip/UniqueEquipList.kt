package cn.wthee.pcrtool.ui.tool.uniqueequip

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
import cn.wthee.pcrtool.data.db.view.getIndex
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.BottomSearchBar
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel


/**
 * 专用装备列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UniqueEquipList(
    equipmentViewModel: EquipmentViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel(),
    toUniqueEquipDetail: (Int) -> Unit
) {
    val defaultName = navViewModel.uniqueEquipName.value ?: ""
    //关键词输入
    val keywordInputState = remember {
        mutableStateOf(defaultName)
    }
    //关键词查询
    val keywordState = remember {
        mutableStateOf(defaultName)
    }

    LaunchedEffect(keywordState.value) {
        navViewModel.uniqueEquipName.value = keywordState.value
    }
    //专用装备1
    val uniqueEquips1Flow = remember(keywordState.value) {
        equipmentViewModel.getUniqueEquips(keywordState.value, 1)
    }
    val uniqueEquips1 by uniqueEquips1Flow.collectAsState(initial = arrayListOf())

    //专用装备2
    val uniqueEquips2Flow = remember(keywordState.value) {
        equipmentViewModel.getUniqueEquips(keywordState.value, 2)
    }
    val uniqueEquips2 by uniqueEquips2Flow.collectAsState(initial = arrayListOf())

    //总列表
    val uniqueEquips = uniqueEquips1 + uniqueEquips2

    val gridState1 = rememberLazyGridState()
    val gridState2 = rememberLazyGridState()
    var pagerCount = 0
    if (uniqueEquips1.isNotEmpty()) {
        pagerCount++
    }
    if (uniqueEquips2.isNotEmpty()) {
        pagerCount++
    }
    val pagerState = rememberPagerState {
        pagerCount
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (uniqueEquips.isNotEmpty()) {
            Column {
                if (pagerCount == 2) {
                    MainTabRow(
                        pagerState = pagerState,
                        tabs = arrayListOf(
                            getIndex(1) + uniqueEquips1.size,
                            getIndex(2) + uniqueEquips2.size
                        ),
                        gridStateList = arrayListOf(gridState1, gridState2),
                        modifier = Modifier
                            .fillMaxWidth(RATIO_GOLDEN)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                HorizontalPager(state = pagerState) { index ->
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(getItemWidth()),
                        modifier = Modifier.fillMaxHeight(),
                        state = if (index == 0) gridState1 else gridState2
                    ) {
                        items(
                            if (index == 0) uniqueEquips1 else uniqueEquips2,
                            key = {
                                it.equipId
                            }
                        ) { uniqueEquip ->
                            //获取角色名
                            val flow = remember(uniqueEquip.unitId) {
                                characterViewModel.getCharacterBasicInfo(uniqueEquip.unitId)
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
        } else {
            CenterTipText(
                stringResource(id = R.string.no_data)
            )
        }


        //搜索栏
        val count = uniqueEquips.size

        BottomSearchBar(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            labelStringId = R.string.search_unique_equip,
            keywordInputState = keywordInputState,
            keywordState = keywordState,
            leadingIcon = MainIconType.UNIQUE_EQUIP,
            fabText = count.toString()
        )
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
            data = ImageRequestHelper.getInstance().getEquipPic(equip.equipId)
        ) {
            toUniqueEquipDetail(equip.unitId)
        }

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

                UnitIconAndTag(basicInfo)

            }
        }

    }

}

/**
 * 角色图标和标签
 */
@Composable
fun UnitIconAndTag(
    basicInfo: CharacterInfo?
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
                    basicInfo = basicInfo
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
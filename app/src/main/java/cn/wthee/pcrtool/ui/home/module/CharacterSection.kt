package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.RATIO
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.viewmodel.OverviewViewModel


/**
 * 角色预览
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterSection(
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.CHARACTER.id
    //角色总数
    val characterCount =
        overviewViewModel.getCharacterCount().collectAsState(initial = "0").value
    //角色列表
    val characterList =
        overviewViewModel.getCharacterInfoList().collectAsState(initial = arrayListOf()).value

    Section(
        id = id,
        titleId = R.string.character,
        iconType = MainIconType.CHARACTER,
        hintText = characterCount,
        contentVisible = characterList.isNotEmpty(),
        isEditMode = isEditMode,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
                actions.toCharacterList()
        }
    ) {
        if (characterList.isNotEmpty()) {
            //避免角色图片高度过高
            if (ScreenUtil.getWidth() / RATIO < (Dimen.iconSize * 5).value.dp2px) {
                HorizontalPager(
                    state = rememberPagerState { characterList.size },
                    modifier = Modifier
                        .padding(vertical = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = Dimen.largePadding),
                    pageSpacing = Dimen.mediumPadding
                ) { index ->
                    val unitId = if (characterList.isEmpty()) 0 else characterList[index].id
                    CharacterImageItem(
                        modifier = Modifier.fillMaxWidth(),
                        unitId = unitId,
                        actions.toCharacterDetail
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .padding(vertical = Dimen.mediumPadding)
                        .fillMaxWidth()
                ) {
                    items(characterList) {
                        Box(modifier = Modifier.padding(start = Dimen.largePadding)) {
                            CharacterImageItem(
                                modifier = Modifier
                                    .widthIn(max = getItemWidth() * 1.3f)
                                    .fillMaxWidth(),
                                unitId = it.id,
                                actions.toCharacterDetail
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.padding(end = Dimen.largePadding))
                    }
                }
            }

        }
    }
}

/**
 * 角色图片
 */
@Composable
private fun CharacterImageItem(
    modifier: Modifier = Modifier,
    unitId: Int,
    toCharacterDetail: (Int) -> Unit
) {
    MainCard(
        modifier = modifier,
        onClick = {
            toCharacterDetail(unitId)
        }
    ) {
        MainImage(
            data = ImageRequestHelper.getInstance().getMaxCardUrl(unitId),
            ratio = RATIO
        )
    }
}
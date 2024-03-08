package cn.wthee.pcrtool.ui.home.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.RATIO
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ScreenUtil


/**
 * 角色预览
 */
@Composable
fun CharacterSection(
    isEditMode: Boolean,
    orderStr: String,
    updateOrderData: (Int) -> Unit,
    toCharacterList: () -> Unit,
    toCharacterDetail: (Int) -> Unit,
    characterSectionViewModel: CharacterSectionViewModel = hiltViewModel()
) {
    val uiState by characterSectionViewModel.uiState.collectAsStateWithLifecycle()

    CharacterSectionContent(
        uiState = uiState,
        isEditMode = isEditMode,
        orderStr = orderStr,
        updateOrderData = updateOrderData,
        toCharacterList = toCharacterList,
        toCharacterDetail = toCharacterDetail
    )
}

@Composable
private fun CharacterSectionContent(
    uiState: CharacterSectionUiState,
    isEditMode: Boolean,
    orderStr: String,
    updateOrderData: (Int) -> Unit,
    toCharacterList: () -> Unit,
    toCharacterDetail: (Int) -> Unit
) {
    val id = OverviewType.CHARACTER.id

    Section(
        id = id,
        titleId = R.string.character,
        iconType = MainIconType.CHARACTER,
        hintText = uiState.characterCount,
        contentVisible = uiState.characterCount != "0",
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode) {
                updateOrderData(id)
            } else {
                toCharacterList()
            }
        }
    ) {
        if (uiState.characterList?.isNotEmpty() == true) {
            val characterList = uiState.characterList
            //避免角色图片高度过高
            val itemWidthPx = LocalDensity.current.run { (Dimen.iconSize * 5).toPx() }
            if (ScreenUtil.getWidth() / RATIO < itemWidthPx) {
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
                        unitId = unitId,
                        toCharacterDetail = toCharacterDetail
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
                                modifier = Modifier.widthIn(max = Dimen.itemMaxWidth),
                                unitId = it.id,
                                toCharacterDetail = toCharacterDetail
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
    val placeholder = unitId == -1
    MainCard(
        modifier = modifier
            .placeholder(visible = placeholder, shape = MaterialTheme.shapes.medium),
        onClick = {
            if (!placeholder) {
                toCharacterDetail(unitId)
            }
        }
    ) {
        MainImage(
            data = ImageRequestHelper.getInstance().getMaxCardUrl(unitId),
            ratio = RATIO
        )
    }
}


@CombinedPreviews
@Composable
private fun CharacterSectionContentPreview() {
    PreviewLayout {
        CharacterSectionContent(
            uiState = CharacterSectionUiState(
                characterList = arrayListOf(
                    CharacterInfo(
                        id = 1
                    ),
                    CharacterInfo(
                        id = 2
                    )
                ),
                characterCount = "100"
            ),
            isEditMode = false,
            orderStr = "${OverviewType.CHARACTER.id}",
            updateOrderData = {},
            toCharacterList = {},
            toCharacterDetail = {}
        )
    }
}
package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.RATIO
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.utils.editOrder
import cn.wthee.pcrtool.viewmodel.OverviewViewModel


/**
 * 角色预览
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterSection(
    actions: NavActions,
    isEditMode: Boolean,
    orderStr: String,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val id = OverviewType.CHARACTER.id
    //角色总数
    val characterCountFlow = remember {
        overviewViewModel.getCharacterCount()
    }
    val characterCount by characterCountFlow.collectAsState(initial = "0")
    //角色列表
    val characterListFlow = remember {
        overviewViewModel.getCharacterInfoList()
    }
    val characterList by characterListFlow.collectAsState(
        initial = arrayListOf(
            CharacterInfo(),
            CharacterInfo(),
            CharacterInfo()
        )
    )


    Section(
        id = id,
        titleId = R.string.character,
        iconType = MainIconType.CHARACTER,
        hintText = characterCount,
        contentVisible = characterCount != "0",
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode) {
                editOrder(
                    context,
                    scope,
                    id,
                    MainPreferencesKeys.SP_OVERVIEW_ORDER
                )
            } else {
                actions.toCharacterList()
            }
        }
    ) {
        if (characterList?.isNotEmpty() == true) {
            //避免角色图片高度过高
            if (ScreenUtil.getWidth() / RATIO < (Dimen.iconSize * 5).value.dp2px) {
                HorizontalPager(
                    state = rememberPagerState { characterList?.size ?: 0 },
                    modifier = Modifier
                        .padding(vertical = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = Dimen.largePadding),
                    pageSpacing = Dimen.mediumPadding
                ) { index ->
                    val unitId = if (characterList!!.isEmpty()) 0 else characterList!![index].id
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
                    items(characterList!!) {
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
    val placeholder = unitId == -1
    MainCard(
        modifier = modifier
            .commonPlaceholder(placeholder),
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
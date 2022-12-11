package cn.wthee.pcrtool.ui.home.module
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.ImageCompose
import cn.wthee.pcrtool.ui.common.MainCard
import cn.wthee.pcrtool.ui.common.RATIO
import cn.wthee.pcrtool.ui.common.getItemWidth
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


/**
 * 角色预览
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CharacterSection(
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.CHARACTER.id
    //角色总数
    val characterCount =
        overviewViewModel.getCharacterCount().collectAsState(initial = 0).value
    //角色列表
    val characterList =
        overviewViewModel.getCharacterInfoList().collectAsState(initial = arrayListOf()).value

    Section(
        id = id,
        titleId = R.string.character,
        iconType = MainIconType.CHARACTER,
        hintText = characterCount.toString(),
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
            //正常
            if (ScreenUtil.getWidth() < getItemWidth().value.dp2px * 1.3) {
                //宽屏幕时
                HorizontalPager(
                    count = characterList.size,
                    state = rememberPagerState(),
                    modifier = Modifier
                        .padding(vertical = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = Dimen.largePadding),
                    itemSpacing = Dimen.mediumPadding
                ) { index ->
                    val unitId = if (characterList.isEmpty()) 0 else characterList[index].id
                    CharacterImageItem(unitId, actions.toCharacterDetail)
                }
            } else {
                //屏幕较宽时
                LazyRow {
                    items(characterList) {
                        Box(modifier = Modifier.padding(start = Dimen.largePadding)) {
                            CharacterImageItem(it.id, actions.toCharacterDetail)
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
    unitId: Int,
    toCharacterDetail: (Int) -> Unit
) {
    MainCard(
        modifier = Modifier.width(getItemWidth()),
        onClick = {
            toCharacterDetail(unitId)
        }
    ) {
        ImageCompose(
            data = ImageResourceHelper.getInstance().getMaxCardUrl(unitId),
            ratio = RATIO
        )
    }
}
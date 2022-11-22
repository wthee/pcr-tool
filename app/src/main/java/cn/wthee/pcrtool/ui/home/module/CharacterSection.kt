package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
                MainCard(
                    modifier = Modifier.width(getItemWidth()),
                    onClick = {
                        actions.toCharacterDetail(unitId)
                    }
//                    elevation = CardDefaults.cardElevation(0.dp),
//                    shape = MaterialTheme.shapes.medium,
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    ImageCompose(
                        data = ImageResourceHelper.getInstance().getMaxCardUrl(unitId),
                        ratio = RATIO
                    )
                }
            }
        }
    }
}
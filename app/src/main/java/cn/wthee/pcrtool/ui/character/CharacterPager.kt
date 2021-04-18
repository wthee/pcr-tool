package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.ui.compose.CharacterCard
import cn.wthee.pcrtool.utils.Constants
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CharacterPager(
    unitId: Int,
    r6Id: Int,
    toEquipDetail: (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = 3)
    val pages = arrayListOf("信息", "数值", "技能")
    var id = unitId
    id += if (r6Id != 0) 60 else 30
    Column() {
        //图片
        CharacterCard(Constants.CHARACTER_FULL_URL + id + Constants.WEBP)
        //标签
//        TabRow(
//            // Our selected tab is our current page
//            selectedTabIndex = pagerState.currentPage,
//            // Override the indicator, using the provided pagerTabIndicatorOffset modifier
//            indicator = { tabPositions ->
//                TabRowDefaults.Indicator(
//                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
//                )
//            }
//        ) {
//            // Add tabs for all of our pages
//            pages.forEachIndexed { index, title ->
//                Tab(
//                    text = { Text(title) },
//                    selected = pagerState.currentPage == index
//                )
//            }
//        }
        //页面
        HorizontalPager(state = pagerState) { page ->
            // Our page content
            when (page) {
                0 -> CharacterBasicInfo(unitId, r6Id)
                1 -> CharacterAttrInfo(unitId, r6Id, toEquipDetail = toEquipDetail)
                2 -> CharacterSkill(unitId)
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
    }

}
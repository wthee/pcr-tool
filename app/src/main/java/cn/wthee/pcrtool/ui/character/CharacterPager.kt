package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.ui.compose.CharacterCard
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.px2dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
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
    val top = (ScreenUtil.getWidth() / Constants.RATIO).px2dp - 10
    val pagerState = rememberPagerState(pageCount = 3)
    var id = unitId
    id += if (r6Id != 0) 60 else 30
    Box {
        //图片
        CharacterCard(Constants.CHARACTER_FULL_URL + id + Constants.WEBP)
        //页面
        HorizontalPager(state = pagerState, modifier = Modifier.padding(top = top.dp)) { page ->
            // Our page content
            when (page) {
                0 -> CharacterBasicInfo(unitId, r6Id)
                1 -> CharacterAttrInfo(unitId, r6Id, toEquipDetail = toEquipDetail)
                2 -> CharacterSkill(unitId)
            }
        }
    }
}
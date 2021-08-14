package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

/**
 * 角色卡池页面
 */
@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun GachaList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    gachaViewModel: GachaViewModel = hiltViewModel()
) {
    val gachas = gachaViewModel.getGachaHistory().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SlideAnimation(visible = gachas.isNotEmpty()) {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(Dimen.largePadding)
            ) {
                items(gachas) {
                    GachaItem(it, toCharacterDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.GACHA,
            text = stringResource(id = R.string.tool_gacha),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (e: Exception) {
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


}

/**
 * 单个卡池
 */
@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun GachaItem(gachaInfo: GachaInfo, toCharacterDetail: (Int) -> Unit) {
    val today = getToday(mainSP(LocalContext.current).getInt(Constants.SP_DATABASE_TYPE, 2))
    val sd = gachaInfo.startTime
    val ed = gachaInfo.endTime
    val inProgress = today.second(sd) > 0 && ed.second(today) > 0

    val icons = gachaInfo.unitIds.intArrayList
    val type = gachaInfo.getType()
    val color = when (type) {
        "PICK UP" -> colorResource(id = R.color.news_update)
        "复刻" -> colorResource(id = R.color.color_rank_7_10)
        "公主庆典" -> colorResource(id = R.color.color_rank_21)
        else -> MaterialTheme.colors.primary
    }

    //标题
    FlowRow(
        modifier = Modifier.padding(bottom = Dimen.mediumPadding),
    ) {
        MainTitleText(
            text = type,
            backgroundColor = color
        )
        MainTitleText(
            text = gachaInfo.startTime.formatTime.substring(0, 10),
            modifier = Modifier.padding(start = Dimen.smallPadding),
        )
        MainTitleText(
            text = gachaInfo.endTime.days(gachaInfo.startTime),
            modifier = Modifier.padding(start = Dimen.smallPadding)
        )

        //计时
        if (inProgress) {
            Row(
                modifier = Modifier.padding(start = Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCompose(
                    data = MainIconType.TIME_LEFT.icon,
                    size = Dimen.smallIconSize,
                )
                MainContentText(
                    text = stringResource(R.string.progressing, gachaInfo.endTime.dates(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }

    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding)) {
        Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
            //图标/描述
            if (icons.isEmpty()) {
                MainContentText(
                    text = gachaInfo.getDesc(),
                    modifier = Modifier.padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding
                    ),
                    textAlign = TextAlign.Start
                )
            } else {
                IconListCompose(icons = icons, toCharacterDetail = toCharacterDetail)
            }

            //结束日期
            CaptionText(
                text = gachaInfo.endTime.formatTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = Dimen.mediumPadding)

            )
        }
    }

}

@Preview
@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun GachaItemPreview() {
    PreviewBox {
        Column {
            GachaItem(gachaInfo = GachaInfo(), toCharacterDetail = {})
        }
    }
}
package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.openWebView
import cn.wthee.pcrtool.viewmodel.LeaderViewModel
import kotlinx.coroutines.launch

@Composable
fun LeaderboardList(leaderViewModel: LeaderViewModel = hiltNavGraphViewModel()) {
    leaderViewModel.getLeader()
    val list = leaderViewModel.leaderData.observeAsState()
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (list.value == null || list.value!!.data == null || list.value!!.data!!.leader.isEmpty()) {
            navViewModel.loading.postValue(true)
        } else if (list.value!!.message != "success") {
            navViewModel.loading.postValue(false)
            MainText(
                text = stringResource(id = R.string.data_get_error),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            navViewModel.loading.postValue(false)
            val info = list.value!!.data!!.leader
            Column {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(
                        top = Dimen.mediuPadding,
                        start = Dimen.mediuPadding,
                        end = Dimen.mediuPadding
                    )
                ) {
                    Spacer(modifier = Modifier.width(Dimen.iconSize + Dimen.smallPadding))
                    Text(
                        text = stringResource(id = R.string.grade),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = stringResource(id = R.string.jjc),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = stringResource(id = R.string.clan),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = stringResource(id = R.string.tower),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.25f)
                    )
                }
                LazyColumn(state = state) {
                    items(info) { it ->
                        LeaderboardItem(it)
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }
        //回到顶部
        ExtendedFabCompose(
            iconType = MainIconType.LEADER,
            text = stringResource(id = R.string.tool_leader),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                state.scrollToItem(0)
            }
        }
    }
}

/**
 * 角色评价信息
 */
@Composable
fun LeaderboardItem(info: LeaderboardData) {
    Column(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
    ) {
        //标题
        MainTitleText(
            text = info.name,
            modifier = Modifier.padding(bottom = Dimen.mediuPadding)
        )
        val context = LocalContext.current
        val title = stringResource(id = R.string.visit_detail)

        MainCard(onClick = {
            //打开浏览器
            openWebView(context, info.url, title)
        }) {
            Row(
                modifier = Modifier.padding(Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCompose(data = info.icon, modifier = Modifier.size(Dimen.iconSize))
                GradeText(info.all, modifier = Modifier.weight(0.25f))
                GradeText(info.pvp, modifier = Modifier.weight(0.25f))
                GradeText(info.clan, modifier = Modifier.weight(0.25f))
                GradeText(info.tower, modifier = Modifier.weight(0.25f))
            }
        }
    }
}

/**
 * 根据阶级返回颜色
 */
@Composable
fun GradeText(grade: String, textAlign: TextAlign = TextAlign.Center, modifier: Modifier) {
    Text(
        text = grade,
        color = colorResource(
            id = when (grade) {
                "SSS" -> R.color.color_rank_18
                "SS" -> R.color.color_rank_11_17
                "S" -> R.color.color_rank_7_10
                "A" -> R.color.color_rank_4_6
                "B" -> R.color.color_rank_2_3
                "C" -> R.color.cool_apk
                else -> R.color.cool_apk
            }
        ),
        textAlign = textAlign,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}
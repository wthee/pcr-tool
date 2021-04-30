package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.MainText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.BrowserUtil
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
            LazyColumn(state = state) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.padding(Dimen.mediuPadding)
                    ) {
                        Spacer(modifier = Modifier.width(Dimen.iconSize + Dimen.mediuPadding))
                        Spacer(modifier = Modifier.weight(0.25f))
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
                }
                items(info) { it ->
                    LeaderboardItem(it)
                }
            }

        }
        //回到顶部
        ExtendedFabCompose(
            icon = painterResource(id = R.drawable.ic_leader),
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
    Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
        val context = LocalContext.current
        val title = stringResource(id = R.string.visit_detail)

        IconCompose(data = info.icon)
        Card(modifier = Modifier
            .padding(start = Dimen.mediuPadding)
            .shadow(
                elevation = Dimen.cardElevation,
                shape = Shapes.large,
                clip = true
            )
            .clickable {
                //fixme 打开浏览器
                BrowserUtil.OpenWebView(context, info.url, title)
            }) {
            Column(modifier = Modifier.padding(Dimen.smallPadding)) {
                MainTitleText(text = info.name)
                Row {
                    GradeText(info.all, TextAlign.Start, modifier = Modifier.weight(0.25f))
                    GradeText(info.pvp, modifier = Modifier.weight(0.25f))
                    GradeText(info.clan, modifier = Modifier.weight(0.25f))
                    GradeText(info.tower, modifier = Modifier.weight(0.25f))
                }
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
        modifier = modifier
    )
}
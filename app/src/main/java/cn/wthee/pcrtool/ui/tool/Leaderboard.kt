package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.compose.TopBarCompose
import cn.wthee.pcrtool.ui.compose.marginTopBar
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.viewmodel.LeaderViewModel

@Composable
fun LeaderboardList(leaderViewModel: LeaderViewModel = hiltNavGraphViewModel()) {
    leaderViewModel.getLeader()
    val list = leaderViewModel.leaderData.observeAsState()
    //滚动监听
    val scrollState = rememberLazyListState()

    Surface {
        val marginTop: Dp = marginTopBar(scrollState)
        Box(modifier = Modifier.fillMaxSize()) {
            TopBarCompose(
                titleId = R.string.tool_leader,
                iconId = R.drawable.ic_leader,
                scrollState = scrollState
            )
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .padding(top = marginTop)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colors.background, shape = CardTopShape)
            ) {
                if (list.value == null || list.value!!.data == null || list.value!!.data!!.leader.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Dimen.mediuPadding)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(Dimen.topBarIconSize)
                                    .align(Alignment.Center)
                            )
                        }

                    }
                } else if (list.value!!.message != "success") {
                    item {
                        MainTitleText(text = stringResource(id = R.string.data_get_error))
                    }
                } else {
                    val info = list.value!!.data!!.leader
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
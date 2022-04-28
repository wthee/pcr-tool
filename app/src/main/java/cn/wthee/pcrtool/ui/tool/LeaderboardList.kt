package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.utils.openWebView
import cn.wthee.pcrtool.viewmodel.LeaderViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch

/**
 * 角色排行
 */
@Composable
fun LeaderboardList(
    scrollState: LazyListState,
    leaderViewModel: LeaderViewModel = hiltViewModel()
) {
    val leaderData = leaderViewModel.getLeader().collectAsState(initial = null).value
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val openDialog = remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = Dimen.largePadding)
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(
                    top = Dimen.largePadding
                )
            ) {
                Spacer(modifier = Modifier.width(Dimen.iconSize + Dimen.smallPadding))
                MainText(
                    text = stringResource(id = R.string.grade),
                    modifier = Modifier.weight(0.25f)
                )
                MainText(
                    text = stringResource(id = R.string.jjc),
                    modifier = Modifier.weight(0.25f)
                )
                MainText(
                    text = stringResource(id = R.string.clan),
                    modifier = Modifier.weight(0.25f)
                )
                MainText(
                    text = stringResource(id = R.string.tower),
                    modifier = Modifier.weight(0.25f)
                )
            }
            if (leaderData == null) {
                //显示占位图
                LazyColumn {
                    items(20) {
                        LeaderboardItem(LeaderboardData())
                    }
                }
            } else {
                LazyColumn(
                    state = scrollState
                ) {
                    items(
                        items = leaderData.leader,
                        key = {
                            it.url
                        }
                    ) {
                        LeaderboardItem(it)
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            //来源
            val url = stringResource(id = R.string.leader_source_url)
            val tip = stringResource(id = R.string.visit_detail)
            FabCompose(
                iconType = MainIconType.FRIEND_LINK
            ) {
                //打开网页
                openWebView(context, url, tip)
            }

            //更新说明
            FabCompose(
                iconType = MainIconType.UPDATE_INFO
            ) {
                openDialog.value = true
            }

            //回到顶部
            FabCompose(
                iconType = MainIconType.LEADER,
                text = stringResource(id = R.string.tool_leader)
            ) {
                coroutineScope.launch {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    //更新说明弹窗
    if (openDialog.value) {
        AlertDialog(
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    leaderData?.let {
                        MainText(text = it.desc, selectable = true)
                    }
                }
            },
            modifier = Modifier.padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding),
            onDismissRequest = {
                openDialog.value = false
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = Shape.medium,
            confirmButton = {
                //关闭
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    MainButton(text = stringResource(R.string.confirm)) {
                        openDialog.value = false
                    }
                }
            }
        )
    }
}

/**
 * 角色评价信息
 */
@Composable
private fun LeaderboardItem(info: LeaderboardData) {
    val placeholder = info.icon == ""
    val context = LocalContext.current
    val title = stringResource(id = R.string.visit_detail)
    Column(
        modifier = Modifier.padding(
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        MainTitleText(
            text = info.name,
            modifier = Modifier
                .padding(bottom = Dimen.mediumPadding)
                .placeholder(visible = placeholder, highlight = PlaceholderHighlight.shimmer())
        )
        MainCard(modifier = Modifier
            .placeholder(visible = placeholder, highlight = PlaceholderHighlight.shimmer()),
            onClick = {
                //打开浏览器
                if (!placeholder) {
                    openWebView(context, info.url, title)
                }
            }
        ) {
            Row(
                modifier = Modifier.padding(Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCompose(data = info.icon, size = Dimen.iconSize)
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
                "SSS" -> R.color.color_rank_18_20
                "SS" -> R.color.color_rank_11_17
                "S" -> R.color.color_rank_7_10
                "A" -> R.color.color_rank_4_6
                "B" -> R.color.color_rank_2_3
                else -> R.color.color_rank_21_23
            }
        ),
        textAlign = textAlign,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Preview
@Composable
private fun LeaderboardItemPreview() {
    PreviewBox {
        Column {
            LeaderboardItem(info = LeaderboardData(icon = "?"))
        }
    }
}
package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.viewmodel.LeaderViewModel
import kotlinx.coroutines.launch

/**
 * 角色排行
 */
@Composable
fun LeaderboardList(
    scrollState: LazyListState,
    leaderViewModel: LeaderViewModel = hiltViewModel()
) {
    val onlyLast = remember {
        mutableStateOf(false)
    }
    val sort = remember {
        mutableStateOf(0)
    }
    val asc = remember {
        mutableStateOf(false)
    }
    val flow = remember(sort.value, asc.value) {
        leaderViewModel.getLeader(sort.value, asc.value)
    }
    val responseData = flow.collectAsState(initial = null).value
    val filterLeaderData = if (onlyLast.value) {
        responseData?.data?.leader?.filter {
            it.isNew == 1
        }
    } else {
        responseData?.data?.leader
    }
    val leaderList = if (onlyLast.value) filterLeaderData else responseData?.data?.leader
    val coroutineScope = rememberCoroutineScope()
    val url = stringResource(id = R.string.leader_source_url)
    val context = LocalContext.current


    CommonResponseBox(
        responseData = responseData,
        fabContent = {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
            ) {
                //切换显示
                FabCompose(
                    iconType = MainIconType.FILTER,
                    text = stringResource(id = if (onlyLast.value) R.string.last_update else R.string.all)
                ) {
                    onlyLast.value = !onlyLast.value
                }
                //回到顶部
                FabCompose(
                    iconType = MainIconType.LEADER,
                    text = (leaderList?.size ?: 0).toString()
                ) {
                    coroutineScope.launch {
                        try {
                            scrollState.scrollToItem(0)
                        } catch (_: Exception) {
                        }
                    }
                }
            }
        }
    ) { data ->
        Column(
            modifier = Modifier
                .padding(horizontal = Dimen.largePadding)
                .fillMaxSize()
        ) {
            //更新
            Row(
                modifier = Modifier.padding(vertical = Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainTitleText(
                    text = "GameWith",
                    modifier = Modifier
                        .clickable {
                            VibrateUtil(context).single()
                            BrowserUtil.open(context, url)
                        }
                )
                FadeAnimation(visible = data.desc != "") {
                    CaptionText(
                        text = data.desc,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            //标题
            SortTitleGroup(sort, asc)
            LazyColumn(
                state = scrollState
            ) {
                itemsIndexed(
                    items = leaderList!!,
                    key = { _, it ->
                        it.name
                    }
                ) { index, it ->
                    LeaderboardItem(it, index)
                }
                item {
                    CommonSpacer()
                }
            }
        }
    }
}

/**
 * 排序标题组
 */
@Composable
private fun SortTitleGroup(sort: MutableState<Int>, asc: MutableState<Boolean>) {

    val titles = arrayListOf(
//        stringResource(id = R.string.quest),
        stringResource(id = R.string.tower),
        stringResource(id = R.string.jjc),
        stringResource(id = R.string.clan),
    )

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.padding(
            top = Dimen.smallPadding
        )
    ) {
        Spacer(modifier = Modifier.width(Dimen.iconSize + Dimen.smallPadding))
        titles.forEachIndexed { index, title ->
            SortTitleButton(
                modifier = Modifier.weight(0.25f),
                index = index + 1,
                text = title,
                sort = sort,
                asc = asc
            )
        }
    }
}


/**
 * 排序标题组件
 */
@Composable
private fun SortTitleButton(
    modifier: Modifier,
    index: Int,
    text: String,
    sort: MutableState<Int>,
    asc: MutableState<Boolean>
) {
    val context = LocalContext.current
    val color = if (sort.value == index) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable {
                VibrateUtil(context).single()
                if (sort.value == index) {
                    asc.value = !asc.value
                } else {
                    sort.value = index
                    asc.value = false
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        MainText(
            text = text,
            color = color
        )
        IconCompose(
            data = when (sort.value) {
                index -> {
                    if (asc.value) {
                        MainIconType.SORT_ASC
                    } else {
                        MainIconType.SORT_DESC
                    }
                }
                else -> MainIconType.SORT_NULL
            },
            size = Dimen.smallIconSize,
            tint = color
        )
    }
}

/**
 * 角色评价信息
 */
@Composable
private fun LeaderboardItem(info: LeaderboardData, index: Int) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        Row {
            MainTitleText(
                text = "${index + 1}",
                modifier = Modifier
                    .padding(bottom = Dimen.mediumPadding)
            )
            MainTitleText(
                text = info.name,
                modifier = Modifier
                    .padding(bottom = Dimen.mediumPadding, start = Dimen.smallPadding)
            )
        }

        MainCard(
            onClick = {
                //打开浏览器
                BrowserUtil.open(context, info.url)
            }
        ) {
            Row(
                modifier = Modifier.padding(Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCompose(data = info.icon, size = Dimen.iconSize)
//                GradeText(info.quest, info.questFlag, modifier = Modifier.weight(0.25f))
                GradeText(info.tower, info.towerFlag, modifier = Modifier.weight(0.25f))
                GradeText(info.pvp, info.pvpFlag, modifier = Modifier.weight(0.25f))
                GradeText(info.clan, info.clanFlag, modifier = Modifier.weight(0.25f))
            }
        }
    }

}

/**
 * 根据阶级返回颜色
 */
@Composable
fun GradeText(
    grade: String,
    flag: Int,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier
) {

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = grade,
            color = when (grade) {
                "SS+" -> colorRed
                "SS" -> colorRed
                "S+" -> colorPurple
                "S" -> colorPurple
                "A" -> colorGold
                "B" -> colorGreen
                "C" -> colorBlue
                "D" -> colorGray
                else -> colorGray
            },
            textAlign = textAlign,
            fontWeight = FontWeight.Bold,
        )
        if (flag == 1) {
            CaptionText(
                text = stringResource(id = R.string.experimental),
            )
        }
    }
}

@Preview
@Composable
private fun LeaderboardItemPreview() {
    PreviewBox {
        Column {
            LeaderboardItem(info = LeaderboardData(icon = "?"), 1)
        }
    }
}
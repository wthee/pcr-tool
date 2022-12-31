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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.LeaderViewModel
import kotlinx.coroutines.launch

/**
 * 角色排行
 */
@Composable
fun LeaderboardList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    leaderViewModel: LeaderViewModel = hiltViewModel()
) {
    val sort = remember {
        mutableStateOf(0)
    }
    val asc = remember {
        mutableStateOf(false)
    }
    val responseData =
        leaderViewModel.getLeader(sort.value, asc.value).collectAsState(initial = null).value
    val leaderList = responseData?.data

    val coroutineScope = rememberCoroutineScope()
    val url = stringResource(id = R.string.leader_source_url)
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //更新
        Row(
            modifier = Modifier.padding(
                horizontal = Dimen.largePadding,
                vertical = Dimen.mediumPadding
            ),
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

            MainTitleText(
                text = stringResource(id = R.string.only_jp),
                backgroundColor = colorRed,
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
                    .clickable {
                        VibrateUtil(context).single()
                        BrowserUtil.open(context, url)
                    }
            )
        }
        //标题
        SortTitleGroup(sort, asc)
        CommonResponseBox(
            responseData = responseData,
            fabContent = {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                ) {
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
        ) {
            LazyColumn(
                state = scrollState
            ) {
                itemsIndexed(
                    items = leaderList!!,
                    key = { _, it ->
                        it.name
                    }
                ) { index, it ->
                    LeaderboardItem(it, index, toCharacterDetail)
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
            vertical = Dimen.smallPadding
        )
    ) {
        Spacer(modifier = Modifier.width(Dimen.iconSize + Dimen.mediumPadding * 2))
        titles.forEachIndexed { index, title ->
            SortTitleButton(
                modifier = Modifier.weight(1f),
                index = index + 1,
                text = title,
                sort = sort,
                asc = asc
            )
        }
        Spacer(modifier = Modifier.width(Dimen.mediumPadding))
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
private fun LeaderboardItem(
    leader: LeaderboardData,
    index: Int,
    toCharacterDetail: (Int) -> Unit,
    characterViewModel: CharacterViewModel? = hiltViewModel()
) {
    val context = LocalContext.current
    //获取角色名
    val flow = remember(leader.unitId) {
        characterViewModel?.getCharacterBasicInfo(leader.unitId ?: 0)
    }
    val basicInfo = flow?.collectAsState(initial = null)?.value
    val hasUnitId = leader.unitId != null && leader.unitId != 0
    //是否登场角色
    val unknown = basicInfo == null || basicInfo.position == 0

    val textColor = if (!hasUnitId || unknown) {
        colorGray
    } else {
        Color.Unspecified
    }

    Row(
        modifier = Modifier
            .padding(
                start = Dimen.mediumPadding,
                end = Dimen.mediumPadding,
                top = Dimen.smallPadding,
                bottom = Dimen.mediumPadding
            )
            .fillMaxWidth()
    ) {
        Box {
            IconCompose(
                data = if (hasUnitId) {
                    ImageResourceHelper.getInstance()
                        .getMaxIconUrl(leader.unitId!!, forceJpType = true)
                } else {
                    leader.icon
                }
            ) {
                if (!unknown) {
                    toCharacterDetail(leader.unitId!!)
                }
            }
            if (!unknown) {
                PositionIcon(
                    position = basicInfo!!.position,
                    modifier = Modifier
                        .padding(
                            end = Dimen.divLineHeight,
                            bottom = Dimen.divLineHeight
                        )
                        .align(Alignment.BottomEnd),
                    size = Dimen.exSmallIconSize
                )
            }

        }


        MainCard(
            modifier = Modifier.padding(start = Dimen.mediumPadding),
            onClick = {
                BrowserUtil.open(context, leader.url)
            }
        ) {
            Column(modifier = Modifier.padding(bottom = Dimen.smallPadding)) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = Dimen.mediumPadding,
                        vertical = Dimen.smallPadding
                    )
                ) {
                    //名称
                    MainContentText(
                        text = "${index + 1}." + if (hasUnitId && !unknown) {
                            basicInfo!!.getNameF()
                        } else {
                            leader.name
                        },
                        textAlign = TextAlign.Start,
                        color = textColor
                    )

                    CaptionText(
                        text = leader.getTime(),
                        modifier = Modifier.fillMaxWidth(),
                        color = textColor
                    )

                }
                //评价
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                GradeText(info.quest, info.questFlag, modifier = Modifier.weight(0.25f))
                    GradeText(leader.tower, modifier = Modifier.weight(1f))
                    GradeText(leader.pvp, modifier = Modifier.weight(1f))
                    GradeText(leader.clan, modifier = Modifier.weight(1f))
                }
                //评分
                if (BuildConfig.DEBUG) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CaptionText(
                            text = leader.towerScore.toString(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        CaptionText(
                            text = leader.pvpScore.toString(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        CaptionText(
                            text = leader.clanScore.toString(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
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
    }
}


@CombinedPreviews
@Composable
private fun LeaderboardItemPreview() {
    PreviewLayout {
        LeaderboardItem(
            LeaderboardData(
                wikiTime = "2020/01/01",
                quest = "SS+",
                pvp = "S",
                clan = "A"
            ), 1, {}, null
        )
    }
}
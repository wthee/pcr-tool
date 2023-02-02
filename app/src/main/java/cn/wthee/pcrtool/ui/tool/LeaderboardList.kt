package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.LeaderViewModel
import kotlinx.coroutines.launch

/**
 * 角色排行筛选
 */
data class FilterLeaderboard(
    var sort: Int = 0,
    var asc: Boolean = false,
    var onlyLast: Boolean = false
)


/**
 * 角色排行
 */
@Composable
fun LeaderboardList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    leaderViewModel: LeaderViewModel = hiltViewModel()
) {
    val filter = leaderViewModel.filterLeader.value ?: FilterLeaderboard()

    val sort = remember {
        mutableStateOf(filter.sort)
    }
    filter.sort = sort.value
    val asc = remember {
        mutableStateOf(filter.asc)
    }
    filter.asc = asc.value
    val onlyLast = remember {
        mutableStateOf(filter.onlyLast)
    }
    filter.onlyLast = onlyLast.value

    val responseData =
        leaderViewModel.getLeader(filter).collectAsState(initial = null).value
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
                        BrowserUtil.open(url)
                    }
            )

            CaptionText(
                text = stringResource(id = R.string.only_jp),
                modifier = Modifier.padding(start = Dimen.smallPadding)
            )
        }
        //标题
        SortTitleGroup(sort, asc)
        CommonResponseBox(
            responseData = responseData,
            fabContent = {
                //切换显示
                FabCompose(
                    iconType = MainIconType.FILTER,
                    text = if (onlyLast.value) {
                        stringResource(id = R.string.last_update)
                    } else {
                        stringResource(id = R.string.all)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = Dimen.fabMargin,
                            bottom = Dimen.fabMargin * 2 + Dimen.fabSize
                        )
                ) {
                    onlyLast.value = !onlyLast.value
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                ) {
                    //重置
                    if (sort.value != 0 || asc.value || onlyLast.value) {
                        FabCompose(
                            iconType = MainIconType.RESET
                        ) {
                            sort.value = 0
                            asc.value = false
                            onlyLast.value = false
                        }
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
                items(count = 2) {
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
        stringResource(id = R.string.quest),
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
            color = color,
            style = MaterialTheme.typography.titleSmall
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
    val tipText = getLeaderUnknownTip()

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
                        .getMaxIconUrl(leader.unitId!!)
                } else {
                    R.drawable.unknown_item
                }
            ) {
                if (!unknown) {
                    toCharacterDetail(leader.unitId!!)
                } else {
                    ToastUtil.short(tipText)
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
                BrowserUtil.open(leader.url)
            }
        ) {
            Column(modifier = Modifier.padding(bottom = Dimen.smallPadding)) {
                //名称
                MainContentText(
                    modifier = Modifier.padding(
                        horizontal = Dimen.mediumPadding,
                        vertical = Dimen.smallPadding
                    ),
                    text = "${index + 1}、" + if (hasUnitId && !unknown) {
                        basicInfo!!.name
                    } else {
                        leader.name
                    },
                    textAlign = TextAlign.Start,
                    color = textColor,
                    maxLines = 1
                )
                //评价
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GradeText(leader.quest, modifier = Modifier.weight(1f))
                    GradeText(leader.tower, modifier = Modifier.weight(1f))
                    GradeText(leader.pvp, modifier = Modifier.weight(1f))
                    GradeText(leader.clan, modifier = Modifier.weight(1f))
                }

                Row {
                    if (unknown) {
                        //提示
                        CaptionText(
                            modifier = Modifier.padding(start = Dimen.mediumPadding),
                            text = if (!hasUnitId) {
                                stringResource(id = R.string.leader_need_sync)
                            } else {
                                tipText
                            },
                            color = colorGray
                        )
                    }

                    //日期
                    CaptionText(
                        text = leader.updateTime?.substring(0, 11) ?: "-",
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Dimen.smallPadding),
                        color = textColor
                    )
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

/**
 * 未实装提示
 */
@Composable
fun getLeaderUnknownTip(): String {
    val regionName = if (LocalInspectionMode.current) {
        ""
    } else {
        getRegionName(MainActivity.regionType)

    }
    return stringResource(
        id = R.string.unknown_character_type,
        regionName
    )
}

@CombinedPreviews
@Composable
private fun LeaderboardItemPreview() {
    PreviewLayout {
        LeaderboardItem(
            LeaderboardData(
                quest = "SS+",
                pvp = "S",
                clan = "A"
            ), 1, {}, null
        )
    }
}
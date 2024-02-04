package cn.wthee.pcrtool.ui.tool.leaderboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.ExpandableHeader
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorBlue
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.toDate
import kotlinx.coroutines.launch


/**
 * 角色排行
 */
@Composable
fun LeaderboardScreen(
    toCharacterDetail: (Int) -> Unit,
    leaderBoardViewModel: LeaderboardViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val uiState by leaderBoardViewModel.uiState.collectAsStateWithLifecycle()

    val filter = uiState.filterLeader
    val sort = remember {
        mutableIntStateOf(filter.sort)
    }
    filter.sort = sort.intValue
    val asc = remember {
        mutableStateOf(filter.asc)
    }
    filter.asc = asc.value
    val onlyLast = remember {
        mutableStateOf(filter.onlyLast)
    }
    filter.onlyLast = onlyLast.value

    LaunchedEffect(sort.intValue, asc.value, onlyLast.value) {
        leaderBoardViewModel.refreshLeader(filter)
    }


    MainScaffold(
        fab = {
            //重置
            if (sort.intValue != 0 || asc.value || onlyLast.value) {
                MainSmallFab(
                    iconType = MainIconType.RESET,
                    onClick = {
                        sort.intValue = 0
                        asc.value = false
                        onlyLast.value = false
                    }
                )
            }

            //回到顶部
            MainSmallFab(
                iconType = MainIconType.LEADER,
                text = (uiState.currentList.size).toString(),
                loading = uiState.loadState == LoadState.Loading,
                onClick = {
                    coroutineScope.launch {
                        try {
                            scrollState.scrollToItem(0)
                        } catch (_: Exception) {
                        }
                    }
                }
            )
        },
        secondLineFab = {
            if (uiState.loadState == LoadState.Success) {
                //切换显示
                MainSmallFab(
                    iconType = MainIconType.FILTER,
                    text = if (onlyLast.value) {
                        stringResource(id = R.string.last_update)
                    } else {
                        stringResource(id = R.string.all)
                    },
                    modifier = Modifier
                        .padding(
                            end = Dimen.fabMargin,
                            bottom = Dimen.fabMarginLargeBottom
                        ),
                    onClick = {
                        onlyLast.value = !onlyLast.value
                    }
                )
            }
        }
    ) {
        Column {

            ExpandableHeader(
                scrollState = scrollState,
                title = stringResource(id = R.string.leader_source),
                startText = stringResource(id = R.string.only_jp),
                url = stringResource(id = R.string.leader_source_url)
            ) {
                SortTitleGroup(sort, asc)
            }

            StateBox(
                stateType = uiState.loadState,
                loadingContent = {
                    Column {
                        for (i in 0..10) {
                            LeaderboardItem(LeaderboardData(), i, null, toCharacterDetail)
                        }
                    }
                }
            ) {
                LazyColumn(
                    state = scrollState
                ) {
                    itemsIndexed(
                        items = uiState.currentList,
                        key = { _, it ->
                            it.name
                        }
                    ) { index, it ->
                        //获取角色名
                        val flow = remember(it.unitId) {
                            leaderBoardViewModel.getCharacterBasicInfo(it.unitId ?: 0)
                        }
                        val basicInfo by flow.collectAsState(initial = null)
                        LeaderboardItem(
                            leader = it,
                            index = index,
                            basicInfo = basicInfo,
                            toCharacterDetail = toCharacterDetail
                        )
                    }
                    items(count = 2) {
                        CommonSpacer()
                    }
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
        MainIcon(
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
    basicInfo: CharacterInfo?,
    toCharacterDetail: (Int) -> Unit
) {
    val placeholder = leader.unitId == 0
    val hasUnitId = leader.unitId != null && leader.unitId != 0
    //是否登场角色
    val unknown = basicInfo == null || basicInfo.position == 0

    val textColor = if (!hasUnitId || unknown) {
        colorGray
    } else {
        Color.Unspecified
    }
    val tipText = getLeaderUnknownTip(hasUnitId)

    Row(
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding,
            )
            .fillMaxWidth()
    ) {
        //图标
        LeaderCharacterIcon(
            hasUnitId = hasUnitId,
            placeholder = placeholder,
            unitId = leader.unitId,
            url = leader.url,
            unknown = unknown,
            tipText = tipText,
            toCharacterDetail = toCharacterDetail
        )

        //信息
        MainCard(
            modifier = Modifier
                .padding(
                    start = Dimen.mediumPadding
                )
                .placeholder(placeholder),
            onClick = {
                if (!unknown) {
                    leader.unitId?.let { toCharacterDetail(it) }
                } else {
                    ToastUtil.short(tipText)
                }
            }
        ) {
            //名称
            MainContentText(
                modifier = Modifier.padding(
                    horizontal = Dimen.mediumPadding,
                    vertical = Dimen.smallPadding
                ),
                text = "${index + 1}." + if (hasUnitId && !unknown) {
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

            CharacterTagRow(
                modifier = Modifier.padding(top = Dimen.largePadding, bottom = Dimen.smallPadding),
                unknown = unknown,
                basicInfo = basicInfo,
                tipText = tipText,
                endText = if (leader.updateTime == null) {
                    stringResource(id = R.string.none)
                } else {
                    leader.updateTime.toDate
                },
                endTextColor = if (leader.updateTime == null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    textColor
                },
                showUniqueEquipType = false
            )

        }

    }

}

/**
 * 角色图标、wiki跳转
 */
@Composable
fun LeaderCharacterIcon(
    hasUnitId: Boolean,
    placeholder: Boolean = false,
    unitId: Int?,
    url: String,
    unknown: Boolean,
    tipText: String,
    toCharacterDetail: (Int) -> Unit
) {
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        //角色图标
        MainIcon(
            data = if (hasUnitId) {
                ImageRequestHelper.getInstance()
                    .getMaxIconUrl(unitId!!)
            } else {
                R.drawable.unknown_item
            },
            modifier = Modifier.placeholder(placeholder),
            onClick = {
                if (!unknown) {
                    unitId?.let { toCharacterDetail(it) }
                } else {
                    ToastUtil.short(tipText)
                }
            }
        )

        //wiki页面
        if (!placeholder) {
            CaptionText(
                text = "wiki",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = Dimen.exSmallPadding)
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        VibrateUtil(context).single()
                        BrowserUtil.open(url)
                    }
                    .padding(horizontal = Dimen.exSmallPadding)
            )
        }

    }
}

/**
 * 根据阶级返回颜色
 */
@Composable
fun GradeText(
    grade: String,
    modifier: Modifier
) {

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        MainText(
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
            }
        )
    }
}

/**
 * 未实装提示
 */
@Composable
fun getLeaderUnknownTip(hasUnitId: Boolean): String {
    val regionName = if (LocalInspectionMode.current) {
        ""
    } else {
        stringResource(MainActivity.regionType.stringId)
    }
    return if (!hasUnitId) {
        stringResource(id = R.string.leader_need_sync)
    } else {
        stringResource(
            id = R.string.unknown_character_type,
            regionName
        )
    }
}

@CombinedPreviews
@Composable
private fun LeaderboardItemPreview() {
    PreviewLayout {
        SortTitleGroup(
            sort = remember {
                mutableIntStateOf(0)
            },
            asc = remember {
                mutableStateOf(false)
            }
        )

        LeaderboardItem(
            leader = LeaderboardData(
                unitId = 1,
                quest = "SS+",
                pvp = "S",
                clan = "A",
                tower = "A",
                updateTime = "2023-02-02 22:33:44"
            ),
            index = 1,
            basicInfo = CharacterInfo(
                id = 1,
                name = stringResource(id = R.string.debug_name),
                position = 100,
                uniqueEquipType = 2
            ),
        ) {}
    }
}
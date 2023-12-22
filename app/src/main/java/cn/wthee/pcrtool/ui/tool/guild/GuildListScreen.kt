package cn.wthee.pcrtool.ui.tool.guild

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GuildAllMember
import cn.wthee.pcrtool.data.db.view.GuildMemberInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.launch


/**
 * 角色公会
 */
@Composable
fun GuildListScreen(
    toCharacterDetail: (Int) -> Unit,
    guildListViewModel: GuildListViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by guildListViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyStaggeredGridState()


    MainScaffold(
        fab = {
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.GUILD,
                text = stringResource(id = R.string.tool_guild),
            ) {
                scope.launch {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (_: Exception) {
                    }
                }
            }
        }
    ) {
        StateBox(stateType = uiState.loadingState) {
            LazyVerticalStaggeredGrid(
                state = scrollState,
                columns = StaggeredGridCells.Adaptive(getItemWidth())
            ) {
                items(
                    items = uiState.guildList,
                    key = {
                        it.guildId
                    }
                ) {
                    GuildItem(
                        it,
                        toCharacterDetail = toCharacterDetail
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        }
    }

}


/**
 * 公会
 */
@Composable
private fun GuildItem(
    guild: GuildAllMember,
    toCharacterDetail: (Int) -> Unit
) {
    val memberList = arrayListOf<GuildMemberInfo>()
    val names = guild.getNameList()
    var masterName = ""
    guild.unitIds.intArrayList.forEachIndexed { index, unitId ->
        try {
            memberList.add(
                GuildMemberInfo(
                    unitId,
                    names[index]
                )
            )
            if (unitId / 100 == guild.guildMasterId) {
                masterName = names[index]
            }
        } catch (e: IndexOutOfBoundsException) {
            LogReportUtil.upload(e, "${guild.guildId}-${unitId}")
        }
    }
    //调整排序
    memberList.sortWith(compareUnit(masterName))
    val iconIdList = arrayListOf<Int>()
    memberList.forEach {
        iconIdList.add(it.unitId)
    }

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        MainTitleText(
            text = guild.guildName,
            modifier = Modifier.padding(bottom = Dimen.mediumPadding)
        )
        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
                //内容
                if (guild.getDesc() != Constants.UNKNOWN) {
                    MainContentText(
                        text = guild.getDesc(),
                        modifier = Modifier.padding(
                            top = Dimen.mediumPadding,
                            start = Dimen.mediumPadding,
                            end = Dimen.mediumPadding
                        ),
                        textAlign = TextAlign.Start
                    )
                }
                //角色图标列表
                GridIconList(
                    icons = iconIdList,
                    onClickItem = toCharacterDetail
                )
            }
        }
    }

}

/**
 * 按公会创建人、角色名排序
 */
private fun compareUnit(masterName: String) = Comparator<GuildMemberInfo> { gm1, gm2 ->
    if (masterName == "") {
        //无公会
        gm1.unitName.compareTo(gm2.unitName)
    } else {
        if (gm1.unitName == masterName) {
            if (gm2.unitName == masterName) {
                //创建人，按 unitId 排序
                gm1.unitId.compareTo(gm2.unitId)
            } else {
                -1
            }
        } else {
            if (gm2.unitName == masterName) {
                1
            } else {
                //非创建人，按 unitName 排序
                if (gm1.unitName == gm2.unitName) {
                    gm1.unitId.compareTo(gm2.unitId)
                } else {
                    gm1.unitName.compareTo(gm2.unitName)
                }
            }
        }
    }
}

@CombinedPreviews
@Composable
private fun GuildItemPreview() {
    PreviewLayout {
        GuildItem(guild = GuildAllMember(), toCharacterDetail = {})
    }
}
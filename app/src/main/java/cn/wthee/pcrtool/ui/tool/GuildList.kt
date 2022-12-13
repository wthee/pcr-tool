package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GuildAllMember
import cn.wthee.pcrtool.data.db.view.GuildMemberInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.GuildViewModel
import kotlinx.coroutines.launch

/**
 * 角色公会
 */
@Composable
fun GuildList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    guildViewModel: GuildViewModel = hiltViewModel()
) {
    val guilds = guildViewModel.getGuilds().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (guilds.isNotEmpty()) {
            LazyColumn(
                state = scrollState
            ) {
                items(
                    items = guilds,
                    key = {
                        it.guildId
                    }
                ) {
                    GuildItem(it, toCharacterDetail = toCharacterDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.GUILD,
            text = stringResource(id = R.string.tool_guild),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
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
                GridIconListCompose(
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

@Preview
@Composable
private fun GuildItemPreview() {
    PreviewBox {
        Column {
            GuildItem(guild = GuildAllMember(), toCharacterDetail = {})
        }
    }
}
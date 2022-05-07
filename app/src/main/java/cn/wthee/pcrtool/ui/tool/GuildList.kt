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
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.GuildAllMember
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
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
                } catch (e: Exception) {
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
                if (guild.desc != Constants.UNKNOWN) {
                    MainContentText(
                        text = guild.desc,
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
                    icons = guild.memberIds,
                    onClickItem = toCharacterDetail
                )
                // 新加入的成员
                if (guild.newMemberIds.isNotEmpty()) {
                    MainContentText(
                        text = stringResource(R.string.new_member),
                        modifier = Modifier.padding(
                            top = Dimen.largePadding,
                            start = Dimen.mediumPadding,
                            end = Dimen.mediumPadding
                        ),
                        textAlign = TextAlign.Start
                    )
                    GridIconListCompose(
                        icons = guild.newMemberIds,
                        onClickItem = toCharacterDetail
                    )
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
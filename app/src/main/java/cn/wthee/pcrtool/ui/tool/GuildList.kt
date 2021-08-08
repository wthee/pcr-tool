package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
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
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.GuildViewModel
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.launch

/**
 * 角色公会
 */
@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun GuildList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    guildViewModel: GuildViewModel = hiltViewModel()
) {
    val guilds = guildViewModel.getGuilds().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SlideAnimation(visible = guilds.isNotEmpty()) {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(Dimen.largePadding)
            ) {
                items(guilds) {
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
@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun GuildItem(
    guild: GuildAllMember,
    toCharacterDetail: (Int) -> Unit
) {

    MainTitleText(
        text = guild.guildName,
        modifier = Modifier.padding(bottom = Dimen.mediuPadding)
    )
    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding)) {
        Column(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
            //内容
            MainContentText(
                text = guild.desc,
                modifier = Modifier.padding(
                    top = Dimen.mediuPadding,
                    start = Dimen.mediuPadding,
                    end = Dimen.mediuPadding
                ),
                textAlign = TextAlign.Start
            )
            //图标/描述
            IconListCompose(
                icons = guild.memberIds,
                toCharacterDetail = toCharacterDetail
            )
            // 新加入的成员
            if (guild.newMemberIds.isNotEmpty()) {
                MainContentText(
                    text = stringResource(R.string.new_member),
                    modifier = Modifier.padding(
                        top = Dimen.largePadding,
                        start = Dimen.mediuPadding,
                        end = Dimen.mediuPadding
                    ),
                    textAlign = TextAlign.Start
                )
                IconListCompose(
                    icons = guild.newMemberIds,
                    toCharacterDetail = toCharacterDetail
                )
            }
        }
    }
}

@Preview
@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun GuildItemPreview() {
    PreviewBox {
        Column {
            GuildItem(guild = GuildAllMember(), toCharacterDetail = {})
        }
    }
}
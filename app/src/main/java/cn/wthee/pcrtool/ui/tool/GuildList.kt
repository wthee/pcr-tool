package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.GuildData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.fillPlaceholder
import cn.wthee.pcrtool.viewmodel.GuildViewModel
import kotlinx.coroutines.launch

/**
 * 角色公会
 */
@ExperimentalAnimationApi
@Composable
fun GuildList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    guildViewModel: GuildViewModel = hiltViewModel()
) {
    guildViewModel.getGuilds()
    val guilds = guildViewModel.guilds.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {
        SlideAnimation(visible = guilds.value != null) {
            guilds.value?.let { data ->
                LazyColumn(
                    state = scrollState,
                    contentPadding = PaddingValues(Dimen.mediuPadding)
                ) {
                    items(data) {
                        GuildItem(it, toCharacterDetail)
                    }
                    item {
                        CommonSpacer()
                    }
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
                scrollState.scrollToItem(0)
            }
        }
    }


}

/**
 * 公会
 */
@Composable
private fun GuildItem(guild: GuildData, toCharacterDetail: (Int) -> Unit) {
    MainTitleText(
        text = guild.guildName,
        modifier = Modifier.padding(bottom = Dimen.mediuPadding)
    )
    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding)) {
        Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
            //内容
            MainContentText(
                text = guild.getDesc(),
                modifier = Modifier.padding(bottom = Dimen.smallPadding),
                textAlign = TextAlign.Start
            )
            //图标/描述
            IconListCompose(
                guild.getMemberIds().fillPlaceholder(),
                toCharacterDetail
            )
        }
    }
}


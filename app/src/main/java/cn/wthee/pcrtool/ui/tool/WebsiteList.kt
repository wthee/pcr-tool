package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.WebsiteGroupData
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.viewmodel.WebsiteViewModel
import kotlinx.coroutines.launch


/**
 * 网站列表
 */
@Composable
fun WebsiteList(
    scrollState: LazyListState,
    websiteViewModel: WebsiteViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val flow = remember {
        websiteViewModel.getWebsiteList()
    }
    val responseData = flow.collectAsState(initial = null).value

    //列表
    CommonResponseBox(
        responseData = responseData,
        fabContent = {
            //回到顶部
            FabCompose(
                iconType = MainIconType.WEBSITE_BOOKMARK,
                text = stringResource(id = R.string.tool_website),
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
    ) {data ->
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = Dimen.mediumPadding)
                .fillMaxSize(),
            state = scrollState
        ) {
            items(
                items = data,
                key = {
                    it.type
                }
            ) {
                WebsiteGroup(it)
            }
            item {
                CommonSpacer()
            }
        }
    }

}

/**
 * 网站分组
 */
@Composable
private fun WebsiteGroup(
    groupData: WebsiteGroupData
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(horizontal = Dimen.mediumPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainText(
            text = getTitle(groupData.type),
            modifier = Modifier.padding(top = Dimen.largePadding * 2, bottom = Dimen.mediumPadding)
        )
        if (groupData.websiteList.isEmpty()) {
            CenterTipText(text = stringResource(id = R.string.no_data))
        } else {
            VerticalGrid(
                maxColumnWidth = getItemWidth(),
                modifier = Modifier.animateContentSize(defaultSpring())
            ) {
                groupData.websiteList.forEach {
                    SettingItem(
                        iconType = it.icon,
                        title = it.title,
                        titleColor = MaterialTheme.colorScheme.primary,
                        summary = it.summary,
                        colorFilter = null,
                        onClick = {
                            BrowserUtil.open(context, it.url)
                        }
                    ) {
                        IconCompose(data = MainIconType.MORE, size = Dimen.fabIconSize)
                    }
                }
            }
        }
    }
}

/**
 * 获取分组标题
 */
@Composable
private fun getTitle(type: Int) = stringResource(
    id = when (type) {
        1 -> R.string.website_type_1
        2 -> R.string.website_type_2
        3 -> R.string.website_type_3
        4 -> R.string.website_type_4
        else -> R.string.other
    }
)
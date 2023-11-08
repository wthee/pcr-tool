package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.model.WebsiteData
import cn.wthee.pcrtool.data.model.WebsiteGroupData
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.getRegionName
import kotlinx.coroutines.launch


/**
 * 网站列表
 */
@Composable
fun WebsiteScreen(
    scrollState: LazyListState,
    websiteViewModel: WebsiteViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by websiteViewModel.uiState.collectAsStateWithLifecycle()


    val type = remember {
        mutableIntStateOf(0)
    }
    val tabs = arrayListOf(
        stringResource(id = R.string.all),
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp),
    )


    MainScaffold(
        secondLineFab = {
            //切换类型
            SelectTypeFab(
                icon = MainIconType.FILTER,
                tabs = tabs,
                type = type,
                paddingValues = PaddingValues(
                    end = Dimen.fabMargin,
                    bottom = Dimen.fabMargin * 2 + Dimen.fabSize
                )
            )
        },
        fab = {
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.WEBSITE_BOOKMARK,
                text = stringResource(id = R.string.tool_website),
                extraContent = if (uiState.websiteResponseData == null) {
                    //加载提示
                    {
                        CircularProgressCompose()
                    }
                } else {
                    null
                }
            ) {
                coroutineScope.launch {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (_: Exception) {
                    }
                }
            }
        }
    ) {
        StateBox(
            stateType = uiState.loadingState,
            loadingContent = {
                VerticalGrid(
                    itemWidth = getItemWidth(),
                    contentPadding = Dimen.mediumPadding,
                    modifier = Modifier.animateContentSize(defaultSpring())
                ) {
                    for (i in 0..10) {
                        WebsiteItem(data = WebsiteData())
                    }
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(), state = scrollState
            ) {
                items(
                    items = uiState.websiteResponseData?.data ?: emptyList(),
                    key = {
                        it.type
                    }
                ) {
                    WebsiteGroup(it, type.intValue)
                }
                item {
                    CommonSpacer()
                }
                item {
                    CommonSpacer()
                }
            }
        }
    }

}

/**
 * 网站分组
 */
@Composable
private fun WebsiteGroup(
    groupData: WebsiteGroupData,
    regionIndex: Int
) {

    val websiteList = groupData.websiteList.filter {
        it.region == regionIndex + 1 || it.region == 1 || regionIndex == 0
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainText(
            text = groupData.typeName,
            modifier = Modifier.padding(top = Dimen.largePadding * 2, bottom = Dimen.mediumPadding)
        )
        if (websiteList.isEmpty()) {
            CenterTipText(text = stringResource(id = R.string.no_data))
        } else {
            VerticalGrid(
                itemWidth = getItemWidth(),
                contentPadding = Dimen.mediumPadding,
                modifier = Modifier.animateContentSize(defaultSpring())
            ) {
                websiteList.forEach {
                    WebsiteItem(data = it)
                }
            }
        }
    }
}

@Composable
private fun WebsiteItem(data: WebsiteData) {
    val placeholder = data.id == 0

    val regionName = if (LocalInspectionMode.current) {
        //预览模式
        stringResource(id = R.string.unknown)
    } else {
        if (data.region == 1) {
            stringResource(id = R.string.all)
        } else {
            getRegionName(RegionType.getByValue(data.region))
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = Dimen.mediumPadding, horizontal = Dimen.largePadding)
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            //区服
            MainTitleText(
                text = regionName,
                modifier = Modifier.commonPlaceholder(placeholder)
            )
            //摘要
            if (data.summary != "") {
                MainTitleText(
                    text = data.summary,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }

        }

        MainCard(
            modifier = Modifier
                .padding(top = Dimen.mediumPadding)
                .heightIn(min = Dimen.cardHeight)
                .commonPlaceholder(placeholder),
            onClick = {
                if (!placeholder) {
                    BrowserUtil.open(data.url)
                }
            }
        ) {
            Row(
                modifier = Modifier.padding(Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //图标
                MainIcon(
                    data = data.icon, size = Dimen.smallIconSize
                )

                //标题
                Subtitle1(
                    text = data.title,
                    modifier = Modifier.padding(start = Dimen.mediumPadding)
                )
            }

            Row(
                modifier = Modifier
                    .padding(bottom = Dimen.mediumPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (data.browserType == 0 || data.browserType == 1) {
                    MainIcon(
                        data = MainIconType.BROWSER_PC,
                        size = Dimen.smallIconSize,
                        modifier = Modifier.padding(end = Dimen.mediumPadding)
                    )
                }
                if (data.browserType == 0 || data.browserType == 2) {
                    MainIcon(
                        data = MainIconType.BROWSER_PHONE,
                        size = Dimen.smallIconSize,
                        modifier = Modifier.padding(end = Dimen.mediumPadding)
                    )
                }
                if (data.browserType == 3) {
                    MainIcon(
                        data = MainIconType.BROWSER_APP,
                        size = Dimen.smallIconSize,
                        modifier = Modifier.padding(end = Dimen.mediumPadding)
                    )
                }
            }
        }
    }


}


@CombinedPreviews
@Composable
private fun WebsiteItemPreview() {
    PreviewLayout {
        WebsiteItem(
            WebsiteData(
                title = stringResource(id = R.string.debug_long_text),
                summary = stringResource(id = R.string.debug_short_text)
            )
        )
    }
}
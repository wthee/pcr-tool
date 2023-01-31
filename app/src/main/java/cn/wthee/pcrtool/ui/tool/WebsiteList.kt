package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.WebsiteData
import cn.wthee.pcrtool.data.model.WebsiteGroupData
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.getRegionName
import cn.wthee.pcrtool.viewmodel.WebsiteViewModel
import kotlinx.coroutines.launch


/**
 * 网站列表
 */
@Composable
fun WebsiteList(
    scrollState: LazyListState, websiteViewModel: WebsiteViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val flow = remember {
        websiteViewModel.getWebsiteList()
    }
    val responseData = flow.collectAsState(initial = null).value

    val type = remember {
        mutableStateOf(0)
    }
    val tabs = arrayListOf(
        stringResource(id = R.string.all),
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp),
    )


    //列表
    CommonResponseBox(responseData = responseData, fabContent = {
        //切换类型
        SelectTypeCompose(
            modifier = Modifier.align(Alignment.BottomEnd),
            icon = MainIconType.FILTER,
            tabs = tabs,
            type = type,
            paddingValues = PaddingValues(
                end = Dimen.fabMargin,
                bottom = Dimen.fabMargin * 2 + Dimen.fabSize
            )
        )

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
    }) { data ->

        LazyColumn(
            modifier = Modifier.fillMaxSize(), state = scrollState
        ) {
            items(items = data, key = {
                it.type
            }) {
                WebsiteGroup(it, type.value)
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
            text = getTitle(groupData.type),
            modifier = Modifier.padding(top = Dimen.largePadding * 2, bottom = Dimen.mediumPadding)
        )
        if (websiteList.isEmpty()) {
            CenterTipText(text = stringResource(id = R.string.no_data))
        } else {
            VerticalGrid(
                itemWidth = getItemWidth(),
                contentPadding = Dimen.mediumPadding ,
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
    val regionName = if(LocalInspectionMode.current){
        //预览模式
        stringResource(id = R.string.unknown)
    }else{
        getRegionName(data.region)
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
                text = regionName
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
                .heightIn(min = Dimen.cardHeight),
            onClick = {
                BrowserUtil.open(data.url)
            }
        ) {
            Row(
                modifier = Modifier.padding(Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //图标
                IconCompose(
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
                    IconCompose(
                        data = MainIconType.BROWSER_PC,
                        size = Dimen.smallIconSize,
                        modifier = Modifier.padding(end = Dimen.mediumPadding)
                    )
                }
                if (data.browserType == 0 || data.browserType == 2) {
                    IconCompose(
                        data = MainIconType.BROWSER_PHONE,
                        size = Dimen.smallIconSize,
                        modifier = Modifier.padding(end = Dimen.mediumPadding)
                    )
                }
                if (data.browserType == 3) {
                    IconCompose(
                        data = MainIconType.BROWSER_APP,
                        size = Dimen.smallIconSize,
                        modifier = Modifier.padding(end = Dimen.mediumPadding)
                    )
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
        5 -> R.string.website_type_5
        6 -> R.string.website_type_6
        else -> R.string.other
    }
)


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
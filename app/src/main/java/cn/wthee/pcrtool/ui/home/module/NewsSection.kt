package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.tool.NewsItem
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import kotlinx.coroutines.launch


/**
 * 公告
 */
@Composable
fun NewsSection(
    actions: NavActions,
    isEditMode: Boolean,
    orderStr: String,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val id = OverviewType.NEWS.id
    //公告列表
    val newsListFlow = remember {
        overviewViewModel.getNewsOverview()
    }
    val newsList by newsListFlow.collectAsState(initial = null)


    Section(
        id = id,
        titleId = R.string.tool_news,
        iconType = MainIconType.NEWS,
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode) {
                scope.launch {
                    editOverviewMenuOrder(context, id)
                }
            }
            else{
                actions.toNews
            }
        }
    ) {
        Column {
            if (newsList == null) {
                for (i in 0 until 3) {
                    NewsItem(news = NewsTable())
                }
            } else {
                newsList?.let { list ->
                    if (list.data?.isNotEmpty() == true) {
                        list.data!!.forEach {
                            NewsItem(news = it)
                        }
                    } else {
                        CenterTipText(stringResource(id = R.string.no_data))
                    }
                }

            }
        }
    }
}
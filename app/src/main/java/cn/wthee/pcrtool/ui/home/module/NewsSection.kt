package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.tool.NewsItem


/**
 * 公告
 */
@Composable
fun NewsSection(
    updateOrderData: (Int) -> Unit,
    toNews: () -> Unit,
    isEditMode: Boolean,
    orderStr: String,
    newsSectionViewModel: NewsSectionViewModel = hiltViewModel()
) {
    val id = OverviewType.NEWS.id
    val uiState by newsSectionViewModel.uiState.collectAsStateWithLifecycle()


    Section(
        id = id,
        titleId = R.string.tool_news,
        iconType = MainIconType.NEWS,
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode) {
                updateOrderData(id)
            } else {
                toNews()
            }
        }
    ) {
        Column {
            if (uiState.newsList == null) {
                for (i in 0 until 3) {
                    NewsItem(news = NewsTable())
                }
            } else {
                uiState.newsList?.let { list ->
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
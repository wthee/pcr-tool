package cn.wthee.pcrtool.ui.home.news

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.AppResumeEffect
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.VerticalGridList
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.tool.news.NewsItem


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
    val uiState by newsSectionViewModel.uiState.collectAsStateWithLifecycle()
    AppResumeEffect {
        newsSectionViewModel.getNewsOverview()
    }

    NewsSectionContent(
        isEditMode = isEditMode,
        orderStr = orderStr,
        updateOrderData = updateOrderData,
        toNews = toNews,
        uiState = uiState
    )
}

@Composable
private fun NewsSectionContent(
    isEditMode: Boolean,
    orderStr: String,
    updateOrderData: (Int) -> Unit,
    toNews: () -> Unit,
    uiState: NewsSectionUiState
) {
    val id = OverviewType.NEWS.id
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
        StateBox(
            stateType = uiState.loadState,
            loadingContent = {
                VerticalGridList(
                    itemCount = 3,
                    itemWidth = getItemWidth(),
                ) {
                    NewsItem(news = NewsTable())
                }
            },
            errorContent = {
                CenterTipText(stringResource(id = R.string.data_get_error))
            }
        ) {
            uiState.newsList?.let { newsList ->
                VerticalGridList(
                    itemCount = newsList.size,
                    itemWidth = getItemWidth(),
                ) {
                    NewsItem(news = newsList[it], fillMaxHeight = true)
                }
            }
        }
    }
}


@CombinedPreviews
@Composable
private fun NewsSectionContentPreview() {
    PreviewLayout {
        NewsSectionContent(
            uiState = NewsSectionUiState(
                newsList = arrayListOf(
                    NewsTable(
                        id = 1,
                        title = stringResource(id = R.string.debug_short_text)
                    ),
                    NewsTable(
                        id = 1,
                        title = stringResource(id = R.string.debug_long_text)
                    ),
                    NewsTable(
                        id = 1,
                        title = stringResource(id = R.string.debug_short_text)
                    ),
                ),
                loadState = LoadState.Success
            ),
            isEditMode = false,
            orderStr = "${OverviewType.NEWS.id}",
            updateOrderData = { },
            toNews = { }
        )
    }
}
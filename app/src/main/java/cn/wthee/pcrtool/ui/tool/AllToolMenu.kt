package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.home.ToolMenu
import cn.wthee.pcrtool.ui.theme.Dimen

/**
 * 全部工具
 */
@ExperimentalMaterialApi
@Composable
fun AllToolMenu(scrollState: LazyListState, actions: NavActions) {

    Column(
        modifier = Modifier
            .padding(horizontal = Dimen.mediumPadding)
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        MainText(
            text = stringResource(id = R.string.tool_more),
            modifier = Modifier
                .padding(
                    top = Dimen.largePadding,
                    bottom = Dimen.mediumPadding
                )
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
            item {
                ToolMenu(actions = actions, all = true)
            }
        }
    }

}
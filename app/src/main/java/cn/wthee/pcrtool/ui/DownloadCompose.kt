package cn.wthee.pcrtool.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.fillZero

/**
 * 下载组件
 */
@Composable
fun DownloadCompose(viewModel: NavViewModel) {
    val downloadState = viewModel.downloadProgress.observeAsState().value ?: -2
    if (downloadState > -2) {
        val text = when (downloadState) {
            -1 -> stringResource(id = R.string.db_checking)
            in 0..99 -> stringResource(id = R.string.db_downloading) + downloadState.toString()
                .fillZero() + "%"
            else -> stringResource(id = R.string.db_downloaded)
        }
        ExtendedFloatingActionButton(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary,
            modifier = Modifier
                .height(Dimen.fabSize)
                .padding(end = Dimen.fabPadding),
            icon = {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(Dimen.fabIconSize)
                )
            },
            text = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.primary
                )
            },
            onClick = {}
        )
    }
}
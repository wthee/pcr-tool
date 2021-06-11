package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.theme.Dimen
import kotlinx.coroutines.launch


/**
 * 顶部工具栏
 *
 */
@ExperimentalAnimationApi
@Composable
fun TopBarCompose(actions: NavActions) {
    val coroutineScope = rememberCoroutineScope()
    val downloadState = MainActivity.navViewModel.downloadProgress.observeAsState().value ?: -1

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(Dimen.largePadding)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
        )
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            //数据切换
            if (downloadState == -2) {
                IconCompose(
                    data = MainIconType.CHANGE_DATA.icon,
                    tint = MaterialTheme.colors.onSurface,
                    size = Dimen.fabIconSize
                ) {
                    coroutineScope.launch {
                        DatabaseUpdater.changeType()
                    }
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(Dimen.fabIconSize)
                        .padding(Dimen.lineHeight),
                    color = MaterialTheme.colors.onSurface,
                    strokeWidth = Dimen.lineHeight
                )
            }
            Spacer(modifier = Modifier.width(Dimen.largePadding))
            //设置
            IconCompose(
                data = MainIconType.SETTING.icon,
                tint = MaterialTheme.colors.onSurface,
                size = Dimen.fabIconSize
            ) {
                actions.toSetting()
            }
        }

    }
}
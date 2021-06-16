package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.theme.Dimen


/**
 * 顶部工具栏
 *
 */
@ExperimentalAnimationApi
@Composable
fun TopBarCompose(actions: NavActions) {
    val updateApp = MainActivity.noticeViewModel.updateApp.observeAsState().value ?: -1

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding
            )
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //数据切换
            if (updateApp != -1) {
                IconCompose(
                    data = if (updateApp == 1) MainIconType.APP_UPDATE.icon else MainIconType.NOTICE.icon,
                    tint = if (updateApp == 1) colorResource(id = R.color.color_rank_21) else MaterialTheme.colors.onSurface,
                    size = Dimen.fabIconSize
                ) {
                    actions.toNotice()
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
package cn.wthee.pcrtool.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.utils.joinQQGroup
import cn.wthee.pcrtool.utils.openWebView

/**
 * 菜单
 */
@Composable
fun MoreFabCompose(viewModel: NavViewModel) {
    val fabMainIcon = viewModel.fabMainIcon.observeAsState().value ?: MainIconType.OK
    val context = LocalContext.current
    FadeAnimation(visible = fabMainIcon == MainIconType.DOWN) {
        Column(
            modifier = Modifier
                .padding(
                    bottom = Dimen.fabMarginEnd,
                    end = Dimen.fabMargin
                )
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            //反馈交流
            FabCompose(
                iconType = MainIconType.SUPPORT,
                text = stringResource(R.string.qq_group),
                modifier = Modifier.padding(bottom = Dimen.fabSmallMarginEnd),
                hasNavBarPadding = false
            ) {
                joinQQGroup(context)
            }
        }
    }

}

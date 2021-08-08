package cn.wthee.pcrtool.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.compose.FadeAnimation
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.openWebView
import coil.annotation.ExperimentalCoilApi

/**
 * 菜单
 */
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            val issueUrl = stringResource(R.string.issue_url)
            val issueTip = stringResource(R.string.issue_tip)
            //群
            FabCompose(
                iconType = MainIconType.ISSUE,
                text = stringResource(R.string.issue),
                modifier = Modifier.padding(bottom = Dimen.fabSmallMarginEnd),
                defaultPadding = false
            ) {
                openWebView(context, issueUrl, issueTip)
            }
        }
    }

}

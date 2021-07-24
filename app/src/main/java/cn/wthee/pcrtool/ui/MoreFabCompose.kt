package cn.wthee.pcrtool.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.compose.SlideLeftAnimation
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.openWebView
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.launch

/**
 * 菜单
 */
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MoreFabCompose(viewModel: NavViewModel) {
    val fabMainIcon = viewModel.fabMainIcon.observeAsState().value ?: MainIconType.OK
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val color =
        colorResource(id = if (MaterialTheme.colors.isLight) R.color.alpha_white else R.color.alpha_black)
    SlideLeftAnimation(visible = fabMainIcon == MainIconType.DOWN) {
        Column(
            modifier = Modifier
                .background(color = color)
                .clickable {
                    MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
                }
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

            //数据版本切换
            FabCompose(
                iconType = MainIconType.CHANGE_DATA,
                text = stringResource(id = R.string.change_db)
            ) {
                coroutineScope.launch {
                    viewModel.fabMainIcon.postValue(MainIconType.MAIN)
                    DatabaseUpdater.changeType()
                }
            }
        }
    }

}

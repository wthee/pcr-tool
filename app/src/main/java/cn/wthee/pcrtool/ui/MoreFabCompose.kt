package cn.wthee.pcrtool.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.compose.FadeAnimation
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.addToClip
import kotlinx.coroutines.launch

/**
 * 菜单
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MoreFabCompose(viewModel: NavViewModel) {
    val fabMainIcon = viewModel.fabMainIcon.observeAsState().value ?: MainIconType.OK
    val coroutineScope = rememberCoroutineScope()

    FadeAnimation(visible = fabMainIcon == MainIconType.DOWN) {
        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .padding(
                        top = Dimen.mediuPadding,
                        bottom = Dimen.fabMargin,
                        end = Dimen.fabMarginEnd
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val qqGroup = stringResource(R.string.qq_group)
                val tip = stringResource(R.string.copy_qq_tip)
                //群
                FabCompose(
                    iconType = MainIconType.GROUP,
                    text = stringResource(R.string.feedback),
                    modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                ) {
                    addToClip(qqGroup, tip)
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

}

package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

/**
 * fixme 竞技场查询悬浮窗
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun PvpSearchWindow() {
    val activity = ActivityHelper.instance.currentActivity!!
    val viewModel = ViewModelProvider(activity).get(CharacterViewModel::class.java)
    PcrtoolcomposeTheme {
        Row(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(Dimen.mediuPadding)
        ) {
            //窗口状态
            val windowMin = remember {
                mutableStateOf(false)
            }

            Column {
                FabCompose(content = {
                    IconCompose(R.mipmap.ic_logo)
                }) {
                    //最大/小化悬浮窗
                    val min = !windowMin.value
                    windowMin.value = min
                }

            }
            if (!windowMin.value) {
                Card(
                    shape = Shapes.large, modifier = Modifier
                        .padding(start = Dimen.mediuPadding)
                ) {
                    PvpSearchCompose({}, {}, viewModel)
                }
            }
        }
    }
}
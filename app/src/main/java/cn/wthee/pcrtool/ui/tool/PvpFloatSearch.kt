package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.FabMain
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun PvpFloatSearch() {
    val context = LocalContext.current
    val min = MainActivity.navViewModel.floatSearchMin.observeAsState().value ?: false
    val showResult = MainActivity.navViewModel.showResult.observeAsState().value ?: false


    PcrtoolcomposeTheme {
        Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
            Column {
                FloatingActionButton(
                    onClick = {
                        VibrateUtil(context).single()
                        MainActivity.navViewModel.floatSearchMin.postValue(!min)
                    },
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(Dimen.smallPadding)
                        .size(Dimen.fabSize),
                ) {
                    Image(
                        painter = rememberCoilPainter(request = R.mipmap.ic_launcher_foreground),
                        contentDescription = null
                    )
                }
                if (!min) {
                    FabCompose(
                        iconType = MainIconType.FLOAT_CLOSE,
                        modifier = Modifier.padding(Dimen.smallPadding)
                    ) {
                        MainActivity.navViewModel.floatServiceRun.postValue(false)
                    }
                }
                if (!min && !showResult) {
                    FabCompose(
                        iconType = MainIconType.PVP_SEARCH,
                        modifier = Modifier.padding(Dimen.smallPadding)
                    ) {
                        MainActivity.navViewModel.showResult.postValue(true)
                    }
                }
                if (!min && showResult) {
                    FabMain(modifier = Modifier.padding(Dimen.smallPadding))
                }
            }
            if (!min) {
                Card {
                    PvpSearchCompose(
                        floatWindow = true,
                        toCharacter = MainActivity.actions.toCharacterDetail
                    )
                }
            }
        }
    }
}
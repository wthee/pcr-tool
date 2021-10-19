package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.FabMain
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolComposeTheme
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun PvpFloatSearch(pvpViewModel: PvpViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val min = MainActivity.navViewModel.floatSearchMin.observeAsState().value ?: false
    val showResult = MainActivity.navViewModel.showResult.observeAsState().value ?: false
    val pagerState = rememberPagerState()
    val selectListState = rememberScrollState()
    val resultListState = rememberLazyListState()
    val favoritesListState = rememberLazyListState()
    val historyListState = rememberLazyListState()


    PcrtoolComposeTheme {
        Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
            Column {
                FloatingActionButton(
                    onClick = {
                        VibrateUtil(context).single()
                        MainActivity.navViewModel.floatSearchMin.postValue(!min)
                    },
                    shape = CircleShape,
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(Dimen.smallPadding)
                        .size(Dimen.fabSize),
                ) {
                    Image(
                        painter = rememberImagePainter(data = R.mipmap.ic_launcher_foreground),
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
                        coroutineScope.launch {
                            try {
                                resultListState.scrollToItem(0)
                            } catch (ignore: Exception) {

                            }
                        }
                        pvpViewModel.pvpResult.postValue(null)
                        MainActivity.navViewModel.showResult.postValue(true)
                    }
                }
                if (!min && showResult) {
                    FabMain(modifier = Modifier.padding(Dimen.smallPadding))
                }
            }
            val modifier = if (min) {
                Modifier.size(0.dp)
            } else {
                Modifier
            }

            Card(modifier = modifier) {
                PvpSearchCompose(
                    floatWindow = true,
                    pagerState = pagerState,
                    selectListState = selectListState,
                    resultListState = resultListState,
                    favoritesListState = favoritesListState,
                    historyListState = historyListState,
                    toCharacter = MainActivity.actions.toCharacterDetail
                )
            }
        }
    }
}
package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.getIds
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 模拟卡池
 */
@Composable
fun MockGacha() {
    // 类型: 0:单up 1：多up 2：fesup
    val gachaType = remember {
        mutableStateOf(0)
    }
    val coroutineScope = rememberCoroutineScope()
    val openDialog = navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.BACK
    //切换关闭监听
    if (close) {
        navViewModel.openChangeDataDialog.postValue(false)
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.BACK) {
        navViewModel.openChangeDataDialog.postValue(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        UnitList(gachaType.value)
        //卡池类型选择
        SelectGachaTypeCompose(
            gachaType = gachaType,
            openDialog = openDialog,
            coroutineScope = coroutineScope,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
        )

    }
}

/**
 * 卡池类型选择
 */
@Composable
private fun SelectGachaTypeCompose(
    gachaType: MutableState<Int>,
    openDialog: Boolean,
    coroutineScope: CoroutineScope,
    modifier: Modifier
) {
    val context = LocalContext.current
    //类型
    val tabs = arrayListOf(
        stringResource(id = R.string.gacha_pick_up_single),
        stringResource(id = R.string.gacha_pick_up_multi),
        stringResource(id = R.string.gacha_pick_up_fes)
    )
    val sectionColor = MaterialTheme.colorScheme.primary

    SmallFloatingActionButton(
        modifier = modifier
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.fabMarginEnd,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            ),
        containerColor = MaterialTheme.colorScheme.background,
        shape = if (openDialog) androidx.compose.material.MaterialTheme.shapes.medium else CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        onClick = {
            VibrateUtil(context).single()
            if (!openDialog) {
                navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                navViewModel.openChangeDataDialog.postValue(true)
            } else {
                navViewModel.fabCloseClick.postValue(true)
            }
        },
    ) {
        if (openDialog) {
            Column(
                modifier = Modifier.width(Dimen.dataChangeWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //区服选择
                tabs.forEachIndexed { index, tab ->
                    val mModifier = if (gachaType.value == index) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                VibrateUtil(context).single()
                                navViewModel.openChangeDataDialog.postValue(false)
                                navViewModel.fabCloseClick.postValue(true)
                                if (gachaType.value != index) {
                                    coroutineScope.launch {
                                        gachaType.value = index
                                    }
                                }
                            }
                    }
                    SelectText(
                        selected = gachaType.value == index,
                        text = tab,
                        textStyle = MaterialTheme.typography.titleLarge,
                        selectedColor = sectionColor,
                        modifier = mModifier.padding(Dimen.mediumPadding)
                    )
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Dimen.largePadding)
            ) {
                IconCompose(
                    data = MainIconType.MOCK_GACHA.icon,
                    tint = sectionColor,
                    size = Dimen.menuIconSize
                )
                Text(
                    text = tabs[gachaType.value],
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = sectionColor,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.largePadding
                    )
                )
            }

        }
    }
}

/**
 * 角色列表
 */
@Composable
private fun UnitList(gachaType: Int, gachaViewModel: GachaViewModel = hiltViewModel()) {
    val unitList = gachaViewModel.getGachaUnits().collectAsState(initial = null).value

    unitList?.let {
        Column(
            modifier = Modifier
                .padding(Dimen.mediumPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (gachaType == 2) {
                // FES
                val fesLimit = it.fesLimit.getIds()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                ) {
                    MainTitleText(text = "FES 限定")
                    Spacer(modifier = Modifier.weight(1f))
                    MainText(text = fesLimit.size.toString())
                }
                GridIconListCompose(icons = fesLimit, onClickItem = {

                })
            } else {
                // 限定
                val limit = it.limit.getIds()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                ) {
                    MainTitleText(text = "限定★3")
                    Spacer(modifier = Modifier.weight(1f))
                    MainText(text = limit.size.toString())
                }
                GridIconListCompose(icons = limit, onClickItem = {

                })

                // 常驻
                val normal3 = it.normal3.getIds()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                ) {
                    MainTitleText(text = "常驻★3")
                    Spacer(modifier = Modifier.weight(1f))
                    MainText(text = normal3.size.toString())
                }
                GridIconListCompose(icons = normal3, onClickItem = {})
            }

        }
    }
}
package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.MockGachaHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import com.google.accompanist.insets.navigationBarsPadding

/**
 * 模拟卡池
 */
@Composable
fun MockGacha(gachaViewModel: GachaViewModel = hiltViewModel()) {
    val allUnits = gachaViewModel.getGachaUnits().collectAsState(initial = null).value
    // 类型: 0:单up 1：多up 2：fesup
    val gachaType = remember {
        mutableStateOf(0)
    }
    //类型
    val tabs = arrayListOf(
        stringResource(id = R.string.gacha_pick_up_single),
        stringResource(id = R.string.gacha_pick_up_multi),
        stringResource(id = R.string.gacha_pick_up_fes)
    )
    //选中的角色
    val pickUpList = remember {
        mutableStateOf(arrayListOf<GachaUnitInfo>())
    }
    //页面
    val showResult = remember {
        mutableStateOf(false)
    }
    //抽取到的角色
    val resultList = remember {
        mutableStateOf(arrayListOf<GachaUnitInfo>())
    }
    //抽取次数
    val count = remember {
        mutableStateOf(0)
    }

    val close = navViewModel.fabCloseClick.observeAsState().value ?: false

    if (showResult.value) {
        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
    } else {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
    }
    //返回选择
    if (close) {
        showResult.value = false
        navViewModel.fabCloseClick.postValue(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .animateContentSize(defaultSpring())
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Dimen.iconSize),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pickUpList.value.isEmpty()) {
                    MainText(text = "请选择 UP 角色")
                } else {
                    pickUpList.value.forEach {
                        IconCompose(
                            data = ImageResourceHelper.getInstance().getMaxIconUrl(it.unitId),
                            modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                        ) {
                            updatePickUpList(pickUpList, it)
                        }
                    }
                }
            }
            if (showResult.value) {
                //抽卡结果
                MainText(text = "第 ${count.value} 次十连")
                GachaUnitIconListCompose(true, resultList.value) {

                }
            } else {
                //角色选择
                allUnits?.let {
                    UnitList(gachaType.value, it, pickUpList)
                }
            }
        }

        //抽取
        allUnits?.let {
            FabCompose(
                iconType = MainIconType.ALL_IN_GACHA,
                text = "+19",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = Dimen.fabMargin,
                        bottom = Dimen.fabMargin * 2 + Dimen.fabSize
                    )
            ) {
                if (pickUpList.value.isNotEmpty()) {
                    if (!showResult.value) {
                        showResult.value = true
                    }
                    count.value = count.value + 1
                    resultList.value = MockGachaHelper(
                        3.0,
                        pickUpType = gachaType.value,
                        pickUpList = pickUpList.value,
                        allUnits

                    ).giveMe1500Gems()
                } else {
                    ToastUtil.short("请选择 UP 角色")
                }


            }
        }

        //卡池类型选择
        if (!showResult.value) {
            SelectTypeCompose(
                icon = MainIconType.MOCK_GACHA,
                tabs = tabs,
                type = gachaType,
                width = 160.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
            ) {
                //切换时清空
                pickUpList.value = arrayListOf()
            }
        }
    }
}

/**
 * 角色列表
 */
@Composable
private fun UnitList(
    gachaType: Int,
    allUnits: UnitsInGacha,
    pickUpList: MutableState<ArrayList<GachaUnitInfo>>
) {
    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .verticalScroll(rememberScrollState())
    ) {
        if (gachaType == 2) {
            // FES
            GachaUnitGridList(allUnits.fesLimit, "Fes限定★3", pickUpList)
        } else {
            // 限定
            GachaUnitGridList(allUnits.limit, "限定★3", pickUpList)
            // 常驻
            GachaUnitGridList(allUnits.normal3, "常驻★3", pickUpList)
        }
        CommonSpacer()

    }
}

@Composable
private fun GachaUnitGridList(
    data: List<GachaUnitInfo>,
    title: String,
    pickUpList: MutableState<ArrayList<GachaUnitInfo>>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
    ) {
        MainTitleText(text = title)
        Spacer(modifier = Modifier.weight(1f))
        MainText(text = data.size.toString())
    }
    GachaUnitIconListCompose(icons = data, onClickItem = {
        updatePickUpList(pickUpList, it)
    })
}

/**
 * 更新选中列表
 */
private fun updatePickUpList(
    pickUpList: MutableState<ArrayList<GachaUnitInfo>>,
    data: GachaUnitInfo
) {
    val newList = arrayListOf<GachaUnitInfo>()
    newList.addAll(pickUpList.value)
    if (newList.contains(data)) {
        newList.remove(data)
    } else {
        newList.add(data)
    }
    pickUpList.value = newList
}


/**
 * 角色图标列表
 */
@Composable
fun GachaUnitIconListCompose(
    showRarity: Boolean = false,
    icons: List<GachaUnitInfo>,
    onClickItem: (GachaUnitInfo) -> Unit
) {
    VerticalGrid(
        modifier = Modifier.padding(
            top = Dimen.mediumPadding,
            start = Dimen.mediumPadding,
            end = Dimen.mediumPadding
        ),
        maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2,
    ) {
        icons.forEach {
            val iconId = it.unitId + (if (it.rarity == 1) 10 else 30)
            Column(
                modifier = Modifier
                    .padding(
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCompose(
                    data = ImageResourceHelper.getInstance()
                        .getUrl(ImageResourceHelper.ICON_UNIT, iconId)
                ) {
                    onClickItem(it)
                }
                if (showRarity) {
                    MainText(text = "★${it.rarity}")
                }
            }
        }
    }
}
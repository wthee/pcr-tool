package cn.wthee.pcrtool.ui.tool

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getIdStr
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import com.google.gson.JsonArray
import kotlin.math.round


/**
 * 竞技场查询
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun PvpCompose(
    toResult: (String) -> Unit,
    viewModel: CharacterViewModel = hiltNavGraphViewModel()
) {
    //已选择的id
    val navIds = navViewModel.selectedIds.value
    val selectedIds = remember {
        if (navIds != null && navIds.isNotEmpty()) {
            mutableStateListOf(
                navIds[0],
                navIds[1],
                navIds[2],
                navIds[3],
                navIds[4],
            )
        } else {
            mutableStateListOf(
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
            )
        }

    }
    navViewModel.selectedIds.postValue(selectedIds.subList(0, 5))

    val context = LocalContext.current
    viewModel.getAllCharacter()
    val data = viewModel.allPvpCharacterData.observeAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {

        Column {
            //已选择列表
            PvpIconItemList(selectedIds, selectedIds.subList(0, 5), PvpCharacterData(0, -1))
            //供选择列表
            data.value?.let { dataValue ->
                val character1 = dataValue.filter {
                    it.position in 0..299
                }
                val character2 = dataValue.filter {
                    it.position in 300..599
                }
                val character3 = dataValue.filter {
                    it.position in 600..9999
                }
                val spanCount = 5
                val placeholder = PvpCharacterData(0, -1)
                val newList1 =
                    getGridData(
                        spanCount = spanCount,
                        list = character1,
                        placeholder = placeholder
                    )
                val newList2 =
                    getGridData(
                        spanCount = spanCount,
                        list = character2,
                        placeholder = placeholder
                    )
                val newList3 =
                    getGridData(
                        spanCount = spanCount,
                        list = character3,
                        placeholder = placeholder
                    )


                Column(
                    Modifier
                        .padding(top = Dimen.mediuPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    //前卫
                    MainText(text = stringResource(id = R.string.position_1))
                    newList1.forEachIndexed { index, _ ->
                        if (index % spanCount == 0) {
                            PvpIconItemList(
                                selectedIds,
                                newList1.subList(index, index + spanCount),
                                placeholder
                            )
                        }
                    }
                    //中卫
                    MainText(text = stringResource(id = R.string.position_2))
                    newList2.forEachIndexed { index, _ ->
                        if (index % spanCount == 0) {
                            PvpIconItemList(
                                selectedIds,
                                newList2.subList(index, index + spanCount),
                                placeholder
                            )
                        }
                    }
                    //后卫
                    MainText(text = stringResource(id = R.string.position_3))
                    newList3.forEachIndexed { index, _ ->
                        if (index % spanCount == 0) {
                            PvpIconItemList(
                                selectedIds,
                                newList3.subList(index, index + spanCount),
                                placeholder
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimen.sheetMarginBottom))
                }
            }
        }

        //fab
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            //悬浮窗
            ExtendedFabCompose(
                iconType = MainIconType.PVP_SEARCH_WINDOW,
                text = stringResource(id = R.string.pvp_float),
                modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
            ) {

                //检查是否已经授予权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                        context
                    )
                ) {
                    //若未授权则请求权限
                    val intent = Intent(ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:" + context.packageName)
                    context.startActivity(intent)
                } else {
                    //TODO 打开悬浮窗
                }
            }
            //查询
            val tip = stringResource(id = R.string.tip_select_5)
            FabCompose(iconType = MainIconType.PVP_SEARCH) {
                //查询
                if (selectedIds.contains(PvpCharacterData())) {
                    ToastUtil.short(tip)
                } else {
                    toResult(selectedIds.subList(0, 5).getIdStr())
                }
            }
        }

    }

}


@Composable
private fun PvpIconItemList(
    selectedIds: SnapshotStateList<PvpCharacterData>,
    data: List<PvpCharacterData>,
    placeholder: PvpCharacterData
) {

    val tipSelectLimit = stringResource(id = R.string.tip_select_limit)

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimen.smallPadding)
    ) {
        data.forEach {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val alpha = if (it == placeholder) 0f else 1f
                val selected = selectedIds.contains(it)
                val icon = if (it.unitId == 0) {
                    R.drawable.unknown_gray
                } else {
                    CharacterIdUtil.getMaxIconUrl(
                        it.unitId,
                        MainActivity.r6Ids.contains(it.unitId)
                    )
                }
                //图标
                IconCompose(
                    data = icon,
                    modifier = Modifier.alpha(alpha)
                ) {
                    //点击选择或取消选择
                    if (selected) {
                        var cancelSelectIndex = 0
                        selectedIds.forEachIndexed { index, sel ->
                            if (it.position == sel.position) {
                                cancelSelectIndex = index
                            }
                        }
                        selectedIds[cancelSelectIndex] = PvpCharacterData()
                    } else {
                        val unSelected = selectedIds.find { it.position == 999 }
                        if (unSelected == null) {
                            //选完了
                            ToastUtil.short(tipSelectLimit)
                        } else {
                            //可以选择
                            selectedIds[0] = it
                        }
                    }
                    selectedIds.sortByDescending { it.position }
                }
                //位置
                val text =
                    if (it != PvpCharacterData()) it.position.toString() else stringResource(id = R.string.unselect)

                Text(
                    text = text,
                    color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    fontWeight = if (selected) FontWeight.Black else FontWeight.Light,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .alpha(alpha)
                        .padding(top = Dimen.smallPadding)
                )
            }
        }
    }
}

/**
 * 查询结果页面
 */
@Composable
fun PvpSearchResult(
    idString: String,
    viewModel: PvpViewModel = hiltNavGraphViewModel()
) {
    val ids = JsonArray()
    for (id in idString.split("-")) {
        if (id.isNotBlank()) {
            ids.add(id)
        }
    }
    viewModel.getPVPData(ids)

    val result = viewModel.pvpResult.observeAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (result.value == null || result.value!!.data == null) {
            CircularProgressIndicator(modifier = Modifier.size(Dimen.iconSize))
        } else {
            //展示查询结果
            val list = result.value!!.data!!.sortedByDescending { it.up }
            MainText(text = stringResource(id = R.string.team_atk))
            LazyColumn {
                itemsIndexed(items = list) { index, item ->
                    PvpAtkTeam(index + 1, item)
                }
            }
        }
    }

}

/**
 * 查询结果
 */
@Composable
private fun PvpAtkTeam(i: Int, item: PvpResultData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            MainTitleText(text = stringResource(id = R.string.team_no, i.toString().fillZero()))
            val upRatio = if (item.up == 0) 0 else {
                round(item.up * 1.0 / (item.up + item.down) * 100).toInt()
            }
            MainContentText(
                text = "${upRatio}%",
                color = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth(0.2f)
            )
            MainContentText(
                text = item.up.toString(),
                color = colorResource(id = R.color.cool_apk),
                modifier = Modifier.fillMaxWidth(0.2f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            item.getAtkIdList().forEach {
                IconCompose(
                    data = CharacterIdUtil.getMaxIconUrl(
                        it,
                        MainActivity.r6Ids.contains(it)
                    )
                )
            }
        }
    }
}

package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getIdStr
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.gson.JsonArray
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


/**
 * 查询结果页面
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun PvpSearchResult(
    resultListState: LazyListState,
    selectedIds: List<PvpCharacterData>,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel = hiltViewModel()
) {

    val defIds = selectedIds.subList(0, 5).getIdStr()
    //展示搜索结果
    val idArray = JsonArray()
    for (sel in selectedIds.subList(0, 5)) {
        idArray.add(sel.unitId)
    }
    val result = pvpViewModel.pvpResult.observeAsState().value
    val placeholder = result == null
    pvpViewModel.getPVPData(idArray)
    val region = getRegion()
    pvpViewModel.getFavoritesList(defIds, region)
    //收藏信息
    val favorites = pvpViewModel.favorites.observeAsState()
    val favoritesList = arrayListOf<String>()
    //获取数据
    LaunchedEffect(null) {
        //添加搜索记录
        var unSplitDefIds = ""
        for (sel in selectedIds.subList(0, 5)) {
            idArray.add(sel.unitId)
            unSplitDefIds += "${sel.unitId}-"
        }
        pvpViewModel.insert(
            PvpHistoryData(
                "$region@$unSplitDefIds",
                getToday(),
            )
        )
    }
    val context = LocalContext.current
    val vibrated = remember {
        mutableStateOf(false)
    }
    //宽度
    val itemWidth = getItemWidth(floatWindow)

    if (favorites.value != null) {
        SideEffect {
            favorites.value?.let {
                it.forEach { data ->
                    favoritesList.add(data.atks)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (!placeholder) {
                if (result!!.message == "success") {
                    //振动提醒
                    if (!vibrated.value) {
                        vibrated.value = true
                        VibrateUtil(context).done()
                    }
                    val hasData = result.data!!.isNotEmpty()
                    if (hasData) {
                        //查询成功
                        val list = result.data!!.sortedByDescending { it.up }
                        FadeAnimation(visible = hasData) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                //展示查询结果
                                LazyVerticalGrid(
                                    state = resultListState,
                                    cells = GridCells.Adaptive(itemWidth)
                                ) {
                                    itemsIndexed(items = list) { index, item ->
                                        PvpResultItem(
                                            favoritesList,
                                            index + 1,
                                            item,
                                            region,
                                            floatWindow,
                                            pvpViewModel
                                        )
                                    }
                                    item {
                                        CommonSpacer()
                                    }
                                }
                            }
                        }
                    } else {
                        MainText(
                            text = stringResource(id = R.string.pvp_no_data),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = Dimen.largePadding)
                    ) {
                        MainText(
                            text = stringResource(id = R.string.data_get_error)
                        )
                        SubButton(
                            text = "重新查询",
                            modifier = Modifier.padding(top = Dimen.mediumPadding)
                        ) {
                            pvpViewModel.pvpResult.postValue(null)
                            pvpViewModel.getPVPData(idArray)
                        }
                    }

                }
            } else {
                //占位图
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    //展示查询结果
                    LazyVerticalGrid(
                        state = resultListState,
                        cells = GridCells.Adaptive(itemWidth)
                    ) {
                        items(10) {
                            PvpResultItem(
                                favoritesList,
                                0,
                                PvpResultData(),
                                region,
                                floatWindow,
                                pvpViewModel
                            )
                        }
                        item {
                            CommonSpacer()
                        }
                    }
                }
            }

        }
    }

}

/**
 * 查询结果 Item
 */
@ExperimentalMaterialApi
@Composable
private fun PvpResultItem(
    favoritesList: List<String>,
    i: Int,
    item: PvpResultData,
    region: Int,
    floatWindow: Boolean,
    viewModel: PvpViewModel
) {
    val placeholder = item.id == ""
    val scope = rememberCoroutineScope()
    val favorites = remember {
        mutableStateOf(favoritesList.contains(item.atk))
    }

    val largerPadding = if (floatWindow) Dimen.mediumPadding else Dimen.largePadding
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding

    Column(
        modifier = Modifier.padding(
            horizontal = largerPadding,
            vertical = mediumPadding
        )
    ) {
        Row {
            MainTitleText(
                text = stringResource(id = R.string.team_no, i.toString().fillZero()),
                modifier = Modifier
                    .padding(bottom = mediumPadding)
                    .placeholder(visible = placeholder, highlight = PlaceholderHighlight.shimmer())
            )
            Spacer(modifier = Modifier.weight(1f))
            //收藏
            if (!placeholder) {
                IconCompose(
                    data = if (favorites.value) MainIconType.LOVE_FILL.icon else MainIconType.LOVE_LINE.icon,
                    size = Dimen.fabIconSize
                ) {
                    if (!placeholder) {
                        scope.launch {
                            if (favorites.value) {
                                //已收藏，取消收藏
                                viewModel.delete(item.atk, item.def, region)
                            } else {
                                //未收藏，添加收藏
                                val simpleDateFormat =
                                    SimpleDateFormat(
                                        "yyyy/MM/dd HH:mm:ss.SSS",
                                        Locale.CHINESE
                                    )
                                val date = Date(System.currentTimeMillis())
                                viewModel.insert(
                                    PvpFavoriteData(
                                        item.id,
                                        item.atk,
                                        item.def,
                                        simpleDateFormat.format(date),
                                        region
                                    )
                                )
                            }
                            favorites.value = !favorites.value
                        }
                    }
                }
            }
        }

        MainCard(
            modifier = Modifier.placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            )
        ) {
            val upRatio = if (item.up == 0) 0 else {
                round(item.up * 1.0 / (item.up + item.down) * 100).toInt()
            }
            Column {
                //点赞信息
                Row(
                    modifier = Modifier.padding(horizontal = mediumPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainContentText(
                        text = "${upRatio}%",
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(0.3f)
                    )
                    MainContentText(
                        text = item.up.toString(),
                        color = colorResource(id = R.color.color_rank_21),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(0.3f)
                    )
                    MainContentText(
                        text = item.down.toString(),
                        color = colorResource(id = R.color.color_rank_18_20),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(if (floatWindow) 0.3f else 1f)
                    )
                }
                //队伍角色图标
                //进攻
                Row(
                    modifier = Modifier
                        .padding(bottom = mediumPadding)
                ) {
                    item.getIdList(0).forEachIndexed { _, it ->
                        Box(
                            modifier = Modifier.padding(mediumPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            IconCompose(
                                data = ImageResourceHelper.getInstance().getMaxIconUrl(
                                    it,
                                    r6Ids.contains(it)
                                ),
                                size = if (floatWindow) Dimen.mediumIconSize else Dimen.iconSize
                            )
                        }
                    }
                }
            }
        }

    }
}


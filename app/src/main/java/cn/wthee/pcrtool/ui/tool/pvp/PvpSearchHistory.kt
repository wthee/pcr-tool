package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import kotlinx.coroutines.launch
import java.util.*


/**
 * 搜索历史
 */
@ExperimentalMaterialApi
@Composable
fun PvpSearchHistory(
    historyListState: LazyListState,
    toCharacter: (Int) -> Unit,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel
) {
    val region = getRegion()
    pvpViewModel.getHistory(region)
    val list = pvpViewModel.history.observeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (list.value != null && list.value!!.isNotEmpty()) {
            LazyColumn(
                state = historyListState,
                contentPadding = PaddingValues(Dimen.mediumPadding)
            ) {
                items(list.value!!) { data ->
                    PvpHistoryItem(
                        toCharacter,
                        data,
                        floatWindow,
                        pvpViewModel
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            MainText(
                text = stringResource(id = R.string.pvp_no_history),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * 搜索历史项
 */
@ExperimentalMaterialApi
@Composable
private fun PvpHistoryItem(
    toCharacter: (Int) -> Unit,
    itemData: PvpHistoryData,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel,
    characterViewModel: CharacterViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding

    MainCard(modifier = Modifier.padding((mediumPadding))) {
        //队伍角色图标
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = mediumPadding, bottom = mediumPadding)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = mediumPadding, end = mediumPadding)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //搜索
                TextButton(
                    contentPadding = PaddingValues(
                        top = if (floatWindow) 0.dp else Dimen.mediumPadding,
                        bottom = if (floatWindow) 0.dp else Dimen.mediumPadding,
                        start = Dimen.smallPadding,
                        end = Dimen.smallPadding
                    ),
                    onClick = {
                        //重置页面
                        scope.launch {
                            pvpViewModel.pvpResult.postValue(null)
                            val selectedData =
                                characterViewModel.getPvpCharacterByIds(itemData.getDefIds())
                            val selectedIds = selectedData as ArrayList<PvpCharacterData>?
                            selectedIds?.sortByDescending { it.position }
                            MainActivity.navViewModel.selectedPvpData.postValue(selectedIds)
                            MainActivity.navViewModel.showResult.postValue(true)
                        }
                        VibrateUtil(context).single()
                    }) {
                    IconCompose(
                        data = MainIconType.PVP_SEARCH.icon,
                        size = Dimen.fabIconSize
                    )
                    if (!floatWindow) {
                        MainContentText(text = stringResource(id = R.string.pvp_research))
                    }
                }
                //日期
                CaptionText(
                    text = itemData.date,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = mediumPadding, end = mediumPadding),
                    textAlign = TextAlign.End
                )
            }

            //防守
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val modifier = if (!floatWindow) {
                    Modifier
                        .weight(1f)
                        .padding(Dimen.smallPadding)
                } else {
                    Modifier
                        .weight(1f)
                        .padding(start = Dimen.smallPadding, end = Dimen.smallPadding)
                }
                itemData.getDefIds().forEachIndexed { _, it ->
                    Box(
                        modifier = modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        IconCompose(
                            data = CharacterIdUtil.getMaxIconUrl(
                                it,
                                MainActivity.r6Ids.contains(it)
                            )
                        ) {
                            toCharacter(it)
                        }
                    }
                }
            }
        }
    }
}
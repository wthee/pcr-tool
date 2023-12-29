package cn.wthee.pcrtool.ui.equip.drop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.QuestDetail
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdds
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.tool.quest.AreaItem
import cn.wthee.pcrtool.ui.tool.quest.QuestPager
import kotlinx.coroutines.launch


/**
 * 装备素材掉落信息
 */
@Composable
fun EquipMaterialDropInfoScreen(
    equipMaterialDropInfoViewModel: EquipMaterialDropInfoViewModel = hiltViewModel(),
) {
    val uiState by equipMaterialDropInfoViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()


    MainScaffold(
        fab = {
            //装备素材收藏
            MainSmallFab(
                iconType = if (uiState.loved) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                text = if (uiState.loved) "" else stringResource(id = R.string.love_equip_material)
            ) {
                scope.launch {
                    equipMaterialDropInfoViewModel.updateStarId()
                }
            }
        }
    ) {
        EquipMaterialDropInfoContent(
            equipId = uiState.equipId,
            equipName = uiState.equipName,
            dropQuestList = uiState.dropQuestList,
            loved = uiState.loved,
            randomDropList = uiState.randomDropList,
            randomDropLoadingState = uiState.randomDropLoadingState,
        )
    }

}

@Composable
private fun EquipMaterialDropInfoContent(
    equipId: Int?,
    equipName: String,
    dropQuestList: List<QuestDetail>?,
    loved: Boolean,
    randomDropList: List<RandomEquipDropArea>?,
    randomDropLoadingState: LoadingState
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //基本信息
        MainText(
            text = equipName,
            modifier = Modifier
                .padding(top = Dimen.largePadding),
            color = if (loved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            selectable = true
        )
        //掉落信息
        dropQuestList?.let { questList ->
            if (questList.isNotEmpty()) {
                equipId?.let {
                    QuestPager(
                        questList = questList,
                        equipId = it,
                        randomDropList = randomDropList,
                        loadingState = randomDropLoadingState
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    MainText(text = stringResource(id = R.string.tip_no_equip_get_area))
                }
            }
        }
        if (dropQuestList == null) {
            //加载中
            val odds = arrayListOf<EquipmentIdWithOdds>()
            for (i in 0..9) {
                odds.add(EquipmentIdWithOdds())
            }
            LazyColumn {
                items(odds.size) {
                    AreaItem(
                        selectedId = -1,
                        odds = odds,
                        num = "30-15",
                        color = colorWhite
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        }
    }
}

/**
 * fixme 预览
 * @see [QuestPager]
 */
@CombinedPreviews
@Composable
private fun EquipMaterialDropInfoContentPreview() {
    PreviewLayout {
        EquipMaterialDropInfoContent(
            equipId = 1,
            equipName = stringResource(id = R.string.debug_long_text),
            dropQuestList = arrayListOf(QuestDetail(questId = 1)),
            loved = true,
            randomDropList = null,
            randomDropLoadingState = LoadingState.Success
        )
    }
}

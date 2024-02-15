package cn.wthee.pcrtool.ui.equip.drop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.CenterTipText
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
import cn.wthee.pcrtool.ui.tool.quest.QuestPagerPreview
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
                iconType = if (uiState.favorite) MainIconType.FAVORITE_FILL else MainIconType.FAVORITE_LINE,
                text = if (uiState.favorite) "" else stringResource(id = R.string.favorite_equip_material),
                onClick = {
                    scope.launch {
                        equipMaterialDropInfoViewModel.updateFavoriteId()
                    }
                }
            )
        }
    ) {
        EquipMaterialDropInfoContent(
            equipId = uiState.equipId,
            equipName = uiState.equipName,
            dropQuestList = uiState.dropQuestList,
            favorite = uiState.favorite,
            randomDropList = uiState.randomDropList,
            randomDropLoadState = uiState.randomDropLoadState,
        )
    }

}

@Composable
private fun EquipMaterialDropInfoContent(
    equipId: Int?,
    equipName: String,
    dropQuestList: List<QuestDetail>?,
    favorite: Boolean,
    randomDropList: List<RandomEquipDropArea>?,
    randomDropLoadState: LoadState
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //基本信息
        MainText(
            text = equipName,
            modifier = Modifier
                .padding(top = Dimen.largePadding),
            color = if (favorite) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            selectable = true
        )
        //掉落信息
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
        } else {
            if (dropQuestList.isNotEmpty()) {
                equipId?.let {
                    QuestPager(
                        questList = dropQuestList,
                        equipId = it,
                        randomDropList = randomDropList,
                        loadState = randomDropLoadState
                    )
                }
            } else {
                CenterTipText(text = stringResource(id = R.string.tip_no_equip_get_area))
            }
        }
    }
}

/**
 * @see [QuestPager]
 */
@CombinedPreviews
@Composable
private fun EquipMaterialDropInfoContentPreview() {
    PreviewLayout {
        EquipMaterialDropInfoContent(
            equipId = null,
            equipName = stringResource(id = R.string.debug_short_text),
            dropQuestList = arrayListOf(QuestDetail(questId = 1)),
            favorite = true,
            randomDropList = null,
            randomDropLoadState = LoadState.Error
        )
        QuestPagerPreview()
    }
}

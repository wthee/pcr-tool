package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.AttrValueType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.ui.components.AttrCompare
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.RankRangePickerCompose
import cn.wthee.pcrtool.ui.components.RankText
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel


/**
 * 角色 RANK 对比
 *
 * @param unitId 角色编号
 * @param maxRank 角色rank最大值
 * @param level 角色等级
 * @param rarity 角色星级
 * @param uniqueEquipLevel 角色专武等级
 * @param uniqueEquipLevel2 角色专武2等级
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RankCompare(
    unitId: Int,
    maxRank: Int,
    level: Int,
    rarity: Int,
    uniqueEquipLevel: Int,
    uniqueEquipLevel2: Int,
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
) {
    val rank0 = remember {
        mutableIntStateOf(maxRank)
    }
    val rank1 = remember {
        mutableIntStateOf(maxRank)
    }

    val attrCompareDataFlow = remember(
        unitId,
        level,
        rarity,
        uniqueEquipLevel,
        uniqueEquipLevel2,
        rank0.intValue,
        rank1.intValue
    ) {
        attrViewModel.getUnitAttrCompare(
            unitId,
            level,
            rarity,
            uniqueEquipLevel,
            uniqueEquipLevel2,
            rank0.intValue,
            rank1.intValue
        )
    }
    val attrCompareData by attrCompareDataFlow.collectAsState(initial = arrayListOf())


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Dimen.largePadding)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(
                    vertical = Dimen.mediumPadding,
                    horizontal = Dimen.mediumPadding + Dimen.smallPadding
                )
            ) {
                Spacer(modifier = Modifier.weight(0.3f))
                RankText(
                    rank = rank0.intValue,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(0.dp)
                )
                RankText(
                    rank = rank1.intValue,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.2f)
                )
                Subtitle1(
                    text = stringResource(id = R.string.result),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.2f),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            AttrCompare(attrCompareData, isExtraEquip = false, attrValueType = AttrValueType.INT)
        }

        MainSmallFab(
            iconType = MainIconType.RANK_COMPARE,
            text = stringResource(id = R.string.rank_compare),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {

        }

        //RANK 选择
        RankRangePickerCompose(
            rank0,
            rank1,
            maxRank
        )
    }
}


@CombinedPreviews
@Composable
private fun AttrComparePreview() {
    val data = listOf(AttrCompareData(), AttrCompareData(), AttrCompareData())
    PreviewLayout {
        AttrCompare(compareData = data, isExtraEquip = false, attrValueType = AttrValueType.INT)
    }
}
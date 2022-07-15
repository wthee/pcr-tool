package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.RankCompareData
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel


/**
 * 角色 RANK 对比
 *
 * @param unitId 角色编号
 * @param maxRank 角色rank最大值
 * @param currentValue 角色属性
 */
@OptIn(ExperimentalMaterialApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RankCompare(
    unitId: Int,
    maxRank: Int,
    currentValue: CharacterProperty,
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
) {
    val rank0 = remember {
        mutableStateOf(maxRank)
    }
    val rank1 = remember {
        mutableStateOf(maxRank)
    }

    val attrCompareData = attrViewModel.getUnitAttrCompare(
        unitId,
        currentValue.level,
        currentValue.rarity,
        currentValue.uniqueEquipmentLevel,
        rank0.value,
        rank1.value
    ).collectAsState(initial = arrayListOf()).value

    val dialogState = remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Dimen.largePadding)
    ) {
        Column {
            Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
                Spacer(modifier = Modifier.weight(0.3f))
                RankText(
                    rank = rank0.value,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(0.dp)
                )
                RankText(
                    rank = rank1.value,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = stringResource(id = R.string.result),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(0.2f),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            AttrCompare(attrCompareData)
        }

        FabCompose(
            iconType = MainIconType.RANK_SELECT,
            text = stringResource(id = R.string.rank_select),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            dialogState.value = true
        }

        //RANK 选择
        if (dialogState.value) {
            RankSelectCompose(
                rank0,
                rank1,
                maxRank,
                dialogState,
            )
        }
    }
}

/**
 * 属性对比
 */
@Composable
fun AttrCompare(compareData: List<RankCompareData>) {
    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        compareData.forEach {
            Row(modifier = Modifier.padding(Dimen.smallPadding)) {
                MainTitleText(
                    text = it.title,
                    modifier = Modifier.weight(0.3f)
                )
                MainContentText(
                    text = it.attr0.int.toString(),
                    modifier = Modifier.weight(0.2f)
                )
                MainContentText(
                    text = it.attr1.int.toString(),
                    modifier = Modifier.weight(0.2f)
                )
                val color = when {
                    it.attrCompare.int > 0 -> colorGreen
                    it.attrCompare.int < 0 -> colorRed
                    else -> MaterialTheme.colorScheme.onSurface
                }
                Text(
                    text = it.attrCompare.int.toString(),
                    color = color,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }
        CommonSpacer()
    }
}


@Preview
@Composable
private fun Preview() {
    val data = listOf(RankCompareData(), RankCompareData(), RankCompareData())
    PreviewBox {
        AttrCompare(compareData = data)
    }
}
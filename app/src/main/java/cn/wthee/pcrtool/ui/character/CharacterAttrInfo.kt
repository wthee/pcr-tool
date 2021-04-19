package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.all
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.ui.compose.AttrList
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.getEquipIconUrl
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.getRankColor
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 角色属性
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CharacterAttrInfo(
    unitId: Int,
    r6Id: Int,
    characterViewModel: CharacterViewModel = hiltNavGraphViewModel(),
    characterAttrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel(),
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel(),
    toEquipDetail: (Int) -> Unit
) {
    characterAttrViewModel.getMaxRankAndRarity(unitId)

    val selectInfo = characterAttrViewModel.selData.observeAsState().value
    val maxData = characterAttrViewModel.maxData.observeAsState().value
    val attrData = characterAttrViewModel.sumInfo.observeAsState().value
    val storyAttr = characterAttrViewModel.storyAttrs.observeAsState().value
    val equips = characterAttrViewModel.equipments.observeAsState().value

    maxData?.let {
        characterAttrViewModel.selData.postValue(it)
        characterAttrViewModel.getCharacterInfo(unitId, it)
    }

    Card(shape = CardTopShape, elevation = 0.dp) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.smallPadding)
        ) {
            //等级
            Text(
                text = selectInfo?.level.toString() ?: "",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            //属性
            attrData?.let { AttrList(attrs = it.all()) }
            Text(
                text = stringResource(id = R.string.title_story_attr),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.largePadding, bottom = Dimen.smallPadding)
            )
            //剧情属性
            storyAttr?.let { AttrList(attrs = storyAttr.allNotZero()) }
            //RANK 装备
            if (equips != null && equips.isNotEmpty()) {
                CharacterEquip(rank = selectInfo?.rank ?: 2, equips = equips, toEquipDetail)
            }
        }
    }
}

/**
 * 角色 RANK 装备
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CharacterEquip(
    rank: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit
) {

    Column {
        //装备 6、 3
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.largePadding)
        ) {
            val id6 = equips[0].equipmentId
            val id3 = equips[3].equipmentId
            IconCompose(
                data = getEquipIconUrl(id6),
                modifier = Modifier
                    .size(Dimen.iconSize)
                    .clickable {
                        toEquipDetail(id6)
                    }
            )
            IconCompose(
                data = getEquipIconUrl(id3),
                modifier = Modifier
                    .size(Dimen.iconSize)
                    .clickable {
                        toEquipDetail(id3)
                    }
            )
        }
        //装备 5、 2
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.mediuPadding)
        ) {
            val id5 = equips[1].equipmentId
            IconCompose(
                data = getEquipIconUrl(id5),
                modifier = Modifier
                    .size(Dimen.iconSize)
                    .clickable {
                        toEquipDetail(id5)
                    }
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //TODO RANK 选择
                TextButton(onClick = { /*TODO*/ }) {
                    Text(
                        text = getFormatText(rank),
                        color = colorResource(id = getRankColor(rank)),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                TextButton(onClick = { /*TODO*/ }) {
                    Text(
                        text = stringResource(id = R.string.rank_compare),
                        modifier = Modifier.padding(top = Dimen.mediuPadding),
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            }
            val id2 = equips[4].equipmentId
            IconCompose(
                data = getEquipIconUrl(id2),
                modifier = Modifier
                    .size(Dimen.iconSize)
                    .clickable {
                        toEquipDetail(id2)
                    }
            )
        }
        //装备 4、 1
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.mediuPadding)
        ) {
            val id4 = equips[2].equipmentId
            val id1 = equips[5].equipmentId
            IconCompose(
                data = getEquipIconUrl(id4),
                modifier = Modifier
                    .size(Dimen.iconSize)
                    .clickable {
                        toEquipDetail(id4)
                    }
            )
            IconCompose(
                data = getEquipIconUrl(id1),
                modifier = Modifier
                    .size(Dimen.iconSize)
                    .clickable {
                        toEquipDetail(id1)
                    }
            )
        }
    }
}
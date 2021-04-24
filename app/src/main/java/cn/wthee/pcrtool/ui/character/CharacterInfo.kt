package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.*
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
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CharacterInfo(
    unitId: Int,
    r6Id: Int,
    toEquipDetail: (Int) -> Unit,
    toCharacterBasicInfo: (Int) -> Unit,
    characterAttrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel(),
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

    val cardHeight = (ScreenUtil.getWidth() / Constants.RATIO).px2dp - 10
    var id = unitId
    id += if (r6Id != 0) 60 else 30
    val scrollState = rememberScrollState()
    val marginTop = if (scrollState.value < 0)
        cardHeight
    else if (cardHeight - scrollState.value < 0)
        0
    else
        cardHeight - scrollState.value

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            //图片
            CharacterCard(
                Constants.CHARACTER_FULL_URL + id + Constants.WEBP,
                scrollState = scrollState
            )
            //页面
            Card(
                shape = CardTopShape,
                elevation = 0.dp,
                modifier = Modifier.padding(top = marginTop.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(Dimen.mediuPadding)
                        .fillMaxSize()
                        .background(color = MaterialTheme.colors.onPrimary)
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
                    MainText(
                        text = stringResource(id = R.string.title_story_attr),
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
                    //显示专武
                    UniqueEquip(unitId, selectInfo?.uniqueEquipLevel ?: 1)
                    //技能
                    CharacterSkill(id = unitId)
                }
            }
        }
        FabCompose(
            R.drawable.ic_drop,
            Modifier
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                .align(Alignment.BottomEnd)
        ) {
            toCharacterBasicInfo(unitId)
        }
    }

}

/**
 * 角色 RANK 装备
 */
@Composable
private fun CharacterEquip(
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
                        if (id6 != Constants.UNKNOWN_EQUIP_ID) {
                            toEquipDetail(id6)
                        }
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
                        text = stringResource(id = cn.wthee.pcrtool.R.string.rank_compare),
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

/**
 * v专武信息
 */
@Composable
private fun UniqueEquip(
    unitId: Int,
    level: Int,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
) {
    equipmentViewModel.getUniqueEquipInfos(unitId, level)
    val data = equipmentViewModel.uniqueEquip.observeAsState().value

    data?.let {
        Column(
            modifier = Modifier
                .padding(top = Dimen.largePadding)
        ) {
            MainText(
                text = it.equipmentName,
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconCompose(
                    data = getEquipIconUrl(it.equipmentId),
                    modifier = Modifier.size(Dimen.iconSize)
                )
                Text(
                    text = it.getDesc(),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(start = Dimen.mediuPadding)
                )
            }
            //属性
            AttrList(attrs = it.attr.allNotZero())
        }
    }

}
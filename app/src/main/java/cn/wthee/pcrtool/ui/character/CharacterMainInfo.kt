package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.view.all
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

/**
 * 角色信息
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CharacterMainInfo(
    unitId: Int,
    r6Id: Int,
    toEquipDetail: (Int) -> Unit,
    toCharacterBasicInfo: (Int) -> Unit,
    toRankEquip: (Int) -> Unit,
    toRankCompare: (Int, Int, Int, Int, Int) -> Unit,
    toEquipCount: (Int, Int) -> Unit,
    navViewModel: NavViewModel,
    attrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel(),
) {
    attrViewModel.isUnknown(unitId)
    //是否已登场
    val unknown = attrViewModel.isUnknown.observeAsState().value ?: false
    //选择的 RANK
    val selectRank = navViewModel.selectRank.observeAsState().value ?: 0
    //最大值
    val allData = attrViewModel.allAttr.observeAsState()
    val levelMax = remember {
        mutableStateOf(0)
    }
    val rankMax = remember {
        mutableStateOf(0)
    }
    val rarityMax = remember {
        mutableStateOf(0)
    }
    val uniqueEquipLevelMax = remember {
        mutableStateOf(0)
    }
    val level = attrViewModel.levelValue.observeAsState()
    val rank = attrViewModel.rankValue.observeAsState()
    val rarity = attrViewModel.rarityValue.observeAsState()
    val uniqueEquipLevel = attrViewModel.uniqueEquipLevelValue.observeAsState()

    //滑动条
    val sliderLevel = remember {
        mutableStateOf(0)
    }
    val sliderUniqueEquipLevel = remember {
        mutableStateOf(0)
    }
    if (selectRank != 0) {
        attrViewModel.rankValue.postValue(selectRank)
    }
    if (level.value != null && rank.value != 0 && rarity.value != null && uniqueEquipLevel.value != null) {
        attrViewModel.getCharacterInfo(
            unitId, level.value!!, rank.value!!, rarity.value!!, uniqueEquipLevel.value!!
        )
    }

    val cardHeight = (ScreenUtil.getWidth() / Constants.RATIO).px2dp - 10
    var id = unitId
    id += if (r6Id != 0) 60 else 30

    val scrollState = rememberScrollState()
    val marginTop = when {
        scrollState.value < 0 -> cardHeight
        cardHeight - scrollState.value < 0 -> 0
        else -> cardHeight - scrollState.value
    }
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
        navViewModel.fabClose.postValue(false)
    }

    //页面
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            SkillLoopList(unitId = unitId)
        }
    ) {
        //获取最大数据,初始化加载
        coroutineScope.launch {
            val maxData = attrViewModel.getMaxRankAndRarity(unitId)
            val maxValueNotInit =
                levelMax.value == 0 && rankMax.value == 0 && rarityMax.value == 0 && uniqueEquipLevelMax.value == 0
            val valueNotInit =
                level.value == null && rarity.value == null && uniqueEquipLevel.value == null
            if (maxData.isNotEmpty() && maxValueNotInit) {
                levelMax.value = maxData[0]
                rankMax.value = maxData[1]
                rarityMax.value = maxData[2]
                uniqueEquipLevelMax.value = maxData[3]
                sliderLevel.value = maxData[0]
                sliderUniqueEquipLevel.value = maxData[3]
            }
            if (maxData.isNotEmpty() && valueNotInit) {
                attrViewModel.levelValue.postValue(maxData[0])
                attrViewModel.rankValue.postValue(maxData[1])
                attrViewModel.rarityValue.postValue(maxData[2])
                attrViewModel.uniqueEquipLevelValue.postValue(maxData[3])
            }
            if (maxData.isNotEmpty() && selectRank == 0) {
                navViewModel.selectRank.postValue(maxData[1])
            }
        }

        //关闭监听
        val close = navViewModel.fabClose.observeAsState().value ?: false
        if (close) {
            coroutineScope.launch {
                state.hide()
            }
            navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
            navViewModel.fabClose.postValue(false)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.onPrimary)
        ) {
            Box(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
            ) {
                if (levelMax.value != 0 && allData.value != null && allData.value!!.equips.isNotEmpty()) {
                    Box(modifier = Modifier.clickable {
                        //TODO 跳转角色图片列表
                    }) {
                        //图片
                        CharacterCard(
                            Constants.CHARACTER_FULL_URL + id + Constants.WEBP,
                            scrollState = scrollState
                        )
                        //星级
                        StarSelect(
                            rarityMax.value,
                            rarity.value!!,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = Dimen.largePadding)
                        )
                    }
                    //页面
                    Card(
                        shape = CardTopShape,
                        elevation = 20.dp,
                        modifier = Modifier.padding(top = marginTop.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(Dimen.mediuPadding)
                                .fillMaxSize()
                        ) {
                            //等级
                            Text(
                                text = sliderLevel.value.toString(),
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Slider(
                                value = sliderLevel.value.toFloat(),
                                onValueChange = { sliderLevel.value = it.toInt() },
                                onValueChangeFinished = {
                                    if (sliderLevel.value != 0) {
                                        attrViewModel.levelValue.postValue(sliderLevel.value)
                                    }
                                },
                                valueRange = 1f..levelMax.value.toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth(0.618f)
                                    .align(Alignment.CenterHorizontally)
                            )
                            //属性
                            AttrList(attrs = allData.value!!.sumAttr.all())
                            MainText(
                                text = stringResource(id = R.string.title_story_attr),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = Dimen.largePadding, bottom = Dimen.smallPadding)
                            )
                            //剧情属性
                            AttrList(attrs = allData.value!!.stroyAttr.allNotZero())
                            //RANK 装备
                            CharacterEquip(
                                unitId,
                                rankMax.value,
                                level.value!!,
                                rank.value!!,
                                rarity.value!!,
                                uniqueEquipLevel.value!!,
                                allData.value!!.equips,
                                toEquipDetail,
                                toRankEquip,
                                toRankCompare,
                                toEquipCount,
                            )
                            //显示专武
                            if (allData.value!!.uniqueEquip.equipmentId != Constants.UNKNOWN_EQUIP_ID) {
                                UniqueEquip(
                                    uniqueEquipLevelMax.value,
                                    uniqueEquipLevel.value!!,
                                    sliderUniqueEquipLevel,
                                    allData.value!!.uniqueEquip
                                )
                            }
                            //技能
                            CharacterSkill(
                                id = unitId,
                                level = level.value!!,
                                atk = allData.value!!.sumAttr.atk.int
                            )
                        }
                    }
                } else {
                    //未知角色占位页面
                    if (unknown) {
                        //等级
                        Text(
                            text = stringResource(R.string.unknown_character),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
            ) {
                //技能循环
                FabCompose(
                    modifier = Modifier
                        .padding(end = Dimen.fabSmallMarginEnd),
                    iconId = R.drawable.ic_loop,
                ) {
                    coroutineScope.launch {
                        if (state.isVisible) {
                            navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
                            state.hide()
                        } else {
                            navViewModel.fabMainIcon.postValue(R.drawable.ic_cancel)
                            state.show()
                        }
                    }
                }
                //跳转至角色资料
                ExtendedFabCompose(
                    icon = painterResource(id = R.drawable.ic_drop),
                    text = stringResource(id = R.string.character_basic_Info)
                ) {
                    toCharacterBasicInfo(unitId)
                }
            }

        }
    }

}

/**
 * 角色 RANK 装备
 */
@Composable
private fun CharacterEquip(
    unitId: Int,
    maxRank: Int,
    level: Int,
    rank: Int,
    rarity: Int,
    uniqueEquipLevel: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit,
    toRankEquip: (Int) -> Unit,
    toRankCompare: (Int, Int, Int, Int, Int) -> Unit,
    toEquipCount: (Int, Int) -> Unit,
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
            val id3 = equips[1].equipmentId
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
            val id5 = equips[2].equipmentId
            IconCompose(
                data = getEquipIconUrl(id5),
                modifier = Modifier
                    .size(Dimen.iconSize)
                    .clickable {
                        toEquipDetail(id5)
                    }
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //跳转至所有 RANK 装备列表
                SubButton(
                    text = getFormatText(rank),
                    color = colorResource(id = getRankColor(rank)),
                ) {
                    toRankEquip(unitId)
                }
                Row(
                    modifier = Modifier.padding(top = Dimen.smallPadding),
                ) {
                    //跳转至 RANK 对比页面
                    TextButton(onClick = {
                        toRankCompare(unitId, maxRank, level, rarity, uniqueEquipLevel)
                    }) {
                        Text(
                            text = stringResource(id = R.string.rank_compare),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                    //跳转至装备统计页面
                    TextButton(onClick = {
                        toEquipCount(unitId, maxRank)
                    }) {
                        Text(
                            text = stringResource(id = R.string.rank_equip_statistics),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                }
            }
            val id2 = equips[3].equipmentId
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
            val id4 = equips[4].equipmentId
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
 * 专武信息
 */
@Composable
private fun UniqueEquip(
    uniqueEquipLevelMax: Int,
    uniqueEquipLevel: Int,
    silderState: MutableState<Int>,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
    attrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel()
) {
    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier
                .padding(top = Dimen.largePadding)
        ) {
            //名称
            MainText(
                text = it.equipmentName,
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .align(Alignment.CenterHorizontally)
            )
            //图标描述
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconCompose(
                        data = getEquipIconUrl(it.equipmentId),
                        modifier = Modifier.size(Dimen.iconSize)
                    )
                    Text(
                        text = silderState.value.toString(),
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(top = Dimen.smallPadding)
                    )
                }
                Text(
                    text = it.getDesc(),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(start = Dimen.mediuPadding)
                )
            }
            //等级选择
            Slider(
                value = silderState.value.toFloat(),
                onValueChange = { silderState.value = it.toInt() },
                onValueChangeFinished = {
                    if (silderState.value != 0) {
                        attrViewModel.uniqueEquipLevelValue.postValue(silderState.value)
                    }
                },
                valueRange = 1f..uniqueEquipLevelMax.toFloat(),
                modifier = Modifier
                    .fillMaxWidth(0.618f)
                    .height(Dimen.slideHeight)
                    .align(Alignment.CenterHorizontally)
            )
            //属性
            AttrList(attrs = it.attr.allNotZero())
        }
    }

}

/**
 * 星级选择
 */
@Composable
private fun StarSelect(
    max: Int,
    rarity: Int,
    modifier: Modifier = Modifier,
    attrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel()
) {
    Row(modifier) {
        for (i in 1..max) {
            val iconId = when {
                i > rarity -> R.drawable.ic_star_dark
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            Image(
                painter = rememberCoilPainter(request = iconId),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = Dimen.smallPadding, bottom = Dimen.smallPadding)
                    .clip(CircleShape)
                    .size(Dimen.starIconSize)
                    .clickable {
                        attrViewModel.rarityValue.postValue(i)
                    }
            )
        }
    }
}
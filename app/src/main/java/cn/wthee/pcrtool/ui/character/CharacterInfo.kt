package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.CharacterSelectInfo
import cn.wthee.pcrtool.data.view.*
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.circleShape
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.gson.Gson
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CharacterInfo(
    unitId: Int,
    r6Id: Int,
    toEquipDetail: (Int) -> Unit,
    toCharacterBasicInfo: (Int) -> Unit,
    toRankEquip: (Int) -> Unit,
    toRankCompare: (Int, Int, String) -> Unit,
    navViewModel: NavViewModel,
    characterAttrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel(),
) {
    characterAttrViewModel.getMaxRankAndRarity(unitId)
    characterAttrViewModel.isUnknown(unitId)
    //是否已登场
    val unknown = characterAttrViewModel.isUnknown.observeAsState().value ?: false
    //选择的 RANK
    val selectRank = navViewModel.selectRank.observeAsState().value ?: 2
    //选择的信息
    val selectInfo = characterAttrViewModel.selData.observeAsState().value
    selectInfo?.rank = selectRank
    characterAttrViewModel.selData.postValue(selectInfo)

    //最大值
    val maxData = characterAttrViewModel.maxData.observeAsState().value ?: CharacterSelectInfo()
    //基本属性
    val attrData = characterAttrViewModel.sumInfo.observeAsState().value ?: Attr()
    //剧情属性
    val storyAttr = characterAttrViewModel.storyAttrs.observeAsState().value ?: Attr()
    //RANK 装备
    val equips = characterAttrViewModel.equipments.observeAsState().value ?: getEquipsPlaceholder()
    //滑动条
    val level = remember { mutableStateOf(maxData.level.toFloat()) }
    selectInfo?.level = level.value.toInt()
    val uniqueEquipLevel = remember { mutableStateOf(maxData.uniqueEquipLevel.toFloat()) }
    selectInfo?.uniqueEquipLevel = uniqueEquipLevel.value.toInt()
    //星级选择
    val starNum = remember {
        mutableStateOf(maxData.rank)
    }
    selectInfo?.rarity = starNum.value

    //fixme 重复加载数据的问题
    if ((selectInfo == null || selectInfo.rarity == 0) && maxData.rarity != 0) {
        characterAttrViewModel.selData.postValue(maxData)
        navViewModel.selectRank.postValue(maxData.rank)
        level.value = maxData.level.toFloat()
        uniqueEquipLevel.value = maxData.uniqueEquipLevel.toFloat()
        starNum.value = maxData.rarity
    }
    characterAttrViewModel.getCharacterInfo(unitId, selectInfo ?: CharacterSelectInfo())

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
            Surface(modifier = Modifier.padding(bottom = Dimen.sheetMarginBottom)) {
                SkillLoopList(unitId = unitId)
            }
        }
    ) {
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
                Box {
                    //图片
                    CharacterCard(
                        Constants.CHARACTER_FULL_URL + id + Constants.WEBP,
                        scrollState = scrollState
                    )
                    //星级
                    StarSelect(
                        maxData.rarity,
                        starNum,
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
                        //数据加载完，更新页面
                        if (selectInfo != null && storyAttr.allNotZero().isNotEmpty()) {
                            //等级
                            Text(
                                text = level.value.toInt().toString(),
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Slider(
                                value = level.value,
                                onValueChange = { level.value = it },
                                valueRange = 1f..maxData.level.toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth(0.618f)
                                    .align(Alignment.CenterHorizontally)
                            )
                            //属性
                            AttrList(attrs = attrData.all())
                            MainText(
                                text = stringResource(id = R.string.title_story_attr),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = Dimen.largePadding, bottom = Dimen.smallPadding)
                            )
                            //剧情属性
                            AttrList(attrs = storyAttr.allNotZero())
                            //RANK 装备
                            CharacterEquip(
                                unitId,
                                selectInfo,
                                maxData.rank,
                                equips,
                                toEquipDetail,
                                toRankEquip,
                                toRankCompare
                            )
                            //显示专武
                            UniqueEquip(unitId, uniqueEquipLevel, maxData.uniqueEquipLevel)
                            //技能
                            CharacterSkill(id = unitId)
                        } else {
                            //未知角色占位页面
                            if (unknown) {
                                //等级
                                Text(
                                    text = "角色暂未登场",
                                    color = MaterialTheme.colors.primary,
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
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
                    iconId = R.drawable.ic_drop,
                    text = stringResource(id = R.string.character_basic_Info),
                    textWidth = Dimen.getWordWidth(2)
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
    selectInfo: CharacterSelectInfo,
    maxRank: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit,
    toRankEquip: (Int) -> Unit,
    toRankCompare: (Int, Int, String) -> Unit,
) {
    val rank = selectInfo.rank
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
                //跳转至 RANK 对比页面
                TextButton(onClick = {
                    toRankCompare(unitId, maxRank, Gson().toJson(selectInfo))
                }) {
                    Text(
                        text = stringResource(id = R.string.rank_compare),
                        modifier = Modifier.padding(top = Dimen.mediuPadding),
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.subtitle2
                    )
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
    unitId: Int,
    level: MutableState<Float>,
    maxLevel: Int,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
) {
    equipmentViewModel.getUniqueEquipInfos(unitId, level.value.toInt())
    val data = equipmentViewModel.uniqueEquip.observeAsState().value

    data?.let {
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
                        text = level.value.toInt().toString(),
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
                value = level.value,
                onValueChange = { level.value = it },
                valueRange = 1f..maxLevel.toFloat(),
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
fun StarSelect(max: Int, select: MutableState<Int>, modifier: Modifier = Modifier) {

    Row(modifier) {
        for (i in 1..max) {
            val iconId = when {
                i > select.value -> R.drawable.ic_star_dark
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            Image(
                painter = rememberCoilPainter(request = iconId),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = Dimen.smallPadding, bottom = Dimen.smallPadding)
                    .clip(circleShape)
                    .size(Dimen.starIconSize)
                    .clickable {
                        select.value = i
                    }
            )
        }
    }
}
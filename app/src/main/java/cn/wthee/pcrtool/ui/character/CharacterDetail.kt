package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.db.view.all
import cn.wthee.pcrtool.data.db.view.allNotZero
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * 角色信息
 *
 * @param unitId 角色编号
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CharacterDetail(
    scrollState: ScrollState,
    unitId: Int,
    toEquipDetail: (Int) -> Unit,
    toCharacterBasicInfo: (Int) -> Unit,
    toRankEquip: (Int) -> Unit,
    toRankCompare: (Int, Int, Int, Int, Int) -> Unit,
    toEquipCount: (Int, Int) -> Unit,
    toPics: (Int) -> Unit,
    navViewModel: NavViewModel,
    attrViewModel: CharacterAttrViewModel = hiltViewModel(),
    skillViewModel: SkillViewModel = hiltViewModel()
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
    //角色等级滑动条
    val sliderLevel = remember {
        mutableStateOf(0)
    }
    //专武等级滑动条
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
    //卡面高度
    val cardHeight = ScreenUtil.getCharacterCardHeight().toInt().px2dp - 10
    //滚动距离
    val marginTop = when {
        scrollState.value < 0 -> cardHeight
        cardHeight - scrollState.value < 0 -> 0
        else -> cardHeight - scrollState.value
    }
    val coroutineScope = rememberCoroutineScope()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    //关闭监听
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    //收藏状态
    val filter = navViewModel.filterCharacter.observeAsState()
    val loved = remember {
        mutableStateOf(filter.value?.starIds?.contains(unitId) ?: false)
    }
    //技能循环
    skillViewModel.getCharacterSkillLoops(unitId)
    val loopData = skillViewModel.atkPattern.observeAsState().value ?: arrayListOf()
    val iconTypes = skillViewModel.iconTypes.observeAsState().value ?: hashMapOf()

    //页面
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            SkillLoopList(
                loopData, iconTypes, Modifier.padding(
                    top = Dimen.largePadding,
                    start = Dimen.mediuPadding,
                    end = Dimen.mediuPadding,
                )
            )
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
        //关闭
        if (close) {
            coroutineScope.launch {
                state.hide()
            }
            navViewModel.fabMainIcon.postValue(MainIconType.BACK)
            navViewModel.fabCloseClick.postValue(false)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
            ) {
                //角色卡面
                FadeAnimation(visible = rarityMax.value != 0 || unknown) {
                    CardImage(toPics, unitId, scrollState, rarityMax.value, rarity.value ?: 5)
                }
                //数据加载后，展示页面
                val visible =
                    levelMax.value != 0 && allData.value != null && allData.value!!.equips.isNotEmpty()
                SlideAnimation(visible = visible) {
                    if (visible) {
                        //页面
                        Card(
                            shape = CardTopShape,
                            elevation = Dimen.cardElevation,
                            modifier = Modifier.padding(top = marginTop.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(top = Dimen.mediuPadding)
                                    .fillMaxSize()
                            ) {
                                //等级
                                Text(
                                    text = sliderLevel.value.toString(),
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    color = MaterialTheme.colors.primary,
                                    style = MaterialTheme.typography.h6
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
                                //剧情属性
                                MainText(
                                    text = stringResource(id = R.string.title_story_attr),
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(
                                            top = Dimen.largePadding,
                                            bottom = Dimen.smallPadding
                                        )
                                )
                                AttrList(attrs = allData.value!!.stroyAttr.allNotZero())
                                //RANK 装备
                                CharacterEquip(
                                    unitId, rank.value!!,
                                    allData.value!!.equips,
                                    toEquipDetail, toRankEquip,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                //显示专武
                                if (allData.value!!.uniqueEquip.equipmentId != Constants.UNKNOWN_EQUIP_ID) {
                                    UniqueEquip(
                                        uniqueEquipLevelMax.value,
                                        sliderUniqueEquipLevel,
                                        allData.value!!.uniqueEquip
                                    )
                                }
                                //技能
                                CharacterSkill(
                                    unitId = unitId,
                                    level = level.value!!,
                                    atk = max(
                                        allData.value!!.sumAttr.atk.int,
                                        allData.value!!.sumAttr.magicStr.int
                                    )
                                )
                            }
                        }
                    }
                }
                SlideAnimation(visible = unknown) {
                    //未知角色占位页面
                    Card(
                        shape = CardTopShape,
                        elevation = Dimen.cardElevation,
                        modifier = Modifier
                            .padding(top = marginTop.dp)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(R.string.unknown_character),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(Dimen.largePadding)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.padding(
                        bottom = Dimen.fabSmallMarginEnd,
                        end = Dimen.fabMargin
                    )
                ) {
                    //收藏
                    FabCompose(
                        iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                        modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd),
                        defaultPadding = false
                    ) {
                        filter.value?.addOrRemove(unitId)
                        loved.value = !loved.value
                    }
                    //跳转至图片
                    FabCompose(
                        iconType = MainIconType.IMAGE,
                        modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd),
                        defaultPadding = false
                    ) {
                        toPics(unitId)
                    }
                    //跳转至角色资料
                    FabCompose(
                        iconType = MainIconType.CHARACTER_INTRO,
                        modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd),
                        defaultPadding = false
                    ) {
                        toCharacterBasicInfo(unitId)
                    }
                    //技能循环
                    FabCompose(
                        iconType = MainIconType.SKILL_LOOP,
                        defaultPadding = false
                    ) {
                        coroutineScope.launch {
                            if (state.isVisible) {
                                navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                                state.hide()
                            } else {
                                navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                                state.show()
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                ) {
                    //跳转至 RANK 对比页面
                    FabCompose(
                        iconType = MainIconType.RANK_COMPARE,
                        text = stringResource(id = R.string.compare),
                        modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                    ) {
                        toRankCompare(
                            unitId,
                            rankMax.value,
                            level.value!!,
                            rarity.value!!,
                            uniqueEquipLevel.value!!
                        )
                    }
                    //跳转至装备统计页面
                    FabCompose(
                        iconType = MainIconType.EQUIP_CALC,
                        text = stringResource(id = R.string.count),
                    ) {
                        toEquipCount(unitId, rankMax.value)
                    }
                }
            }
        }
    }

}

/**
 * 角色卡面图片
 */
@Composable
private fun CardImage(
    toPics: (Int) -> Unit,
    unitId: Int,
    scrollState: ScrollState,
    rarityMax: Int,
    rarity: Int
) {
    val context = LocalContext.current
    Box(modifier = Modifier
        .clickable {
            //跳转角色图片列表
            toPics(unitId)
            VibrateUtil(context).single()
        }) {
        //图片
        CharacterCard(
            CharacterIdUtil.getMaxCardUrl(unitId),
            scrollState = scrollState,
        )
        //星级
        StarSelect(
            rarityMax,
            rarity,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimen.largePadding)
        )
    }
}

/**
 * 角色 RANK 装备
 * @param unitId 角色编号
 * @param rank 当前rank
 * @param equips 装备列表
 */
@Composable
private fun CharacterEquip(
    unitId: Int,
    rank: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit,
    toRankEquip: (Int) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier.fillMaxWidth(0.8f)) {
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
            IconCompose(data = getEquipIconUrl(id6)) {
                if (id6 != Constants.UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id6)
                }
            }
            IconCompose(data = getEquipIconUrl(id3)) {
                toEquipDetail(id3)
            }
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
            IconCompose(data = getEquipIconUrl(id5)) {
                toEquipDetail(id5)
            }

            //跳转至所有 RANK 装备列表
            SubButton(
                text = getFormatText(rank),
                color = getRankColor(rank),
                modifier = Modifier.padding(
                    top = Dimen.largePadding * 2,
                    bottom = Dimen.largePadding * 2,
                )
            ) {
                toRankEquip(unitId)
            }

            val id2 = equips[3].equipmentId
            IconCompose(data = getEquipIconUrl(id2)) {
                toEquipDetail(id2)
            }
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
            IconCompose(data = getEquipIconUrl(id4)) {
                toEquipDetail(id4)
            }
            IconCompose(data = getEquipIconUrl(id1)) {
                toEquipDetail(id1)
            }
        }
    }
}

/**
 * 专武信息
 * @param uniqueEquipLevelMax 最大等级
 * @param silderState 等级滑动条状态
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@Composable
private fun UniqueEquip(
    uniqueEquipLevelMax: Int,
    silderState: MutableState<Int>,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
) {
    val attrViewModel: CharacterAttrViewModel = hiltViewModel()
    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier.padding(top = Dimen.largePadding)
        ) {
            //名称
            MainText(
                text = it.equipmentName,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            //专武等级选
            Subtitle1(
                text = silderState.value.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colors.primary
            )
            //专武等级选择
            Slider(
                value = silderState.value.toFloat(),
                onValueChange = { silderState.value = it.toInt() },
                onValueChangeFinished = {
                    attrViewModel.uniqueEquipLevelValue.postValue(silderState.value)
                },
                valueRange = 0f..uniqueEquipLevelMax.toFloat(),
                modifier = Modifier
                    .fillMaxWidth(0.618f)
                    .height(Dimen.slideHeight)
                    .align(Alignment.CenterHorizontally)
            )
            //图标描述
            Row(
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .fillMaxWidth()
            ) {
                IconCompose(getEquipIconUrl(it.equipmentId))
                Subtitle2(
                    text = it.getDesc(),
                    modifier = Modifier.padding(start = Dimen.mediuPadding)
                )
            }
            //属性
            AttrList(attrs = it.attr.allNotZero())
        }
    }

}

/**
 * 星级选择
 * @param max 最大值
 * @param rarity 当前值
 */
@Composable
private fun StarSelect(
    max: Int,
    rarity: Int,
    modifier: Modifier = Modifier,
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
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
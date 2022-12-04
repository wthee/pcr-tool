package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.ui.MainActivity.Companion.navController
import cn.wthee.pcrtool.ui.MainActivity.Companion.navSheetState
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import kotlin.math.max


/**
 * 角色信息
 *
 * @param unitId 角色编号
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CharacterDetail(
    scrollState: ScrollState,
    unitId: Int,
    actions: NavActions,
    attrViewModel: CharacterAttrViewModel = hiltViewModel(),
) {
    //特殊形态角色id（吉塔）
    val cutinId = attrViewModel.getCutinId(unitId).collectAsState(initial = 0).value
    //形态切换
    val isCcutinSkill = remember {
        mutableStateOf(true)
    }
    //不同技能形态对应的 unitId
    val currentIdState = remember {
        mutableStateOf(0)
    }
    currentIdState.value = if (isCcutinSkill.value && cutinId != 0) {
        cutinId
    } else {
        unitId
    }

    //最大值
    val maxValue = attrViewModel.getMaxRankAndRarity(unitId)
        .collectAsState(initial = CharacterProperty()).value
    //当前选择的数值信息
    val currentValueState = remember {
        mutableStateOf(CharacterProperty())
    }

    //rank 装备选择监听
    LaunchedEffect(navSheetState.currentValue) {
        val rankEquipSelectedValue =
            navController.currentBackStackEntry?.savedStateHandle?.get<Int>("currentRank")

        if (rankEquipSelectedValue != null) {
            currentValueState.value = currentValueState.value.update(rank = rankEquipSelectedValue)
        }
    }

    //数值信息
    if (currentValueState.value.level == 0 && maxValue.isInit()) {
        //初始为最大值
        currentValueState.value = maxValue
    }

    //角色属性
    val characterAttrData = attrViewModel.getCharacterInfo(unitId, currentValueState.value)
        .collectAsState(initial = AllAttrData()).value

    //数据加载后，展示页面
    val visible = characterAttrData.sumAttr.hp > 1 && characterAttrData.equips.isNotEmpty()
    //未实装角色
    val unknown = maxValue.level == -1

    //收藏状态
    val starIds = FilterCharacter.getStarIdList()
    val loved = remember {
        mutableStateOf(starIds.contains(unitId))
    }

    //页面
    Box(modifier = Modifier.fillMaxSize()) {
        //页面
        if (visible) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //角色卡面
                CharacterCard(
                    unitId = unitId,
                    cutinId = cutinId,
                    loved = loved.value,
                    characterAttrData,
                    currentValueState.value,
                    actions
                )
                //星级
                StarSelect(
                    currentValueState = currentValueState, max = maxValue.rarity
                )
                //角色等级
                CharacterLevel(currentValueState = currentValueState, maxValue.level)
                //属性
                AttrLists(
                    unitId, characterAttrData, actions.toCharacterStoryDetail
                )
                //RANK相关功能
                FlowRow(
                    modifier = Modifier
                        .padding(Dimen.largePadding)
                        .fillMaxWidth(),
                    crossAxisAlignment = FlowCrossAxisAlignment.Center,
                    mainAxisAlignment = MainAxisAlignment.Center,
                    lastLineMainAxisAlignment = MainAxisAlignment.Center,
                ) {
                    //RANK对比
                    IconTextButton(
                        icon = MainIconType.RANK_COMPARE,
                        text = stringResource(id = R.string.rank_compare),
                        iconSize = Dimen.fabIconSize,
                        textStyle = MaterialTheme.typography.bodyMedium
                    ) {
                        actions.toCharacterRankCompare(
                            unitId,
                            maxValue.rank,
                            currentValueState.value.level,
                            currentValueState.value.rarity,
                            currentValueState.value.uniqueEquipmentLevel,
                        )
                    }
                    //装备统计
                    IconTextButton(
                        icon = MainIconType.EQUIP_CALC,
                        text = stringResource(id = R.string.calc_equip_count),
                        iconSize = Dimen.fabIconSize,
                        textStyle = MaterialTheme.typography.bodyMedium
                    ) {
                        actions.toCharacteEquipCount(unitId, maxValue.rank)
                    }
                    //ex装备
                    IconTextButton(
                        icon = MainIconType.EXTRA_EQUIP,
                        text = stringResource(id = R.string.tool_extra_equip),
                        iconSize = Dimen.fabIconSize,
                        textStyle = MaterialTheme.typography.bodyMedium
                    ) {
                        actions.toCharacterExtraEquip(unitId)
                    }
                }
                //RANK 装备
                CharacterEquip(
                    unitId = unitId,
                    currentValueState = currentValueState,
                    maxRank = maxValue.rank,
                    equips = characterAttrData.equips,
                    toEquipDetail = actions.toEquipDetail,
                    toCharacteRankEquip = actions.toCharacterRankEquip
                )
                //显示专武
                if (characterAttrData.uniqueEquip.equipmentId != UNKNOWN_EQUIP_ID) {
                    UniqueEquip(
                        currentValueState = currentValueState,
                        uniqueEquipLevelMax = maxValue.uniqueEquipmentLevel,
                        uniqueEquipmentMaxData = characterAttrData.uniqueEquip,
                    )
                }
                //技能列表
                SkillCompose(
                    unitId = currentIdState.value, property = currentValueState.value, atk = max(
                        characterAttrData.sumAttr.atk.int, characterAttrData.sumAttr.magicStr.int
                    ), unitType = UnitType.CHARACTER, toSummonDetail = actions.toSummonDetail
                )
                CommonSpacer()
                Spacer(modifier = Modifier.height(Dimen.fabSize + Dimen.fabMargin))
            }
        }
        //未登场角色
        if (unknown) {
            CenterTipText(text = stringResource(R.string.unknown_character))
        }
        //悬浮按钮
        if (!unknown) {
            Column(
                modifier = Modifier.align(Alignment.BottomEnd), horizontalAlignment = Alignment.End
            ) {
                if (cutinId != 0) {
                    Row(
                        modifier = Modifier.padding(
                            end = Dimen.fabMargin
                        )
                    ) {
                        //角色技能形态
                        FabCompose(
                            iconType = if (isCcutinSkill.value) {
                                MainIconType.CHARACTER_CUTIN_SKILL
                            } else {
                                MainIconType.CHARACTER_NORMAL_SKILL
                            },
                            text = if (isCcutinSkill.value) {
                                stringResource(id = R.string.cutin_skill)
                            } else {
                                ""
                            },
                        ) {
                            isCcutinSkill.value = !isCcutinSkill.value
                        }
                    }
                }
                Row(
                    modifier = Modifier.padding(
                        end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin
                    )
                ) {
                    //收藏
                    FabCompose(
                        iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                    ) {
                        FilterCharacter.addOrRemove(unitId)
                        loved.value = !loved.value
                    }

                    //技能循环
                    FabCompose(
                        iconType = MainIconType.SKILL_LOOP,
                    ) {
                        actions.toCharacterSkillLoop(currentIdState.value)
                    }
                }
            }
        }
    }

}

/**
 * 角色卡
 */
@Composable
private fun CharacterCard(
    unitId: Int,
    cutinId: Int = 0,
    loved: Boolean,
    characterAttrData: AllAttrData,
    currentValue: CharacterProperty,
    actions: NavActions,
    characterViewModel: CharacterViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    //基本信息
    val basicInfo =
        characterViewModel.getCharacterBasicInfo(unitId).collectAsState(initial = null).value
    if (basicInfo != null) {
        Column(
            modifier = Modifier
                .padding(Dimen.largePadding)
                .width(getItemWidth()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //卡面信息
            Box {
                CharacterItem(
                    character = basicInfo, loved = loved
                ) {
                    actions.toAllPics(unitId, AllPicsType.CHARACTER.type)
                }
            }

            //其它功能
            Row(
                modifier = Modifier
                    .padding(top = Dimen.smallPadding)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //战力计算
                CharacterCoe(characterAttrData, currentValue, actions.toCoe)
                Spacer(modifier = Modifier.weight(1f))
                //资料
                IconTextButton(
                    icon = MainIconType.CHARACTER_INTRO,
                    text = stringResource(id = R.string.character_basic_info),
                ) {
                    actions.toCharacterBasicInfo(unitId)
                }
                //立绘预览
                IconTextButton(
                    icon = MainIconType.PREVIEW_IMAGE,
                    text = stringResource(id = R.string.character_pic),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                ) {
                    actions.toAllPics(unitId, AllPicsType.CHARACTER.type)
                }
                //模型预览
                IconTextButton(
                    icon = MainIconType.PREVIEW_UNIT_SPINE,
                    text = stringResource(id = R.string.spine_preview),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                ) {
                    BrowserUtil.open(
                        context,
                        Constants.PREVIEW_UNIT_URL + (if (cutinId != 0) cutinId else unitId)
                    )
                }
            }
        }
    }
}

/**
 * 战力计算
 */
@Composable
private fun CharacterCoe(
    characterAttrData: AllAttrData,
    currentValue: CharacterProperty,
    toCoe: () -> Unit,
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
) {
    //战力系数
    val coe = attrViewModel.getCoefficient().collectAsState(initial = null).value
    val context = LocalContext.current

    Row(modifier = Modifier
        .clip(MaterialTheme.shapes.extraSmall)
        .clickable {
            VibrateUtil(context).single()
            toCoe()
        }
        .padding(horizontal = Dimen.smallPadding),
        verticalAlignment = Alignment.CenterVertically) {
        val value = if (coe == null) {
            ""
        } else {
            val basicAttr = characterAttrData.sumAttr.copy().sub(characterAttrData.exSkillAttr)
            val basic =
                basicAttr.hp * coe.hp_coefficient + basicAttr.atk * coe.atk_coefficient + basicAttr.magicStr * coe.magic_str_coefficient + basicAttr.def * coe.def_coefficient + basicAttr.magicDef * coe.magic_def_coefficient + basicAttr.physicalCritical * coe.physical_critical_coefficient + basicAttr.magicCritical * coe.magic_critical_coefficient + basicAttr.waveHpRecovery * coe.wave_hp_recovery_coefficient + basicAttr.waveEnergyRecovery * coe.wave_energy_recovery_coefficient + basicAttr.dodge * coe.dodge_coefficient + basicAttr.physicalPenetrate * coe.physical_penetrate_coefficient + basicAttr.magicPenetrate * coe.magic_penetrate_coefficient + basicAttr.lifeSteal * coe.life_steal_coefficient + basicAttr.hpRecoveryRate * coe.hp_recovery_rate_coefficient + basicAttr.energyRecoveryRate * coe.energy_recovery_rate_coefficient + basicAttr.energyReduceRate * coe.energy_reduce_rate_coefficient + basicAttr.accuracy * coe.accuracy_coefficient
            //技能2：默认加上技能2
            var skill = currentValue.level * coe.skill_lv_coefficient
            //技能1：解锁专武，技能1系数提升
            if (characterAttrData.uniqueEquip.equipmentId != UNKNOWN_EQUIP_ID) {
                skill += coe.skill1_evolution_coefficient
                skill += currentValue.level * coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient
            } else {
                skill += currentValue.level * coe.skill_lv_coefficient
            }
            //不同星级处理
            if (currentValue.rarity >= 5) {
                //ex+:大于等于五星，技能 ex+
                skill += coe.exskill_evolution_coefficient
                skill += currentValue.level * coe.skill_lv_coefficient
                if (currentValue.rarity == 6) {
                    //ub+
                    skill += coe.ub_evolution_coefficient
                    skill += currentValue.level * coe.skill_lv_coefficient * coe.ub_evolution_slv_coefficient
                } else {
                    //ub
                    skill += currentValue.level * coe.skill_lv_coefficient
                }
            } else {
                //ub、ex
                skill += currentValue.level * coe.skill_lv_coefficient * 2
            }
            (basic + skill).int.toString()
        }
        MainText(
            text = stringResource(id = R.string.attr_all_value, value),
        )
        IconCompose(
            data = MainIconType.HELP, size = Dimen.smallIconSize
        )
    }
}

/**
 * 角色等级
 */
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun CharacterLevel(
    currentValueState: MutableState<CharacterProperty>, maxLevel: Int
) {
    val context = LocalContext.current

    //角色等级
    val inputLevel = remember {
        mutableStateOf("")
    }
    //输入框管理
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible

    //等级
    Text(
        text = currentValueState.value.level.toString(),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .padding(Dimen.mediumPadding)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                VibrateUtil(context).single()
                if (isImeVisible) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                } else {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            })
    //等级输入框
    OutlinedTextField(
        value = inputLevel.value,
        onValueChange = {
            var filterStr = ""
            it.deleteSpace.forEach { ch ->
                if (Regex("\\d").matches(ch.toString())) {
                    filterStr += ch
                }
            }
            inputLevel.value = when {
                filterStr == "" -> ""
                filterStr.toInt() < 1 -> "1"
                filterStr.toInt() in 1..maxLevel -> filterStr
                else -> maxLevel.toString()
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        trailingIcon = {
            IconCompose(
                data = MainIconType.OK, size = Dimen.fabIconSize
            ) {
                keyboardController?.hide()
                focusManager.clearFocus()
                if (inputLevel.value != "") {
                    currentValueState.value = currentValueState.value.update(
                        level = inputLevel.value.toInt()
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                if (inputLevel.value != "") {
                    currentValueState.value = currentValueState.value.update(
                        level = inputLevel.value.toInt()
                    )
                }
            }
        ),
        modifier = if (isImeVisible) {
            Modifier
                .focusRequester(focusRequester)
                .padding(Dimen.smallPadding)
        } else {
            Modifier
                .focusRequester(focusRequester)
                .height(1.dp)
                .alpha(0f)
        }
    )
}

/**
 * 属性
 */
@Composable
private fun AttrLists(
    unitId: Int, allData: AllAttrData, toCharacterStoryDetail: (Int) -> Unit
) {
    val hasBonus = allData.bonus.attr.allNotZero().isNotEmpty()
    val context = LocalContext.current

    //属性
    AttrList(attrs = allData.sumAttr.all())
    //剧情属性
    if (allData.storyAttr.allNotZero().isNotEmpty()) {
        Row(modifier = Modifier
            .padding(
                top = Dimen.largePadding, bottom = Dimen.smallPadding
            )
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                toCharacterStoryDetail(unitId)
            }
            .padding(horizontal = Dimen.smallPadding),
            verticalAlignment = Alignment.CenterVertically) {
            MainText(text = stringResource(id = R.string.title_story_attr))
            IconCompose(
                data = MainIconType.HELP, size = Dimen.smallIconSize
            )
        }
        AttrList(attrs = allData.storyAttr.allNotZero())
    }
    //Rank 奖励
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasBonus) {
            MainText(
                text = stringResource(id = R.string.title_rank_bonus),
                modifier = Modifier.Companion.padding(
                    top = Dimen.largePadding, bottom = Dimen.smallPadding
                ),
                textAlign = TextAlign.Center
            )
            AttrList(attrs = allData.bonus.attr.allNotZero())
        }
    }
}

/**
 * 角色 RANK 装备
 */
@Composable
private fun CharacterEquip(
    unitId: Int,
    currentValueState: MutableState<CharacterProperty>,
    maxRank: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit,
    toCharacteRankEquip: (Int, Int) -> Unit
) {
    val rank = currentValueState.value.rank

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //装备 6、 3
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(Dimen.iconSize * 4)
        ) {
            val id6 = equips[0].equipmentId
            val id3 = equips[1].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id6)) {
                if (id6 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id6)
                }
            }
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id3)) {
                if (id3 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id3)
                }
            }
        }
        //装备 5、 2
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.mediumPadding)
        ) {
            val id5 = equips[2].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id5)) {
                if (id5 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id5)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconCompose(data = MainIconType.BACK,
                    tint = if (currentValueState.value.rank < maxRank) {
                        getRankColor(rank = rank + 1)
                    } else {
                        Color.Transparent
                    },
                    onClick = if (rank < maxRank) {
                        {
                            currentValueState.value = currentValueState.value.update(
                                rank = rank + 1
                            )
                        }
                    } else {
                        null
                    },
                    size = Dimen.mediumIconSize,
                    modifier = Modifier.padding(start = Dimen.mediumPadding))
                //跳转至所有 RANK 装备列表
                SubButton(
                    text = getFormatText(rank),
                    color = getRankColor(rank),
                    modifier = Modifier.padding(
                        vertical = Dimen.largePadding * 2
                    )
                ) {
                    toCharacteRankEquip(unitId, rank)
                }
                IconCompose(data = MainIconType.MORE,
                    tint = if (rank > 1) {
                        getRankColor(rank = rank - 1)
                    } else {
                        Color.Transparent
                    },
                    onClick = if (rank > 1) {
                        {
                            currentValueState.value = currentValueState.value.update(
                                rank = rank - 1
                            )
                        }
                    } else {
                        null
                    },
                    size = Dimen.mediumIconSize,
                    modifier = Modifier.padding(end = Dimen.mediumPadding))
            }
            val id2 = equips[3].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id2)) {
                if (id2 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id2)
                }
            }

        }
        //装备 4、 1
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(Dimen.iconSize * 4)
        ) {
            val id4 = equips[4].equipmentId
            val id1 = equips[5].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id4)) {
                if (id4 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id4)
                }

            }
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id1)) {
                if (id1 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id1)
                }
            }
        }
    }
}

/**
 * 专武信息
 * @param currentValueState 当前属性
 * @param uniqueEquipLevelMax 等级
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@OptIn(
    ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun UniqueEquip(
    currentValueState: MutableState<CharacterProperty>,
    uniqueEquipLevelMax: Int,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
) {
    val inputLevel = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible
    val context = LocalContext.current

    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier.padding(top = Dimen.largePadding * 2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //名称
            MainText(
                text = it.equipmentName, selectable = true
            )
            //专武等级
            Text(text = currentValueState.value.uniqueEquipmentLevel.toString(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(Dimen.mediumPadding)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        VibrateUtil(context).single()
                        if (isImeVisible) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        } else {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    })
            //等级输入框
            OutlinedTextField(
                value = inputLevel.value,
                shape = MaterialTheme.shapes.medium,
                onValueChange = {
                    var filterStr = ""
                    it.deleteSpace.forEach { ch ->
                        if (Regex("\\d").matches(ch.toString())) {
                            filterStr += ch
                        }
                    }
                    inputLevel.value = when {
                        filterStr == "" -> ""
                        filterStr.toInt() < 1 -> "1"
                        filterStr.toInt() in 1..uniqueEquipLevelMax -> filterStr
                        else -> uniqueEquipLevelMax.toString()
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = {
                    IconCompose(
                        data = MainIconType.OK, size = Dimen.fabIconSize
                    ) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (inputLevel.value != "") {
                            currentValueState.value = currentValueState.value.update(
                                uniqueEquipmentLevel = inputLevel.value.toInt()
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (inputLevel.value != "") {
                        currentValueState.value = currentValueState.value.update(
                            uniqueEquipmentLevel = inputLevel.value.toInt()
                        )
                    }
                }),
                modifier = if (isImeVisible) {
                    Modifier
                        .focusRequester(focusRequester)
                        .padding(Dimen.smallPadding)
                } else {
                    Modifier
                        .focusRequester(focusRequester)
                        .height(1.dp)
                        .alpha(0f)
                },
                maxLines = 1,
                singleLine = true
            )
            //图标描述
            Row(
                modifier = Modifier
                    .padding(
                        start = Dimen.largePadding,
                        end = Dimen.largePadding,
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth()
            ) {
                IconCompose(
                    data = ImageResourceHelper.getInstance().getEquipPic(it.equipmentId)
                )
                Subtitle2(
                    text = it.getDesc(),
                    modifier = Modifier.padding(start = Dimen.mediumPadding),
                    selectable = true
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
 */
@Composable
private fun StarSelect(
    currentValueState: MutableState<CharacterProperty>,
    max: Int,
    modifier: Modifier = Modifier,
) {

    Row(modifier) {
        for (i in 1..max) {
            val iconId = when {
                i > currentValueState.value.rarity -> R.drawable.ic_star_dark
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            IconCompose(
                data = iconId,
                size = Dimen.fabIconSize,
                modifier = Modifier.padding(Dimen.smallPadding)
            ) {
                currentValueState.value = currentValueState.value.update(
                    rarity = i
                )
            }

        }
    }
}
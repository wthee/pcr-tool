package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.common.AttrList
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.common.PositionIcon
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import cn.wthee.pcrtool.viewmodel.SummonViewModel
import kotlin.math.max

/**
 * 技能召唤物信息
 */
@Composable
fun SummonDetail(
    unitId: Int,
    level: Int,
    summonViewModel: SummonViewModel = hiltViewModel(),
    attrViewModel: CharacterAttrViewModel = hiltViewModel(),
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    //TODO 星级、rank
    //基本信息
    val basicInfo = summonViewModel.getSummonData(unitId).collectAsState(initial = null).value

    //数值信息
    val maxValue = attrViewModel.getMaxRankAndRarity(unitId)
        .collectAsState(initial = CharacterProperty()).value
    val currentValueState = attrViewModel.currentValue.observeAsState()
    if (currentValueState.value == null && maxValue.isInit()) {
        attrViewModel.currentValue.postValue(maxValue)
    }
    val attrs = attrViewModel.getCharacterInfo(unitId, currentValueState.value).collectAsState(
        initial = null
    ).value

    //技能信息
    val loopData =
        skillViewModel.getCharacterSkillLoops(unitId).collectAsState(initial = arrayListOf()).value
    val iconTypes = skillViewModel.iconTypes.observeAsState().value ?: hashMapOf()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        basicInfo?.let {
            MainText(text = it.unitName)
            MainText(text = level.toString())
            Row(verticalAlignment = Alignment.CenterVertically) {
                PositionIcon(position = it.position)
                MainText(
                    text = it.position.toString(),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
        }
        attrs?.let {
            //基本属性
            AttrList(attrs = attrs.sumAttr.summonAttr())
            //技能循环
            SkillLoopList(
                loopData,
                iconTypes,
                isClanBoss = true,
                modifier = Modifier.padding(Dimen.mediumPadding * 2)
            )
            //技能信息
            SkillCompose(
                unitId = unitId,
                cutinId = 0,
                atk = max(attrs.sumAttr.atk, attrs.sumAttr.magicStr).int
            )
        }
    }
}
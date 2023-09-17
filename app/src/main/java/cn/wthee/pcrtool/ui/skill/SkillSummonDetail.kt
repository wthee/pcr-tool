package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CharacterPositionTag
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.enemy.EnemyDetail
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import cn.wthee.pcrtool.viewmodel.SummonViewModel
import kotlin.math.max

/**
 * 技能召唤物信息
 *
 * @param id    召唤物id 或敌人id enemyId
 * @param unitType  召唤物类型
 */
@Composable
fun SummonDetail(
    id: Int,
    unitType: UnitType,
    level: Int,
    rank: Int,
    rarity: Int
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (unitType == UnitType.ENEMY || unitType == UnitType.ENEMY_SUMMON) {
            //敌人召唤物
            EnemyDetail(enemyId = id)
        } else {
            //角色召唤物
            CharacterSummonDetail(
                unitId = id,
                level = level,
                rank = rank,
                rarity = rarity
            )
        }
    }

}

/**
 * 角色召唤物信息
 */
@Composable
private fun CharacterSummonDetail(
    unitId: Int,
    level: Int,
    rank: Int,
    rarity: Int,
    attrViewModel: CharacterAttrViewModel = hiltViewModel(),
    summonViewModel: SummonViewModel = hiltViewModel(),
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    val property = CharacterProperty(level = level, rank = rank, rarity = rarity)
    //基本信息
    val basicInfoFlow = remember {
        summonViewModel.getSummonData(unitId)
    }
    val basicInfo by basicInfoFlow.collectAsState(initial = null)
    //数值信息
    val attrsFlow = remember {
        attrViewModel.getCharacterInfo(unitId, property)
    }
    val attrs by attrsFlow.collectAsState(initial = null)
    //技能信息
    val loopDataFlow = remember {
        skillViewModel.getCharacterSkillLoops(unitId)
    }
    val loopData by loopDataFlow.collectAsState(initial = arrayListOf())


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        basicInfo?.let {
            //名称
            MainText(
                text = it.unitName,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.mediumPadding),
                selectable = true
            )
            //等级等属性
            CaptionText(
                text = stringResource(
                    id = R.string.character_summon_info,
                    level,
                    rank,
                    rarity
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            //位置
            CharacterPositionTag(
                modifier = Modifier.padding(Dimen.mediumPadding),
                position = it.position
            )
        }
        attrs?.let {
            Column {
                //属性
                AttrList(attrs = it.sumAttr.summonAttr())

                //技能循环
                MainText(
                    text = stringResource(R.string.skill_loop),
                    modifier = Modifier
                        .padding(top = Dimen.largePadding * 2)
                        .align(Alignment.CenterHorizontally)
                )
                SkillLoopList(
                    loopData,
                    unitType = UnitType.CHARACTER_SUMMON,
                    modifier = Modifier.padding(
                        top = Dimen.largePadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
                )

                //技能信息
                SkillCompose(
                    unitId = unitId,
                    atk = max(it.sumAttr.atk, it.sumAttr.magicStr).int,
                    unitType = UnitType.CHARACTER_SUMMON,
                    property = property
                )
                CommonSpacer()
            }
        }
    }
}
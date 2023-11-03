package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.SummonData
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CharacterPositionTag
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.tool.enemy.EnemyDetailScreen
import cn.wthee.pcrtool.utils.int
import kotlin.math.max

/**
 * 技能召唤物信息
 *
 * @param id    召唤物id 或敌人id enemyId
 * @param unitType  召唤物类型
 */
@Composable
fun SkillSummonScreen(
    id: Int,
    unitType: UnitType,
    level: Int,
    rank: Int,
    rarity: Int
) {
    MainScaffold {
        if (unitType == UnitType.ENEMY || unitType == UnitType.ENEMY_SUMMON) {
            //敌人召唤物
            EnemyDetailScreen(enemyId = id)
        } else {
            //角色召唤物
            CharacterSummonDetailScreen(
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
private fun CharacterSummonDetailScreen(
    unitId: Int,
    level: Int,
    rank: Int,
    rarity: Int,
    skillSummonDetailViewModel: SkillSummonDetailViewModel = hiltViewModel()
) {
    val property = CharacterProperty(level = level, rank = rank, rarity = rarity)
    val uiState by skillSummonDetailViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(unitId, property.hashCode()) {
        skillSummonDetailViewModel.loadData(unitId, property)
    }


    CharacterSummonDetailContent(
        uiState = uiState,
        unitId = unitId,
        property = property
    )
}

@Composable
private fun CharacterSummonDetailContent(
    uiState: SkillSummonUiState,
    unitId: Int,
    property: CharacterProperty
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        uiState.summonData?.let {
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
                    property.level,
                    property.rank,
                    property.rarity
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            //位置
            CharacterPositionTag(
                modifier = Modifier.padding(Dimen.mediumPadding),
                position = it.position
            )
        }
        uiState.attrs?.let {
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
                SkillLoopScreen(
                    attackPatternList = uiState.attackPatternList,
                    unitType = UnitType.CHARACTER_SUMMON,
                    modifier = Modifier.padding(
                        top = Dimen.largePadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
                )

                //技能信息
                SkillListScreen(
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


/**
 * @see [SkillLoopItemContentPreview]
 */
@CombinedPreviews
@Composable
private fun SkillLoopItemContentPreview() {
    PreviewLayout {
        CharacterSummonDetailContent(
            uiState = SkillSummonUiState(
                summonData = SummonData(unitName = stringResource(id = R.string.debug_name)),
                attrs = AllAttrData(
                    sumAttr = Attr().also {
                        it.atk = 1000.0
                    }
                ),
                attackPatternList = emptyList()
            ),
            unitId = 10101,
            property = CharacterProperty(
                level = 100
            )
        )
    }
}
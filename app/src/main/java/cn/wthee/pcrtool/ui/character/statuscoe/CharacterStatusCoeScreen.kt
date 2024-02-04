package cn.wthee.pcrtool.ui.character.statuscoe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.UnitStatusCoefficient
import cn.wthee.pcrtool.data.enums.AttrValueType
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout

/**
 * 属性说明
 */
@Composable
fun CharacterStatusCoeScreen(characterStatusCoeViewModel: CharacterStatusCoeViewModel = hiltViewModel()) {
    val uiState by characterStatusCoeViewModel.uiState.collectAsStateWithLifecycle()

    MainScaffold {
        StateBox(stateType = uiState.loadState) {
            uiState.coeValue?.let { coe ->
                CharacterStatusCoeContent(coe)
            }
        }
    }
}

@Composable
private fun CharacterStatusCoeContent(coe: UnitStatusCoefficient) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        //其它说明
        CaptionText(
            text = stringResource(R.string.tip_status_coe), modifier = Modifier.padding(
                top = Dimen.largePadding,
                bottom = Dimen.smallPadding
            )
        )
        //属性说明
        MainText(
            text = stringResource(R.string.title_attr_tip),
            modifier = Modifier.padding(
                top = Dimen.largePadding,
                bottom = Dimen.smallPadding
            )
        )
        Subtitle1(
            text = stringResource(R.string.attr_calc),
            modifier = Modifier.padding(
                start = Dimen.largePadding,
                end = Dimen.largePadding
            )
        )

        //基本系数
        MainText(
            text = stringResource(id = R.string.title_basic_coe),
            modifier = Modifier
                .padding(
                    top = Dimen.largePadding,
                    bottom = Dimen.smallPadding
                )
        )
        AttrList(
            attrs = coe.getAttrValueList(LocalContext.current),
            attrValueType = AttrValueType.DOUBLE
        )
        //技能相关系数
        MainText(
            text = stringResource(id = R.string.title_skill_basic_coe),
            modifier = Modifier
                .padding(
                    top = Dimen.largePadding,
                    bottom = Dimen.smallPadding
                )
        )

        val skillCoeList = arrayListOf(
            AttrValue(
                title = stringResource(id = R.string.title_skill_coe),
                value = coe.skill_lv_coefficient
            ),
            AttrValue(
                title = stringResource(id = R.string.title_skill_1_coe),
                value = coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient
            ),
            AttrValue(
                title = stringResource(id = R.string.title_ub_coe),
                value = coe.skill_lv_coefficient * coe.ub_evolution_slv_coefficient
            ),
            AttrValue(
                title = stringResource(id = R.string.title_ex_skill_add),
                value = coe.exskill_evolution_coefficient.toDouble()
            ),
            AttrValue(
                title = stringResource(id = R.string.title_skill_1_add),
                value = coe.skill1_evolution_coefficient.toDouble()
            ),
            AttrValue(
                title = stringResource(id = R.string.title_ub_add),
                value = coe.ub_evolution_coefficient.toDouble()
            )
        )
        AttrList(attrs = skillCoeList, itemWidth = getItemWidth(), fixColumns = 1)
        CommonSpacer()
    }
}

@CombinedPreviews
@Composable
private fun CharacterStatusCoeContentPreview() {
    PreviewLayout {
        CharacterStatusCoeContent(UnitStatusCoefficient())
    }
}
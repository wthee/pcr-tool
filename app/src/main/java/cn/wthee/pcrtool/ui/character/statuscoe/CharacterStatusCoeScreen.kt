package cn.wthee.pcrtool.ui.character.statuscoe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.Constants

/**
 * 属性说明
 */
@Composable
fun CharacterStatusCoeScreen(characterStatusCoeViewModel: CharacterStatusCoeViewModel = hiltViewModel()) {
    val uiState by characterStatusCoeViewModel.uiState.collectAsStateWithLifecycle()

    MainScaffold {
        StateBox(stateType = uiState.loadingState) {
            uiState.coeValue?.let { coe ->
                CharacterStatusCoeContent(coe)
            }
        }
    }
}

@Composable
private fun CharacterStatusCoeContent(coe: UnitStatusCoefficient) {
    val coeList = arrayListOf<AttrValue>()
    for (i in 0..16) {
        val value = when (i) {
            0 -> coe.hp_coefficient
            1 -> coe.life_steal_coefficient
            2 -> coe.atk_coefficient
            3 -> coe.magic_str_coefficient
            4 -> coe.def_coefficient
            5 -> coe.magic_def_coefficient
            6 -> coe.physical_critical_coefficient
            7 -> coe.magic_critical_coefficient
            8 -> coe.physical_penetrate_coefficient
            9 -> coe.magic_penetrate_coefficient
            10 -> coe.accuracy_coefficient
            11 -> coe.dodge_coefficient
            12 -> coe.wave_hp_recovery_coefficient
            13 -> coe.hp_recovery_rate_coefficient
            14 -> coe.wave_energy_recovery_coefficient
            15 -> coe.energy_recovery_rate_coefficient
            16 -> coe.energy_reduce_rate_coefficient
            else -> 0.0
        }
        coeList.add(AttrValue(Constants.ATTR[i], value))
    }


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
        AttrList(attrs = coeList, AttrValueType.DOUBLE)
        //技能相关系数
        MainText(
            text = stringResource(id = R.string.title_skill_basic_coe),
            modifier = Modifier
                .padding(
                    top = Dimen.largePadding,
                    bottom = Dimen.smallPadding
                )
        )
        CoeItem(
            title = stringResource(id = R.string.title_skill_coe),
            value = coe.skill_lv_coefficient
        )
        CoeItem(
            title = stringResource(R.string.title_skill_1_coe),
            value = coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient
        )
        CoeItem(
            title = stringResource(R.string.title_ub_coe),
            value = coe.skill_lv_coefficient * coe.ub_evolution_slv_coefficient
        )
        CoeItem(
            title = stringResource(R.string.title_ex_skill_add),
            value = coe.exskill_evolution_coefficient.toDouble()
        )
        CoeItem(
            title = stringResource(R.string.title_skill_1_add),
            value = coe.skill1_evolution_coefficient.toDouble()
        )
        CoeItem(
            title = stringResource(R.string.title_ub_add),
            value = coe.ub_evolution_coefficient.toDouble()
        )
        CommonSpacer()
    }
}

@Composable
private fun CoeItem(title: String, value: Double) {
    Row(
        modifier = Modifier.padding(top = Dimen.smallPadding)
    ) {
        MainTitleText(
            text = title,
            modifier = Modifier
                .padding(start = Dimen.largePadding)
                .weight(0.6f)
        )
        MainContentText(
            text = value.toString(),
            modifier = Modifier
                .padding(end = Dimen.largePadding)
                .weight(0.4f)
        )
    }
}


@CombinedPreviews
@Composable
private fun CharacterStatusCoeContentPreview() {
    PreviewLayout {
        CharacterStatusCoeContent(UnitStatusCoefficient())
    }
}
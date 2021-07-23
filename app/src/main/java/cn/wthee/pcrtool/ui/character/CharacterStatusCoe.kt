package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.ui.compose.MainContentText
import cn.wthee.pcrtool.ui.compose.MainText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel

@Composable
fun CharacterStatusCoeCompose(attrViewModel: CharacterAttrViewModel = hiltViewModel()) {
    val coeValue = attrViewModel.getCoefficient().collectAsState(initial = null).value
    coeValue?.let { coe ->
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //基本系数
            MainText(
                text = stringResource(id = R.string.title_basic_coe),
                modifier = Modifier
                    .padding(
                        top = Dimen.largePadding,
                        bottom = Dimen.smallPadding
                    )
            )
            coeList.forEach {
                CoeItem(it.title, it.value)
            }
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
                title = stringResource(R.string.title_ex_skill_add),
                value = coe.exskill_evolution_coefficient.toDouble()
            )
            CoeItem(
                title = stringResource(R.string.title_skill_1_add),
                value = coe.skill1_evolution_coefficient.toDouble()
            )
            CoeItem(
                title = stringResource(R.string.title_skill_1_coe),
                value = coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient
            )
            CoeItem(
                title = stringResource(R.string.title_ub_add),
                value = coe.ub_evolution_coefficient.toDouble()
            )
            CoeItem(
                title = stringResource(R.string.title_ub_coe),
                value = coe.skill_lv_coefficient * coe.ub_evolution_slv_coefficient
            )
        }
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
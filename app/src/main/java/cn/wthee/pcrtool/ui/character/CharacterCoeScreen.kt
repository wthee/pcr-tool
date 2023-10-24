package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.int

/**
 * 战力计算
 */
@Composable
fun CharacterCoeScreen(
    characterAttrData: AllAttrData,
    currentValue: CharacterProperty,
    toCoe: () -> Unit,
    coeViewModel: CharacterCoeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    //战力系数
    val uiState by coeViewModel.uiState.collectAsStateWithLifecycle()


    Row(
        modifier = Modifier
            .padding(start = Dimen.smallPadding)
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                toCoe()
            }
            .padding(horizontal = Dimen.smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var value = ""

        uiState.coeValue?.let { coe ->
            val basicAttr = characterAttrData.sumAttr.copy().sub(characterAttrData.exSkillAttr)
            val basic =
                basicAttr.hp * coe.hp_coefficient + basicAttr.atk * coe.atk_coefficient + basicAttr.magicStr * coe.magic_str_coefficient + basicAttr.def * coe.def_coefficient + basicAttr.magicDef * coe.magic_def_coefficient + basicAttr.physicalCritical * coe.physical_critical_coefficient + basicAttr.magicCritical * coe.magic_critical_coefficient + basicAttr.waveHpRecovery * coe.wave_hp_recovery_coefficient + basicAttr.waveEnergyRecovery * coe.wave_energy_recovery_coefficient + basicAttr.dodge * coe.dodge_coefficient + basicAttr.physicalPenetrate * coe.physical_penetrate_coefficient + basicAttr.magicPenetrate * coe.magic_penetrate_coefficient + basicAttr.lifeSteal * coe.life_steal_coefficient + basicAttr.hpRecoveryRate * coe.hp_recovery_rate_coefficient + basicAttr.energyRecoveryRate * coe.energy_recovery_rate_coefficient + basicAttr.energyReduceRate * coe.energy_reduce_rate_coefficient + basicAttr.accuracy * coe.accuracy_coefficient
            //技能2：默认加上技能2
            var skill = currentValue.level * coe.skill_lv_coefficient
            //技能1：解锁专武，技能1系数提升
            if (characterAttrData.uniqueEquipList.isNotEmpty()) {
                skill += coe.skill1_evolution_coefficient * characterAttrData.uniqueEquipList.size
                skill += currentValue.level * coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient * characterAttrData.uniqueEquipList.size
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
            value = (basic + skill).int.toString()
        }
        //战力数值
        MainText(
            text = stringResource(id = R.string.attr_all_value, value),
        )
        MainIcon(
            data = MainIconType.HELP, size = Dimen.smallIconSize
        )
    }
}
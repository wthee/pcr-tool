package cn.wthee.pcrtool.ui.tool.unknownskill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.skill.SkillItemContent

/**
 * 未知描述的技能列表
 */
@Composable
fun UnknownSkillListScreen(
    viewModel: UnknownSkillListViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        MainTitleText(text = "${uiState.unitSkillList.size} | ${uiState.enemySkillList.size}")
        //角色技能
        StateBox(stateType = uiState.unitSKillLoadState) {
            uiState.unitSkillList.forEach {
                SkillItemContent(skillDetail = it, unitType = UnitType.CHARACTER)
            }
        }

        CommonSpacer()
        //公会战boss仅能
        uiState.enemySkillList.forEach {
            SkillItemContent(skillDetail = it, unitType = UnitType.ENEMY)
        }
    }
}
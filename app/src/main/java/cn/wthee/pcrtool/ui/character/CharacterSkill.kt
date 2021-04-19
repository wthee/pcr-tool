package cn.wthee.pcrtool.ui.character

import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.ui.compose.SkillCompose
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel

/**
 * 角色技能列表
 */
@Composable
fun CharacterSkill(id: Int, attrViewModel: CharacterAttrViewModel = hiltNavGraphViewModel()) {
    val level = attrViewModel.level.observeAsState().value ?: 0
    val atk = attrViewModel.atk.observeAsState().value ?: 0
    Card(shape = CardTopShape, elevation = 0.dp) {
        SkillCompose(level = level, atk = atk, id = id)
    }
}
package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.ui.common.CharacterCard
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.CharacterViewModel

@Composable
fun CharacterBasicInfo(
    unitId: Int,
    r6Id: Int,
    viewModel: CharacterViewModel = hiltNavGraphViewModel()
) {
    viewModel.getCharacter(unitId)
    val data = viewModel.character.observeAsState().value

    var id = unitId
    id += if (r6Id != 0) 60 else 30

    Column(modifier = Modifier.fillMaxSize()) {
        CharacterCard(Constants.CHARACTER_FULL_URL + id + Constants.WEBP)
        Text(data?.intro ?: "...")
        Text(data?.catchCopy ?: "...")
    }
}
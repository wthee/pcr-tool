package cn.wthee.pcrtool.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.viewmodel.CharacterViewModel

@Composable
fun CharacterList(navController: NavController, viewModel: CharacterViewModel = viewModel()) {

    viewModel.getCharacters(FilterCharacter(), SortType.SORT_DATE, false, "")
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(viewModel.characterList.value!!) {
            CharacterItem(it)
        }
    }
}

@Composable
fun CharacterItem(character: CharacterInfo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = character.getNameF())
            Row() {
                Text(text = character.getFixedAge())
                Text(text = character.getFixedHeight())
                Text(text = character.getFixedWeight())
                Text(text = character.position.toString())
            }
        }
    }
}

@Preview
@Composable
fun PreviewCharacterItem() {
    CharacterItem(CharacterInfo())
}
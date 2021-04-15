package cn.wthee.pcrtool.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.viewmodel.CharacterViewModel

@Composable
fun CharacterList(
    navController: NavController,
    viewModel: CharacterViewModel = hiltNavGraphViewModel()
) {
    viewModel.getCharacters(FilterCharacter(), SortType.SORT_DATE, false, "")
    var list by remember { mutableStateOf(listOf<CharacterInfo>()) }
    viewModel.characterList.observeForever {
        list = it
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(list) {
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
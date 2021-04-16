package cn.wthee.pcrtool.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.ui.common.CharacterCard
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.CharacterViewModel

/**
 * 角色列表
 */
@ExperimentalFoundationApi
@Composable
fun CharacterList(
    toDetail: (Int, Int) -> Unit,
    viewModel: CharacterViewModel = hiltNavGraphViewModel(),
) {
    viewModel.getCharacters(FilterCharacter(), SortType.SORT_DATE, false, "")
    val list = viewModel.characterList.observeAsState()
    LazyVerticalGrid(cells = GridCells.Fixed(2)) {
        items(list.value ?: arrayListOf()) {
            CharacterItem(it, toDetail)
        }
    }
}

/**
 * 角色列表项
 */
@Composable
fun CharacterItem(
    character: CharacterInfo,
    toDetail: (Int, Int) -> Unit,
) {

    Card(shape = Shapes.large, modifier = Modifier
        .padding(Dimen.mediuPadding)
        .clip(Shapes.large)
        .clickable {
            //跳转至详情
            toDetail(character.id, character.r6Id)
        }) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            //图片
            var id = character.id
            id += if (character.r6Id != 0) 60 else 30
            CharacterCard(Constants.CHARACTER_FULL_URL + id + Constants.WEBP, true)
            //名字
            Text(
                text = character.getNameF(),
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(Dimen.smallPadding)
            )
            //其它属性
            Row(modifier = Modifier.padding(Dimen.smallPadding)) {
                Text(text = character.getFixedAge(), style = MaterialTheme.typography.body1)
                Text(text = character.getFixedHeight(), style = MaterialTheme.typography.body1)
                Text(text = character.getFixedWeight(), style = MaterialTheme.typography.body1)
                Text(text = character.position.toString(), style = MaterialTheme.typography.body1)
            }
        }
    }
}
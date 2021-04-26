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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.ui.compose.CharacterCard
import cn.wthee.pcrtool.ui.compose.PositionIcon
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

    Card(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
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
            //名字、位置
            Row(
                modifier = Modifier.padding(Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = character.getNameF(),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.weight(1f)
                )
                PositionIcon(character.position, 18.dp)
            }
            //其它属性
            Row(
                modifier = Modifier.padding(
                    start = Dimen.smallPadding,
                    bottom = Dimen.smallPadding
                )
            ) {
                CharacterNumberText(character.getFixedAge())
                CharacterNumberText(character.getFixedHeight() + "CM")
                CharacterNumberText(character.getFixedWeight() + "KG")
                CharacterNumberText(character.position.toString())
            }
        }
    }
}

@Composable
fun CharacterNumberText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colors.primaryVariant,
        style = MaterialTheme.typography.caption,
        modifier = Modifier.padding(end = Dimen.smallPadding)
    )
}
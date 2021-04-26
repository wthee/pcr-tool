package cn.wthee.pcrtool.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.CharacterCard
import cn.wthee.pcrtool.ui.compose.PositionIcon
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import kotlinx.coroutines.launch

/**
 * 角色列表
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CharacterList(
    toDetail: (Int, Int) -> Unit,
    navViewModel: NavViewModel,
    viewModel: CharacterViewModel = hiltNavGraphViewModel(),
) {
    viewModel.getCharacters(FilterCharacter(), SortType.SORT_DATE, false, "")
    val list = viewModel.characterList.observeAsState()

    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    //FabMain 是否显示
    if (state.direction == -1f) {
        navViewModel.fabShow.postValue(false)
    } else if (state.direction == 1f) {
        navViewModel.fabShow.postValue(true)
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Text(text = "Hello from sheet")
            }
        }
    ) {
        Box {
            LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                items(list.value ?: arrayListOf()) {
                    CharacterItem(it, toDetail)
                }
            }
            Button(onClick = {
                coroutineScope.launch {
                    if (state.isVisible) {
                        state.hide()
                    } else {
                        state.show()
                    }
                }
            }) {
                Text(text = "Expand/Collapse Bottom Sheet")
            }
        }
    }
}

/**
 * 角色列表项
 */
@Composable
private fun CharacterItem(
    character: CharacterInfo,
    toDetail: (Int, Int) -> Unit,
) {

    Card(
        shape = Shapes.large,
        elevation = Dimen.cardElevation,
        modifier = Modifier
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
private fun CharacterNumberText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colors.primaryVariant,
        style = MaterialTheme.typography.caption,
        modifier = Modifier.padding(end = Dimen.smallPadding)
    )
}


package cn.wthee.pcrtool.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.MainText
import cn.wthee.pcrtool.ui.compose.Subtitle1
import cn.wthee.pcrtool.ui.compose.TopBarCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.vibrate
import cn.wthee.pcrtool.viewmodel.OverviewViewModel

/**
 * 首页纵览
 */
@ExperimentalMaterialApi
@Composable
fun Overview(
    actions: NavActions,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    overviewViewModel.getCharacterList()
    val characterList = overviewViewModel.characterList.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        TopBarCompose(scrollState = rememberLazyListState())
        //角色
        SectionHead(MainIconType.CHARACTER, R.string.character) {
            actions.toCharacterList()
        }
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            characterList.value?.let { list ->
                list.forEach {
                    CharacterItem(it, FilterCharacter()) { id ->
                        actions.toCharacterDetail(id)
                    }
                }
            }
        }
        //装备
        SectionHead(MainIconType.EQUIP, R.string.tool_equip) {
            actions.toEquipList()
        }
    }
}

@Composable
private fun SectionHead(
    iconType: MainIconType,
    @StringRes titleId: Int,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Row(modifier = Modifier.padding(Dimen.largePadding)) {
        IconCompose(data = iconType.icon, size = Dimen.fabIconSize)
        MainText(
            text = stringResource(id = titleId),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        Subtitle1(
            text = stringResource(id = R.string.more),
            modifier = Modifier.clickable(onClick = onClick.vibrate {
                VibrateUtil(context).single()
            })
        )
    }
}

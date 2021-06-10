package cn.wthee.pcrtool.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
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
    overviewViewModel.getEquipList()
    val characterList = overviewViewModel.characterList.observeAsState()
    val equipList = overviewViewModel.equipList.observeAsState()

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
                    CharacterCard(
                        CharacterIdUtil.getMaxCardUrl(
                            it.id,
                            MainActivity.r6Ids.contains(it.id)
                        ),
                        modifier = Modifier
                            .padding(Dimen.mediuPadding)
                            .width(200.dp)
                            .clickable {
                                actions.toCharacterDetail(it.id)
                            }
                    )
                }
            }
        }
        //装备
        SectionHead(MainIconType.EQUIP, R.string.tool_equip) {
            actions.toEquipList()
        }
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            equipList.value?.let { list ->
                list.forEach {
                    Box(modifier = Modifier.padding(Dimen.mediuPadding)) {
                        IconCompose(data = getEquipIconUrl(it.equipmentId)) {
                            actions.toEquipDetail(it.equipmentId)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 标题
 */
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
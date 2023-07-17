package cn.wthee.pcrtool.ui.tool.uniqueequip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.character.CharacterTag
import cn.wthee.pcrtool.ui.character.getAtkColor
import cn.wthee.pcrtool.ui.character.getAtkText
import cn.wthee.pcrtool.ui.character.getLimitTypeColor
import cn.wthee.pcrtool.ui.character.getLimitTypeText
import cn.wthee.pcrtool.ui.components.BottomSearchBar
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel


/**
 * 专用装备列表
 */
@Composable
fun UniqueEquipList(
    scrollState: LazyGridState,
    viewModel: EquipmentViewModel = hiltViewModel(),
    toUniqueEquipDetail: (Int) -> Unit,
    characterViewModel: CharacterViewModel = hiltViewModel()
) {
    val defaultName = navViewModel.uniqueEquipName.value ?: ""
    //关键词输入
    val keywordInputState = remember {
        mutableStateOf(defaultName)
    }
    //关键词查询
    val keywordState = remember {
        mutableStateOf(defaultName)
    }

    LaunchedEffect(keywordState.value) {
        navViewModel.uniqueEquipName.value = keywordState.value
    }

    val uniqueEquips =
        viewModel.getUniqueEquips(keywordState.value).collectAsState(initial = arrayListOf()).value


    Box(modifier = Modifier.fillMaxSize()) {
        if (uniqueEquips.isNotEmpty()) {

            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                items(
                    uniqueEquips,
                    key = {
                        it.equipId
                    }
                ) { uniqueEquip ->
                    UniqueEquipItem(
                        uniqueEquip,
                        toUniqueEquipDetail,
                        characterViewModel
                    )
                }
                item {
                    CommonSpacer()
                }
            }

        } else {
            CenterTipText(
                stringResource(id = R.string.no_data)
            )
        }


        //搜索栏
        val count = uniqueEquips.size

        BottomSearchBar(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            labelStringId = R.string.search_unique_equip,
            keywordInputState = keywordInputState,
            keywordState = keywordState,
            leadingIcon = MainIconType.UNIQUE_EQUIP,
            gridScrollState = scrollState,
            fabText = count.toString()
        )
    }

}


/**
 * 装备
 */
@Composable
private fun UniqueEquipItem(
    equip: UniqueEquipBasicData,
    toUniqueEquipDetail: (Int) -> Unit,
    characterViewModel: CharacterViewModel
) {
    //获取角色名
    val flow = remember(equip.unitId) {
        characterViewModel.getCharacterBasicInfo(equip.unitId)
    }
    val basicInfo = flow.collectAsState(initial = CharacterInfo()).value ?: CharacterInfo()


    Row(
        modifier = Modifier.padding(
            top = Dimen.largePadding,
            bottom = Dimen.mediumPadding,
            start = Dimen.largePadding,
            end = Dimen.largePadding,
        )
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance().getEquipPic(equip.equipId)
        ) {
            toUniqueEquipDetail(equip.unitId)
        }

        Column {
            MainTitleText(
                text = equip.equipName,
                modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
            )

            MainCard(
                modifier = Modifier.padding(
                    start = Dimen.mediumPadding,
                    top = Dimen.mediumPadding,
                    bottom = Dimen.mediumPadding
                ),
                onClick = {
                    toUniqueEquipDetail(equip.unitId)
                }
            ) {
                Subtitle2(
                    text = equip.description.deleteSpace,
                    modifier = Modifier.padding(Dimen.mediumPadding)
                )

                Row(
                    modifier = Modifier.padding(horizontal = Dimen.mediumPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance().getMaxIconUrl(equip.unitId)
                    )

                    Column {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = Dimen.mediumPadding,
                                vertical = Dimen.smallPadding
                            )
                        ) {
                            //名称
                            Subtitle2(
                                text = equip.unitName,
                                textAlign = TextAlign.Start,
                                maxLines = 1
                            )

                        }
                        Row(
                            modifier = Modifier.padding(
                                vertical = Dimen.mediumPadding,
                                horizontal = Dimen.smallPadding
                            )
                        ) {
                            //获取方式
                            CharacterTag(
                                modifier = Modifier.padding(horizontal = Dimen.smallPadding),
                                text = getLimitTypeText(limitType = basicInfo.limitType),
                                backgroundColor = getLimitTypeColor(limitType = basicInfo.limitType),
                                style = MaterialTheme.typography.bodySmall
                            )
                            //攻击
                            CharacterTag(
                                text = getAtkText(atkType = basicInfo.atkType),
                                backgroundColor = getAtkColor(atkType = basicInfo.atkType),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

            }
        }

    }

}
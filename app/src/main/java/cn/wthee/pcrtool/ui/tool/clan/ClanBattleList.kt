package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_UNIT
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch

/**
 * 每月 BOSS 信息列表
 */
@Composable
fun ClanBattleList(
    scrollState: LazyGridState,
    toClanBossInfo: (Int, Int, Int) -> Unit,
    clanViewModel: ClanViewModel = hiltViewModel()
) {

    val clanList =
        clanViewModel.getAllClanBattleData().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        val visible = clanList.isNotEmpty()
        FadeAnimation(visible = visible) {
            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                items(
                    items = clanList,
                    key = {
                        it.clanBattleId
                    }
                ) {
                    ClanBattleItem(it, toClanBossInfo)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        FadeAnimation(visible = !visible) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                items(20) {
                    ClanBattleItem(ClanBattleInfo(), toClanBossInfo)
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.CLAN,
            text = stringResource(id = R.string.tool_clan),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (_: Exception) {
                }
            }
        }
    }

}

/**
 * 图标列表
 */
@Composable
private fun ClanBattleItem(
    clanBattleInfo: ClanBattleInfo,
    toClanBossInfo: (Int, Int, Int) -> Unit,
) {
    val placeholder = clanBattleInfo.clanBattleId == -1
    val bossUnitIdList = clanBattleInfo.unitIds.intArrayList.subList(0, 5)


    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        Row(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //日期
            MainTitleText(
                text = clanBattleInfo.getDate(),
                modifier = Modifier.placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
            )
            //阶段数
            MainTitleText(
                text = stringResource(
                    id = R.string.phase,
                    getZhNumberText(clanBattleInfo.phase)
                ),
                backgroundColor = getSectionTextColor(clanBattleInfo.phase),
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
                    .placeholder(
                        visible = placeholder,
                        highlight = PlaceholderHighlight.shimmer()
                    ),
            )
        }

        MainCard(
            modifier = Modifier
                .placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
        ) {
            //图标
            Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
                bossUnitIdList.forEachIndexed { index, it ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCompose(
                            data = ImageResourceHelper.getInstance().getUrl(ICON_UNIT, it)
                        ) {
                            if (!placeholder) {
                                toClanBossInfo(
                                    clanBattleInfo.clanBattleId,
                                    index,
                                    clanBattleInfo.phase
                                )
                            }
                        }
                        //多目标提示
                        val targetCount = clanBattleInfo.getMultiCount(index)
                        if (targetCount > 0) {
                            //阴影
                            MainText(
                                text = targetCount.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(
                                    start = Dimen.textElevation,
                                    top = Dimen.textElevation
                                )
                            )
                            MainText(
                                text = targetCount.toString(),
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }

}


/**
 * 获取团队战阶段字体颜色
 */
@Composable
fun getSectionTextColor(section: Int) = when (section) {
    1 -> colorCopper
    2 -> colorSilver
    3 -> colorGold
    4 -> colorPurple
    else -> colorRed
}


@Preview
@Composable
private fun ClanBattleItemPreview() {
    PreviewBox {
        Column {
            ClanBattleItem(
                clanBattleInfo = ClanBattleInfo(1001),
                toClanBossInfo = { _, _, _ -> })
        }
    }
}
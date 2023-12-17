package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleEvent
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.EventTitle
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime

/**
 * 公会战 item 首页预览用
 */
@Composable
fun ClanBattleOverviewItemContent(
    clanBattleEvent: ClanBattleEvent,
    toClanBossInfo: (Int, Int, Int, Int) -> Unit,
    clanBattleOverviewViewModel: ClanBattleOverviewViewModel = hiltViewModel()
) {
    val uiState by clanBattleOverviewViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(clanBattleEvent) {
        clanBattleOverviewViewModel.loadData(clanBattleEvent.id)
    }

    val clanInfo = uiState.clanInfoMap[clanBattleEvent.id]

    if (clanInfo != null) {
        ClanBattleItem(clanBattleEvent, clanInfo, toClanBossInfo)
    } else {
        ClanBattleNoBossContent(clanBattleEvent)
    }
}

/**
 * 无boss信息
 */
@Composable
private fun ClanBattleNoBossContent(clanBattleEvent: ClanBattleEvent) {
    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        Row(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding)
        ) {
            //标题
            MainTitleText(
                text = stringResource(id = R.string.tool_clan),
                modifier = Modifier.padding(end = Dimen.smallPadding),
                backgroundColor = colorOrange
            )
            //显示倒计时
            EventTitle(
                clanBattleEvent.startTime.formatTime,
                clanBattleEvent.getFixedEndTime(),
                showDays = false
            )
        }

        MainCard {
            Column(Modifier.padding(bottom = Dimen.mediumPadding)) {
                Row(
                    modifier = Modifier
                        .padding(Dimen.mediumPadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Subtitle1(text = stringResource(id = R.string.tool_clan_no_boss))
                }

                //结束日期
                CaptionText(
                    text = clanBattleEvent.getFixedEndTime().fixJpTime,
                    modifier = Modifier
                        .padding(end = Dimen.mediumPadding)
                        .fillMaxWidth()
                )
            }

        }
    }
}
package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleEvent
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.EventTitle
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime


/**
 * 公会战 item 首页预览用
 */
@Composable
fun ClanBattleOverviewItemContent(
    clanBattleEvent: ClanBattleEvent,
    toClanBossInfo: (String) -> Unit,
) {
    if (clanBattleEvent.clanBattleInfo != null) {
        ClanBattleItem(
            clanBattleEvent = clanBattleEvent,
            clanBattleInfo = clanBattleEvent.clanBattleInfo!!,
            toClanBossInfo = toClanBossInfo
        )
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
                    .padding(end = Dimen.mediumPadding, bottom = Dimen.mediumPadding)
                    .fillMaxWidth()
            )
        }
    }
}


@CombinedPreviews
@Composable
private fun Preview() {
    PreviewLayout {
        ClanBattleOverviewItemContent(
            clanBattleEvent = ClanBattleEvent(
                clanBattleInfo = ClanBattleInfo(1)
            ),
            toClanBossInfo = {}
        )
        ClanBattleOverviewItemContent(
            clanBattleEvent = ClanBattleEvent(),
            toClanBossInfo = {}
        )
    }
}
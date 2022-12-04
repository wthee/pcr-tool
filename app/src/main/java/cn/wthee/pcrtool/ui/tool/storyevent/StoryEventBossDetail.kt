package cn.wthee.pcrtool.ui.tool.storyevent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.common.SelectTypeCompose
import cn.wthee.pcrtool.ui.tool.enemy.EnemyDetail


/**
 * 剧情活动 boss 信息
 */
@Composable
fun StoryEventBossDetail(
    enemyId: Int,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit)
) {

    val modeIndex = remember {
        mutableStateOf(0)
    }

    //页面
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        //图标列表
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EnemyDetail(
                enemyId + modeIndex.value,
                toSummonDetail,
            )
        }

        //阶段选择
        val tabs = arrayListOf<String>()
        for (i in 1..3) {
            tabs.add(stringResource(id = R.string.mode, i))
        }

        SelectTypeCompose(
            icon = MainIconType.CLAN_SECTION,
            tabs = tabs,
            type = modeIndex,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
        )

    }
}
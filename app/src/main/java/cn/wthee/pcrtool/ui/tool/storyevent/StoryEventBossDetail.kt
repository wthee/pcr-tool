package cn.wthee.pcrtool.ui.tool.storyevent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.tool.enemy.EnemyDetailScreen


/**
 * 剧情活动 boss 信息
 */
@Composable
fun StoryEventBossDetail(
    enemyId: Int,
    toSummonDetail: ((Int, Int, Int, Int, Int) -> Unit),
    storyEventBossViewModel: StoryEventBossViewModel = hiltViewModel()
) {

    val uiState by storyEventBossViewModel.uiState.collectAsStateWithLifecycle()

    MainScaffold(
        fabWithCustomPadding = {
            //mode选择
            val tabs = arrayListOf<String>()
            for (i in 1..3) {
                tabs.add(stringResource(id = R.string.mode, i))
            }
            SelectTypeFab(
                icon = MainIconType.CLAN_SECTION,
                tabs = tabs,
                type = uiState.modeIndex,
                openDialog = uiState.openDialog,
                changeDialog = storyEventBossViewModel::changeDialog,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding(),
                changeSelect = storyEventBossViewModel::changeSelect
            )
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.openDialog) {
                storyEventBossViewModel.changeDialog(false)
            } else {
                navigateUp()
            }
        },
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            storyEventBossViewModel.changeDialog(false)
        }
    ) {
        //图标列表
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EnemyDetailScreen(
                enemyId + uiState.modeIndex,
                toSummonDetail,
            )
        }
    }
}
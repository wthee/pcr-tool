package cn.wthee.pcrtool.ui.character.story

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterStoryAttr
import cn.wthee.pcrtool.data.db.view.getAttr
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper

/**
 * 角色剧情属性详情
 */
@Composable
fun CharacterStoryAttrScreen(characterStoryViewModel: CharacterStoryViewModel = hiltViewModel()) {
    val uiState by characterStoryViewModel.uiState.collectAsStateWithLifecycle()

    MainScaffold {
        StateBox(stateType = uiState.loadingState) {
            CharacterStoryAttrContent(storyMap = uiState.storyMap)
        }
    }
}

@Composable
private fun CharacterStoryAttrContent(storyMap: HashMap<Int, List<CharacterStoryAttr>>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(
            rememberScrollState()
        )
    ) {
        for ((key, value) in storyMap) {
            StoryAttrItemContent(key, value)
        }
        CommonSpacer()
    }
}

/**
 * 剧情属性
 */
@Composable
private fun StoryAttrItemContent(key: Int, attrList: List<CharacterStoryAttr>) {

    Column(
        modifier = Modifier.padding(vertical = Dimen.largePadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance().getMaxIconUrl(key * 100 + 1)
        )
        if (attrList.firstOrNull()?.subTitle == "") {
            Subtitle1(text = stringResource(id = R.string.unknown_character))
        }
        Column {
            attrList.forEach {
                Column(
                    modifier = Modifier.padding(vertical = Dimen.mediumPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (it.subTitle != "") {
                        //标题
                        CaptionText(text = it.getFixedTitle(), textAlign = TextAlign.Center)
                        //剧情名
                        Subtitle1(text = it.subTitle, selectable = true)
                    }
                    AttrList(
                        attrs = it.getAttr().allNotZero(LocalContext.current)
                    )
                }
            }
        }
    }

}

@CombinedPreviews
@Composable
private fun CharacterStoryAttrContentPreview() {
    val subTitle = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        CharacterStoryAttrContent(
            hashMapOf<Int, List<CharacterStoryAttr>>().apply {
                put(
                    1, arrayListOf(
                        CharacterStoryAttr(
                            title = stringResource(id = R.string.debug_short_text),
                            subTitle = subTitle,
                            status_rate_1 = 1,
                            status_rate_2 = 2,
                        )
                    )
                )
                put(
                    2, arrayListOf(
                        CharacterStoryAttr(
                            title = stringResource(id = R.string.debug_long_text),
                            subTitle = subTitle,
                            status_rate_1 = 1,
                            status_rate_2 = 2,
                        )
                    )
                )
            }
        )
    }
}
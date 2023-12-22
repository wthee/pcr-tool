package cn.wthee.pcrtool.ui.character.story

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
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
            uiState.storyAttrList?.let {
                CharacterStoryAttrContent(storyAttrList = it)
            }
        }
    }

}

@Composable
private fun CharacterStoryAttrContent(storyAttrList: List<CharacterStoryAttr>){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
            rememberScrollState()
        )
    ) {
        for ((key, value) in groupStory(storyAttrList)) {
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
                        CaptionText(text = it.getFixedTitle())
                        //剧情名
                        Subtitle1(text = it.subTitle, selectable = true)
                    }
                    AttrList(
                        attrs = it.getAttr().allNotZero(isPreview = LocalInspectionMode.current)
                    )
                }
            }
        }
    }

}

/**
 * 分组
 */
private fun groupStory(list: List<CharacterStoryAttr>): HashMap<Int, List<CharacterStoryAttr>> {
    val map = hashMapOf<Int, List<CharacterStoryAttr>>()
    list.forEach {
        val key = it.storyId / 1000
        if (map[key] == null) {
            map[key] = list.filter { data -> data.storyId / 1000 == key }
        } else {
            return@forEach
        }
    }
    return map
}

@CombinedPreviews
@Composable
private fun CharacterStoryAttrContentPreview() {
    val title = stringResource(id = R.string.debug_short_text)
    val subTitle = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        CharacterStoryAttrContent(
            arrayListOf(
                CharacterStoryAttr(
                    title = title,
                    subTitle = subTitle,
                    status_rate_1 = 1,
                    status_rate_2 = 2,
                )
            )
        )
    }
}
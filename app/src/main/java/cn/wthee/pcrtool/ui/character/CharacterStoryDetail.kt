package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterStoryAttr
import cn.wthee.pcrtool.data.db.view.getAttr
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel

/**
 * 角色剧情属性详情
 */
@Composable
fun CharacterStoryDetail(unitId: Int, attrViewModel: CharacterAttrViewModel = hiltViewModel()) {
    val attrDetailFlow = remember(unitId) {
        attrViewModel.getStoryAttrDetail(unitId)
    }
    val list by attrDetailFlow.collectAsState(initial = arrayListOf())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
                rememberScrollState()
            )
        ) {
            for ((key, value) in groupStory(list)) {
                StoryDetailItem(key, value)
            }
            CommonSpacer()
        }
    }
}

/**
 * 剧情属性
 */
@Composable
private fun StoryDetailItem(key: Int, attrList: List<CharacterStoryAttr>) {

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
private fun StoryDetailItemPreview() {
    val title = stringResource(id = R.string.debug_short_text)
    val subTitle = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        StoryDetailItem(
            1,
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
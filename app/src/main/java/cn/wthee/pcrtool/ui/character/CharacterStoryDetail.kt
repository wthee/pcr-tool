package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterStoryAttr
import cn.wthee.pcrtool.data.db.view.getAttr
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel

/**
 * 角色剧情属性详情
 */
@Composable
fun CharacterStoryDetail(unitId: Int, attrViewModel: CharacterAttrViewModel = hiltViewModel()) {

    val list =
        attrViewModel.getStoryAttrDetail(unitId).collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
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
        IconCompose(
            data = ImageResourceHelper.getInstance().getMaxIconUrl(key * 100 + 1)
        )
        Column {
            attrList.forEach {
                Column(
                    modifier = Modifier.padding(vertical = Dimen.mediumPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //标题
                    CaptionText(text = it.getFixedTitle())
                    //剧情名
                    Subtitle1(text = it.subTitle, selectable = true)
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
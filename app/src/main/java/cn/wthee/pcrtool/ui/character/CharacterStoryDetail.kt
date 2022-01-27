package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.db.view.CharacterStoryAttr
import cn.wthee.pcrtool.data.db.view.getAttr
import cn.wthee.pcrtool.ui.common.AttrList
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel

/**
 * 角色剧情属性详情
 */
@ExperimentalMaterialApi
@Composable
fun CharacterStoryDetail(unitId: Int, attrViewModel: CharacterAttrViewModel = hiltViewModel()) {

    val list =
        attrViewModel.getStoryAttrDetail(unitId).collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            for ((key, value) in groupStory(list)) {
                val id = key * 100 + 1
                item {
                    IconCompose(
                        modifier = Modifier.padding(top = Dimen.largePadding),
                        data = ImageResourceHelper.getInstance().getMaxIconUrl(id)
                    )
                }
                items(value) {
                    StoryDetailItem(it)
                }
            }
            item {
                CommonSpacer()
            }
        }
    }
}

/**
 * 剧情属性
 */
@ExperimentalMaterialApi
@Composable
private fun StoryDetailItem(data: CharacterStoryAttr) {

    Column(
        modifier = Modifier.padding(top = Dimen.largePadding, bottom = Dimen.mediumPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //标题
        MainText(text = data.storyName)
        AttrList(attrs = data.getAttr().allNotZero())
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

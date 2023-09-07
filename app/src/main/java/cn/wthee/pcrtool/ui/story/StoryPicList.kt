package cn.wthee.pcrtool.ui.story

import android.Manifest
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonResponseBox
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainAlertDialog
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SubImage
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageDownloadHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.checkPermissions
import cn.wthee.pcrtool.viewmodel.AllPicsViewModel

//权限
val permissions = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
)


/**
 * 角色/活动剧情图片
 */
@Composable
fun StoryPicList(
    id: Int,
    allPicsType: AllPicsType,
    picsViewModel: AllPicsViewModel = hiltViewModel()
) {
    //角色卡面
    val basicUrls = if (allPicsType == AllPicsType.CHARACTER) {
        picsViewModel.getUniCardList(id).collectAsState(initial = arrayListOf()).value
    } else {
        arrayListOf()
    }
    //剧情活动
    val flow = remember(id, allPicsType.type) {
        picsViewModel.getStoryList(id, allPicsType.type)
    }
    val responseData = flow.collectAsState(initial = null).value
    val hasStory = responseData?.data?.isNotEmpty() == true


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            //角色
            if (allPicsType == AllPicsType.CHARACTER) {
                CharacterPicList(basicUrls)
            }
            //剧情
            StoryPicList(hasStory, responseData)
            CommonSpacer()
        }
    }


}

/**
 * 剧情图片
 */
@Composable
private fun StoryPicList(
    hasStory: Boolean,
    responseData: ResponseData<ArrayList<String>>?
) {
    Row(
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding
            )
            .fillMaxWidth(),
    ) {
        MainTitleText(
            text = stringResource(id = R.string.story)
        )
        Spacer(modifier = Modifier.weight(1f))
        if (hasStory) {
            MainText(text = responseData!!.data!!.size.toString())
        }
    }
    CommonResponseBox(responseData) { data ->
        if (data.isNotEmpty()) {
            CardGridList(
                urls = data
            )
        } else {
            CenterTipText(text = stringResource(id = R.string.no_story_info))
        }
    }

}

/**
 * 角色图片
 */
@Composable
private fun CharacterPicList(
    basicUrls: java.util.ArrayList<String>
) {
    Row(
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTitleText(text = stringResource(id = R.string.basic))
        Spacer(modifier = Modifier.weight(1f))
        MainText(text = basicUrls.size.toString())
    }
    CardGridList(
        urls = basicUrls
    )
}

/**
 * 图片网格列表
 */
@Composable
private fun CardGridList(
    urls: ArrayList<String>
) {
    val context = LocalContext.current
    val unLoadToast = stringResource(id = R.string.wait_pic_load)

    VerticalGrid(itemWidth = getItemWidth()) {

        urls.forEach { picUrl ->
            val openDialog = remember { mutableStateOf(false) }
            val loadedPic: MutableState<Drawable?> = remember { mutableStateOf(null) }
            val loading = remember {
                mutableStateOf(true)
            }

            MainCard(
                modifier = Modifier
                    .padding(horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding),
                onClick = {
                    //加载完成，下载
                    if (!loading.value) {
                        //权限校验
                        checkPermissions(context, permissions) {
                            openDialog.value = true
                        }
                    } else {
                        ToastUtil.short(unLoadToast)
                    }
                }
            ) {
                //图片
                SubImage(
                    data = picUrl,
                    loading = loading,
                    contentScale = ContentScale.FillWidth
                ) {
                    //获取本地原图缓存
                    loadedPic.value = it
                }
            }

            //下载确认
            MainAlertDialog(
                openDialog = openDialog,
                icon = MainIconType.PREVIEW_IMAGE,
                title = stringResource(R.string.title_dialog_save_img),
                text = stringResource(R.string.tip_save_image),
                onDismissRequest = {
                    openDialog.value = false
                }
            ) {
                loadedPic.value.let {
                    ImageDownloadHelper(context).saveBitmap(
                        bitmap = (it as BitmapDrawable).bitmap,
                        displayName = "${getFileName(picUrl)}.jpg"
                    )
                    openDialog.value = false
                }
            }
        }
    }

}

/**
 * 获取文件名
 */
private fun getFileName(url: String): String {
    return try {
        val type = when {
            url.contains("story") -> "story"
            url.contains("actual_profile") -> "unit_actual"
            else -> "unit"
        } + "_"
        type + url.split('/').last().split('.')[0]
    } catch (e: Exception) {
        System.currentTimeMillis().toString()
    }
}


@CombinedPreviews
@Composable
private fun CharacterPicListPreview() {
    PreviewLayout {
        CharacterPicList(arrayListOf("1"))
    }
}
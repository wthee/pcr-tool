package cn.wthee.pcrtool.ui.picture

import android.Manifest
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
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

//权限
val permissions = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
)


/**
 * 角色/活动剧情图片
 *
 */
@Composable
fun PictureScreen(
    picsViewModel: PictureViewModel = hiltViewModel()
) {
    val uiState by picsViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            //角色
            if (uiState.unitCardList.isNotEmpty()) {
                CharacterPictureContent(uiState.unitCardList)
            }
            //剧情
            StoryPictureContent(uiState.hasStory, uiState.isLoadingStory, uiState.storyCardList)
            CommonSpacer()
        }
    }


}

/**
 * 剧情图片
 */
@Composable
private fun StoryPictureContent(
    hasStory: Boolean,
    isLoadingStory: Boolean,
    storyCardList: ArrayList<String>
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
            MainText(text = storyCardList.size.toString())
        }
    }
    if(isLoadingStory){
        Box(modifier = Modifier.fillMaxSize()){
            CircularProgressCompose(
                modifier = Modifier
                    .padding(vertical = Dimen.largePadding)
                    .align(Alignment.Center)
            )
        }
    }else{
        if (storyCardList.isNotEmpty()) {
            CardGridList(
                urls = storyCardList
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
private fun CharacterPictureContent(
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
        CharacterPictureContent(arrayListOf("1"))
    }
}
package cn.wthee.pcrtool.ui.media

import android.Manifest
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainAlertDialog
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.SubImage
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.MediaDownloadHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.checkPermissions
import cn.wthee.pcrtool.utils.getString
import java.io.File

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

    MainScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            //角色
            if (uiState.unitCardList.isNotEmpty()) {
                MediaGridList(
                    urlList = uiState.unitCardList,
                    title = stringResource(id = R.string.basic)
                ) {
                    PictureItem(it)
                }
            }
            //剧情
            MediaGridList(
                urlList = uiState.storyCardList,
                loading = uiState.isLoadingStory,
                title = stringResource(id = R.string.basic),
                noDataTipText = stringResource(id = R.string.no_story_info)
            ) {
                PictureItem(it)
            }
            CommonSpacer()
        }
    }
}


/**
 * 图片
 */
@Composable
fun PictureItem(picUrl: String) {
    val context = LocalContext.current

    val openDialog = remember(picUrl) {
        mutableStateOf(false)
    }
    val loadedPic: MutableState<Drawable?> = remember(picUrl) {
        mutableStateOf(null)
    }
    val loading = remember(picUrl) {
        mutableStateOf(true)
    }

    val displayName = getFileName(picUrl)
    val path = MediaDownloadHelper.getSaveDir(isVideo = false)
    val file = File("$path/$displayName")
    //是否已存在
    var saved by remember {
        mutableStateOf(false)
    }
    if (file.exists()) {
        saved = true
    }
    //未加载提示
    val unLoadToast = stringResource(id = R.string.wait_pic_load)

    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        ),
        onClick = {
            //加载完成，下载
            //权限校验
            checkPermissions(context, permissions) {
                //已保存
                if (saved) {
                    ToastUtil.short(
                        getString(
                            R.string.pic_exist,
                            file.absolutePath.replace(MediaDownloadHelper.DIR, "")
                        )
                    )
                    return@checkPermissions
                }
                if (loading.value) {
                    ToastUtil.short(unLoadToast)
                } else {
                    openDialog.value = true
                }
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
        icon = MainIconType.DOWNLOAD,
        title = stringResource(R.string.title_dialog_save_img),
        text = stringResource(R.string.tip_save_to_gallery),
        onDismissRequest = {
            openDialog.value = false
        }
    ) {
        loadedPic.value.let {
            MediaDownloadHelper(context).saveMedia(
                bitmap = (it as BitmapDrawable).bitmap,
                displayName = displayName
            )
            openDialog.value = false
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
        type + url.split('/').last().split('.')[0] + ".jpg"
    } catch (e: Exception) {
        System.currentTimeMillis().toString()
    }
}

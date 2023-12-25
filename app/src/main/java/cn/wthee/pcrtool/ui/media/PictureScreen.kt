package cn.wthee.pcrtool.ui.media

import android.Manifest
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.SubImage
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.utils.MediaDownloadHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.checkPermissions
import cn.wthee.pcrtool.utils.getString
import kotlinx.coroutines.launch
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
        PictureScreenContent(uiState)
    }
}

/**
 * 角色/活动剧情图片
 *
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PictureScreenContent(uiState: PictureUiState) {
    val pagerState = rememberPagerState {
        uiState.pageCount
    }
    val storyCount = when (uiState.storyLoadState) {
        LoadingState.Success -> uiState.storyCardList.size
        LoadingState.NoData -> 0
        else -> null
    }
    val comicCount = when (uiState.comicLoadState) {
        LoadingState.Success -> uiState.comicList.size
        LoadingState.NoData -> 0
        else -> null
    }

    val tabs = if (uiState.pageCount == 1) {
        //剧情活动
        arrayListOf(
            TabData(
                tab = stringResource(id = R.string.story),
                count = storyCount,
                isLoading = uiState.storyLoadState == LoadingState.Loading
            )
        )
    } else {
        //角色
        arrayListOf(
            TabData(
                tab = stringResource(id = R.string.basic),
                count = uiState.unitCardList.size
            ),
            TabData(
                tab = stringResource(id = R.string.story),
                count = storyCount,
                isLoading = uiState.storyLoadState == LoadingState.Loading
            ),
            TabData(
                tab = stringResource(id = R.string.comic),
                count = comicCount,
                isLoading = uiState.comicLoadState == LoadingState.Loading
            )
        )
    }

    Column {
        if (uiState.pageCount > 1) {
            MainTabRow(
                pagerState = pagerState,
                tabs = tabs,
                modifier = Modifier
                    .fillMaxWidth(RATIO_GOLDEN)
                    .align(Alignment.CenterHorizontally)
            )
        }

        HorizontalPager(state = pagerState) { index ->

            when (tabs[index].tab) {
                stringResource(id = R.string.basic) -> {
                    MediaGridList(
                        urlList = uiState.unitCardList,
                        title = tabs[index].tab,
                        showTitle = false
                    ) {
                        PictureItem(
                            picUrl = it,
                            modifier = Modifier
                                .padding(
                                    horizontal = Dimen.largePadding,
                                    vertical = Dimen.mediumPadding
                                )
                        )
                    }
                }

                stringResource(id = R.string.story) -> {
                    MediaGridList(
                        urlList = uiState.storyCardList,
                        title = tabs[index].tab,
                        loading = uiState.storyLoadState,
                        showTitle = uiState.pageCount == 1
                    ) {
                        PictureItem(
                            picUrl = it,
                            modifier = Modifier
                                .padding(
                                    horizontal = Dimen.largePadding,
                                    vertical = Dimen.mediumPadding
                                )
                        )
                    }
                }

                stringResource(id = R.string.comic) -> {
                    MediaGridList(
                        urlList = uiState.comicList,
                        title = tabs[index].tab,
                        loading = uiState.comicLoadState,
                        showTitle = false
                    ) {
                        PictureItem(
                            picUrl = it,
                            modifier = Modifier
                                .padding(
                                    horizontal = Dimen.largePadding,
                                    vertical = Dimen.mediumPadding
                                )
                        )
                    }
                }
            }
        }
    }
}


/**
 * 图片
 *
 * @param picUrl 图片链接
 * @param ratio 图片比例
 */
@Composable
fun PictureItem(
    modifier: Modifier = Modifier,
    picUrl: String,
    ratio: Float? = null,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    contentScale: ContentScale = ContentScale.FillWidth
) {
    val context = LocalContext.current
    val placeholder = picUrl == ""

    //未加载提示
    val unLoadToast = stringResource(R.string.wait_pic_load)
    //drawable
    val loadedPic: MutableState<Drawable?> = remember(picUrl) {
        mutableStateOf(null)
    }
    //加载中
    val loading = remember(picUrl) {
        mutableStateOf(true)
    }
    //预览弹窗
    val openPreviewDialog = remember(picUrl) {
        mutableStateOf(false)
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

    //图片
    SubImage(
        data = picUrl,
        loading = loading,
        contentScale = contentScale,
        ratio = ratio,
        modifier = modifier
            .clip(shape)
            .shadow(elevation = Dimen.cardElevation, shape = shape)
            .clickable(!placeholder) {
                //预览
                VibrateUtil(context).single()
                openPreviewDialog.value = true
            }
            .commonPlaceholder(placeholder)
    ) {
        //获取本地原图缓存
        loadedPic.value = it
    }

    //预览
    if (openPreviewDialog.value) {
        PreviewPictureDialog(
            openPreviewDialog = openPreviewDialog,
            loading = loading,
            picUrl = picUrl,
            ratio = ratio,
            saved = saved
        ) {
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
                    loadedPic.value.let {
                        MediaDownloadHelper(context).saveMedia(
                            bitmap = (it as BitmapDrawable).bitmap,
                            displayName = displayName
                        ) {
                            saved = true
                        }
                    }
                }
            }
        }
    }

}

/**
 * 图片预览弹窗
 * fixme 小窗模式底部显示异常
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PreviewPictureDialog(
    openPreviewDialog: MutableState<Boolean>,
    loading: MutableState<Boolean>,
    saved: Boolean,
    picUrl: String,
    ratio: Float?,
    toSave: () -> Unit
) {
    val scope = rememberCoroutineScope()
    //手势操作
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState =
        rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            scale *= zoomChange
            rotation += rotationChange
            offset += offsetChange * scale
        }

    AlertDialog(
        onDismissRequest = {
            openPreviewDialog.value = false
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .navigationBarsPadding()
    ) {
        MainScaffold(
            contentAlignment = Alignment.Center,
            fab = {
                //重置
                FadeAnimation(scale != 1f || rotation != 0f || offset != Offset.Zero) {
                    MainSmallFab(
                        iconType = MainIconType.RESET
                    ) {
                        scope.launch {
                            transformableState.transform {
                                transformBy()
                            }
                            scale = 1f
                            rotation = 0f
                            offset = Offset.Zero
                        }
                    }
                }
                //保存
                MainSmallFab(
                    iconType = if (saved) MainIconType.DOWNLOAD_DONE else MainIconType.DOWNLOAD,
                    text = stringResource(
                        id = if (saved) {
                            R.string.saved
                        } else {
                            R.string.title_dialog_save_img
                        }
                    )
                ) {
                    toSave()
                }
            },
            onMainFabClick = {
                openPreviewDialog.value = false
            },
            enableClickClose = openPreviewDialog.value,
            onCloseClick = {
                openPreviewDialog.value = false
            },
            backgroundColor = Color.Transparent,
            modifier = Modifier.navigationBarsPadding()
        ) {
            //预览功能，手势操作
            SubImage(
                data = picUrl,
                loading = loading,
                contentScale = ContentScale.FillWidth,
                ratio = ratio,
                modifier = Modifier
                    .widthIn(max = Dimen.itemMaxWidth)
                    .padding(horizontal = Dimen.largePadding)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        rotationZ = rotation,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(state = transformableState)
                    //置于最上层
                    .zIndex(99f)
            )
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
            url.contains("comic") -> "comic"
            //b站动态图片
            url.contains("bfs") -> "info"
            else -> "unit"
        } + "_"
        type + url.split('/').last().split('.')[0] + ".jpg"
    } catch (e: Exception) {
        System.currentTimeMillis().toString()
    }
}
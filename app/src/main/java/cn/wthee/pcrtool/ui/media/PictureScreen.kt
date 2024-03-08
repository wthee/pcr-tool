package cn.wthee.pcrtool.ui.media

import android.Manifest
import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerBasedShape
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.RATIO
import cn.wthee.pcrtool.ui.components.RATIO_BANNER
import cn.wthee.pcrtool.ui.components.RATIO_TEASER
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.ui.theme.noShape
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.MediaDownloadHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.checkPermissions
import cn.wthee.pcrtool.utils.getString
import coil3.BitmapImage
import coil3.annotation.ExperimentalCoilApi
import kotlinx.coroutines.launch
import java.io.File

//权限
val permissions = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
)


/**
 * 角色、活动剧情、过场漫画图片
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
 * 角色、活动剧情、过场漫画图片
 *
 */
@Composable
private fun PictureScreenContent(uiState: PictureUiState) {
    val pagerState = rememberPagerState {
        uiState.pageCount
    }
    val storyCount = when (uiState.storyLoadState) {
        LoadState.Success -> uiState.storyCardList.size
        LoadState.NoData -> 0
        else -> null
    }
    val comicCount = when (uiState.comicLoadState) {
        LoadState.Success -> uiState.comicList.size
        LoadState.NoData -> 0
        else -> null
    }

    val tabs = if (uiState.pageCount == 2) {
        //剧情活动
        arrayListOf(
            TabData(
                tab = stringResource(id = R.string.story),
                count = storyCount,
                isLoading = uiState.storyLoadState == LoadState.Loading
            ),
            TabData(
                tab = stringResource(id = R.string.other),
                count = uiState.bannerList.size
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
                isLoading = uiState.storyLoadState == LoadState.Loading
            ),
            TabData(
                tab = stringResource(id = R.string.comic),
                count = comicCount,
                isLoading = uiState.comicLoadState == LoadState.Loading
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
                        loadState = uiState.storyLoadState,
                        showTitle = false,
                        noDataText = stringResource(id = R.string.no_story_info)
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
                        loadState = uiState.comicLoadState,
                        showTitle = false,
                        noDataText = stringResource(id = R.string.no_comic_info)
                    ) {
                        PictureItem(
                            picUrl = it,
                            modifier = Modifier
                                .padding(
                                    horizontal = Dimen.largePadding,
                                    vertical = Dimen.mediumPadding
                                ),
                            shape = noShape(),
                            ratio = 1f
                        )
                    }
                }
                //剧情活动 banner
                stringResource(id = R.string.other) -> {
                    MediaGridList(
                        urlList = uiState.bannerList,
                        title = tabs[index].tab,
                        loadState = LoadState.Success,
                        showTitle = false
                    ) {
                        val isBanner = it.contains(ImageRequestHelper.EVENT_BANNER)
                        PictureItem(
                            picUrl = it,
                            modifier = Modifier
                                .padding(
                                    horizontal = Dimen.largePadding,
                                    vertical = Dimen.mediumPadding
                                ),
                            contentScale = if (isBanner) {
                                ContentScale.FillWidth
                            } else {
                                ContentScale.Fit
                            },
                            showShadow = !isBanner,
                            ratio = if (isBanner) {
                                RATIO_BANNER
                            } else {
                                RATIO_TEASER
                            },
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
    contentScale: ContentScale = ContentScale.FillWidth,
    showShadow: Boolean = true,
) {
    val context = LocalContext.current
    val placeholder = picUrl == ""

    //预览弹窗
    val openPreviewDialog = remember(picUrl) {
        mutableStateOf(false)
    }

    //图片
    MainImage(
        data = picUrl,
        contentScale = contentScale,
        ratio = ratio,
        modifier = modifier
            .clip(shape)
            .then(
                if (showShadow) {
                    Modifier.shadow(elevation = Dimen.cardElevation, shape = shape)
                } else {
                    Modifier
                }
            )
            .clickable(!placeholder) {
                //预览
                VibrateUtil(context).single()
                openPreviewDialog.value = true
            }
            .placeholder(placeholder, shape)
    )

    //预览
    if (openPreviewDialog.value) {
        PreviewPictureDialog(
            openPreviewDialog = openPreviewDialog,
            picUrl = picUrl,
            ratio = ratio
        )
    }
}

/**
 * 图片预览弹窗
 * fixme 小窗模式底部显示异常（dialog导致问题）
 */
@Composable
@OptIn(ExperimentalCoilApi::class)
private fun PreviewPictureDialog(
    openPreviewDialog: MutableState<Boolean>,
    picUrl: String,
    ratio: Float?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    //加载完成bitmap
    val loadedPic: MutableState<Bitmap?> = remember(picUrl) {
        mutableStateOf(null)
    }
    //加载成功
    var success by remember(picUrl) {
        mutableStateOf(false)
    }
    //文件名
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

    //未加载提示
    val unLoadToast = stringResource(R.string.wait_pic_load)

    Dialog(
        onDismissRequest = {
            openPreviewDialog.value = false
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        MainScaffold(
            modifier = Modifier.navigationBarsPadding(),
            contentAlignment = Alignment.Center,
            fab = {
                //重置
                FadeAnimation(scale != 1f || rotation != 0f || offset != Offset.Zero) {
                    MainSmallFab(
                        iconType = MainIconType.RESET,
                        onClick = {
                            scope.launch {
                                transformableState.transform {
                                    transformBy()
                                }
                                scale = 1f
                                rotation = 0f
                                offset = Offset.Zero
                            }
                        }
                    )
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
                    ),
                    onClick = {
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
                            if (!success) {
                                ToastUtil.short(unLoadToast)
                            } else {
                                MediaDownloadHelper(context).saveMedia(
                                    bitmap = loadedPic.value,
                                    displayName = displayName
                                ) {
                                    saved = true
                                }
                            }
                        }
                    }
                )
            },
            onMainFabClick = {
                openPreviewDialog.value = false
            },
            enableClickClose = openPreviewDialog.value,
            onCloseClick = {
                openPreviewDialog.value = false
            },
            backgroundColor = Color.Transparent
        ) {
            //预览功能，手势操作
            MainImage(
                data = picUrl,
                ratio = ratio,
                modifier = Modifier
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
                    .padding(horizontal = Dimen.mediumPadding)
                    .width(Dimen.itemMaxWidth)
                    .clickable {
                        VibrateUtil(context).single()
                        openPreviewDialog.value = false
                    },
            ) {
                //获取本地原图缓存
                loadedPic.value = (it.image as BitmapImage).bitmap
                success = true
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
            url.contains(ImageRequestHelper.CARD_STORY) -> "story"
            url.contains(ImageRequestHelper.EVENT_BANNER) -> "banner"
            url.contains(ImageRequestHelper.EVENT_TEASER) -> "teaser"
            url.contains(ImageRequestHelper.CARD_ACTUAL_PROFILE) -> "unit_actual"
            url.contains(ImageRequestHelper.COMIC) -> "comic"
            url.contains(ImageRequestHelper.COMIC_ZH) -> "comic"
            //b站动态图片
            url.contains("bfs") -> "info"
            else -> "unit"
        } + "_"
        type + url.split('/').last().split('.')[0] + ".jpg"
    } catch (e: Exception) {
        System.currentTimeMillis().toString()
    }
}


@CombinedPreviews
@Composable
private fun PictureScreenContentPreview() {
    PreviewLayout {
        PictureScreenContent(
            uiState = PictureUiState(
                pageCount = 3,
                unitCardList = arrayListOf("1"),
                storyCardList = arrayListOf("1"),
                comicList = arrayListOf("1"),
                storyLoadState = LoadState.Success,
                comicLoadState = LoadState.Success,
            )
        )
    }
}

@CombinedPreviews
@Composable
private fun DialogPreview() {
    PreviewLayout {
        PreviewPictureDialog(
            openPreviewDialog = remember {
                mutableStateOf(true)
            },
            picUrl = "",
            ratio = RATIO
        )
    }
}
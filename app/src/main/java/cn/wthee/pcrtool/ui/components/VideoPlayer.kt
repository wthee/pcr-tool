package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.VideoType
import cn.wthee.pcrtool.navigation.navigateUpSheet
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.VideoDownloadHelper
import cn.wthee.pcrtool.utils.getString
import kotlinx.coroutines.launch
import java.io.File

/**
 * 视频播放页面
 *
 * @param unitId 角色编号
 * @param videoTypeValue 视频类型 [cn.wthee.pcrtool.data.enums.VideoType]
 */
@Composable
fun VideoPlayerScreen(
    unitId: Int,
    videoTypeValue: Int,
) {
    val videoType = VideoType.getByValue(videoTypeValue)
    val urlList = ImageRequestHelper.getInstance().getMovieUrlList(unitId, videoType)
    val typeName = stringResource(id = videoType.typeName)
    val ratio = when (videoType) {
        VideoType.UB_SKILL -> 960f / 720
        else -> RATIO
    }

    MainScaffold {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
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
                MainTitleText(text = typeName)
                Spacer(modifier = Modifier.weight(1f))
                MainText(text = urlList.size.toString())
            }
            VerticalGrid(itemWidth = getItemWidth()) {
                urlList.forEach {
                    VideoPlayer(url = it, ratio = ratio, videoTypeValue = videoTypeValue)
                }
            }
        }
    }

}

/**
 * 视频播放
 * fixme 进度显示
 *
 * @param url 视频链接
 * @param ratio 比例
 */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun VideoPlayer(
    url: String,
    ratio: Float,
    videoTypeValue: Int,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    //加载中
    var loading by remember(url) {
        mutableStateOf(true)
    }
    //播放倍速
    val speedList = listOf(0.25f, 0.5f, 1f, 2f, 4f)
    var speed by remember(url) {
        mutableFloatStateOf(1f)
    }
    //播放倍速选择列表
    var speedExpend by remember(url) {
        mutableStateOf(false)
    }
    //播放错误
    var playError by remember(url) {
        mutableStateOf(false)
    }
    //下载状态
    var downloading by remember(url) {
        mutableStateOf(false)
    }
    //下载弹窗
    val openDialog = remember { mutableStateOf(false) }
    //是否已存在
    var saved by remember {
        mutableStateOf(false)
    }

    //判断是否已存在
    val fileName = "${videoTypeValue}_" + url.split("/").last()
    val targetFile = VideoDownloadHelper.getSaveDir() + File.separator + fileName
    if (File(targetFile).exists()) {
        saved = true
    }

    //视频源
    val mediaSource = ProgressiveMediaSource.Factory(
        CacheDataSource.Factory()
            .setCache(MyApplication.simpleCache)
            .setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory()
            )
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    ).createMediaSource(MediaItem.fromUri(url))

    //播放器
    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaSource(mediaSource)
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ALL
                //加载中监听
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        loading = !isPlaying
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        loading = false
                        playError = true
                    }
                })
            }
    }

    //关闭时释放资源
    LifecycleEffect(Lifecycle.Event.ON_PAUSE) {
        exoPlayer.release()
        scope.launch {
            navigateUpSheet()
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding)
            .fillMaxWidth()
    ) {
        MainCard {
            Box(
                contentAlignment = Alignment.Center
            ) {
                //播放器
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            useController = false
                            player = exoPlayer
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = ratio)
                )
                //加载中
                if (loading) {
                    CircularProgressCompose()
                }
                //播放出错
                if (playError) {
                    MainTitleText(text = stringResource(R.string.play_error))
                }
            }
        }

        //功能按钮
        Row(modifier = Modifier.align(Alignment.End)) {
            //倍速列表
            PCRToolComposeTheme(shapes = MaterialTheme.shapes.copy(extraSmall = MaterialTheme.shapes.medium)) {
                DropdownMenu(
                    expanded = speedExpend,
                    onDismissRequest = {
                        speedExpend = false
                    }
                ) {
                    //倍速可选项
                    speedList.forEach {
                        DropdownMenuItem(
                            leadingIcon = {
                                if (speed == it) {
                                    MainIcon(
                                        data = MainIconType.OK,
                                        size = Dimen.smallIconSize
                                    )
                                }
                            },
                            text = {
                                Subtitle1(
                                    text = "x$it",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = if (speed == it) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Unspecified
                                    }
                                )
                            },
                            onClick = {
                                VibrateUtil(context).single()
                                speed = it
                                val playbackParameters = PlaybackParameters(speed, 1.0f)
                                exoPlayer.playbackParameters = playbackParameters
                                speedExpend = false
                            }
                        )
                    }
                }
            }

            //倍速
            IconTextButton(
                text = stringResource(id = R.string.video_play_speed) + " x" + speed.toString(),
                icon = MainIconType.VIDEO_SPEED,
                modifier = Modifier.padding(top = Dimen.smallPadding)
            ) {
                speedExpend = !speedExpend
            }

            //下载视频
            val videoLoading = stringResource(id = R.string.wait_video_load)
            val videoError = stringResource(R.string.video_resource_error)
            if (!downloading) {
                IconTextButton(
                    text = stringResource(id = if (saved) R.string.downloaded_video else R.string.download_video),
                    icon = if (saved) MainIconType.DOWNLOAD_DONE else MainIconType.DOWNLOAD,
                    modifier = Modifier.padding(
                        start = Dimen.largePadding,
                        top = Dimen.smallPadding
                    )
                ) {
                    if (saved) {
                        ToastUtil.short(
                            getString(
                                R.string.video_exist,
                                File(targetFile).absolutePath.replace(VideoDownloadHelper.DIR, "")
                            )
                        )
                        return@IconTextButton
                    }

                    if (loading) {
                        ToastUtil.short(videoLoading)
                    } else if (playError) {
                        ToastUtil.short(videoError)
                    } else {
                        openDialog.value = true
                    }
                }
            } else {
                //下载中
                Row(
                    modifier = Modifier
                        .padding(
                            start = Dimen.largePadding,
                            top = Dimen.smallPadding,
                        )
                        .clip(MaterialTheme.shapes.small)
                        .padding(Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressCompose(
                        size = Dimen.textIconSize,
                        strokeWidth = Dimen.smallStrokeWidth
                    )

                    Text(
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        text = stringResource(id = R.string.title_download_file),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        //浏览器中查看
        if (BuildConfig.DEBUG) {
            IconTextButton(
                text = stringResource(id = R.string.open_browser),
                icon = MainIconType.BROWSER,
                modifier = Modifier.align(Alignment.End)
            ) {
                BrowserUtil.open(url)
            }
        }
    }

    //下载确认
    val videoDownloadError = stringResource(R.string.download_failure)
    MainAlertDialog(
        openDialog = openDialog,
        icon = MainIconType.DOWNLOAD,
        title = stringResource(R.string.download_video),
        text = stringResource(R.string.tip_save_to_gallery),
        onDismissRequest = {
            openDialog.value = false
        }
    ) {
        downloading = true
        openDialog.value = false
        //开始下载
        VideoDownloadHelper(MyApplication.context).download(
            url,
            fileName,
            lifecycleOwner,
            onFinished = {
                downloading = false
            },
            onDownloadFailure = {
                downloading = false
                ToastUtil.short(videoDownloadError)
            }
        )
    }

}

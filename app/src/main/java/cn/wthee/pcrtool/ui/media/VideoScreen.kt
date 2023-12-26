package cn.wthee.pcrtool.ui.media

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
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
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainAlertDialog
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.RATIO
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.MediaDownloadHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.checkPermissions
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.utils.getString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File

/**
 * 视频播放页面
 *
 * @param unitId 角色编号
 * @param videoTypeValue 视频类型 [cn.wthee.pcrtool.data.enums.VideoType]
 */
@Composable
fun VideoScreen(
    unitId: Int,
    videoTypeValue: Int,
) {
    val videoType = VideoType.getByValue(videoTypeValue)
    val urlList = ImageRequestHelper.getInstance().getMovieUrlList(unitId, videoType)
    val typeName = stringResource(id = videoType.typeName)


    MainScaffold {
        MediaGridList(urlList = urlList, title = typeName) {
            VideoPlayer(url = it)
        }
    }

}


/**
 * 视频播放
 *
 * @param url 视频链接
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(url: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    //视频比例
    val ratio = when {
        url.contains("skill") -> 960f / 720
        else -> RATIO
    }

    //加载中
    var loading by remember(url) {
        mutableStateOf(true)
    }
    //播放中
    var playing by remember(url) {
        mutableStateOf(false)
    }
    //播放错误
    var playError by remember(url) {
        mutableStateOf(false)
    }
    //工具栏展开
    var expanded by remember(url) {
        mutableStateOf(false)
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
                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        super.onIsLoadingChanged(isLoading)
                        loading = isLoading
                        if (isPlaying) {
                            loading = false
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        playing = isPlaying
                        if (isPlaying) {
                            loading = false
                        }
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

    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        ),
        onClick = {
            expanded = !expanded
        }
    ) {
        //播放器组件
        MainPlayView(
            exoPlayer = exoPlayer,
            ratio = ratio,
            loading = loading,
            playError = playError,
        )

        //功能按钮
        ExpandAnimation(expanded) {
            Column {
                ToolButtonContent(
                    url = url,
                    exoPlayer = exoPlayer,
                    loading = loading,
                    playing = playing,
                    playError = playError
                )
            }
        }
    }

}

/**
 * 播放控制、保存等
 */
@kotlin.OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ToolButtonContent(
    url: String,
    exoPlayer: ExoPlayer,
    loading: Boolean,
    playing: Boolean,
    playError: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    //下载弹窗
    val openDialog = remember { mutableStateOf(false) }
    //是否已存在
    var saved by remember {
        mutableStateOf(false)
    }
    //下载状态
    var downloading by remember(url) {
        mutableStateOf(false)
    }

    //判断是否已存在
    val fileName = getVideoFileName(url)
    val targetFile =
        File(MediaDownloadHelper.getSaveDir(isVideo = true) + File.separator + fileName)
    if (targetFile.exists()) {
        saved = true
    }

    //播放按钮控制
    var play by remember(url) {
        mutableStateOf(false)
    }
    if (playing) {
        play = true
    }

    //播放进度 fixme 性能优化
    val currentPosition = currentDurationFlow(exoPlayer).collectAsState(initial = 0L).value
    //当前进度
    val current =
        (currentPosition / 1000).toString().fillZero(2)
    //视频长度
    val duration = if (exoPlayer.duration < 0) {
        "00.00"
    } else {
        (exoPlayer.duration / 1000).toString().fillZero(2)
    }

    //播放倍速
    val speedValueList = listOf(
        0.25f, 0.5f, 0.75f, 1f, 2f, 4f, 8f
    )
    var selectedSpeed by remember(url) {
        mutableFloatStateOf(1f)
    }
    exoPlayer.setPlaybackSpeed(selectedSpeed)

    //功能按钮
    Row(
        modifier = Modifier.padding(top = Dimen.smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!playError) {
            //播放进度
            val progress = "00:$current / 00:$duration"

            //播放/暂停
            if (play) {
                IconTextButton(
                    text = progress,
                    icon = MainIconType.VIDEO_PAUSE
                ) {
                    play = false
                    exoPlayer.pause()
                }
            } else {
                IconTextButton(
                    text = progress,
                    icon = MainIconType.VIDEO_PLAY
                ) {
                    play = true
                    exoPlayer.play()
                }
            }
        }

        //浏览器中查看
        if (BuildConfig.DEBUG) {
            IconTextButton(
                text = "",
                icon = MainIconType.BROWSER,
            ) {
                BrowserUtil.open(url)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        //下载视频
        val videoLoading = stringResource(id = R.string.wait_video_load)
        val videoError = stringResource(R.string.video_resource_error)
        if (!downloading) {
            IconTextButton(
                text = stringResource(id = if (saved) R.string.saved else R.string.download_video),
                icon = if (saved) MainIconType.DOWNLOAD_DONE else MainIconType.DOWNLOAD,
                modifier = Modifier.padding(start = Dimen.smallPadding)
            ) {
                //权限校验
                checkPermissions(context, permissions) {
                    //已保存
                    if (saved) {
                        ToastUtil.short(
                            getString(
                                R.string.video_exist,
                                targetFile.absolutePath.replace(MediaDownloadHelper.DIR, "")
                            )
                        )
                        return@checkPermissions
                    }

                    if (loading) {
                        ToastUtil.short(videoLoading)
                    } else if (playError) {
                        ToastUtil.short(videoError)
                    } else {
                        openDialog.value = true
                    }
                }
            }
        } else {
            //下载中
            Row(
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
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

    //倍速选择
    FlowRow(
        modifier = Modifier.padding(bottom = Dimen.smallPadding),
        verticalArrangement = Arrangement.Center
    ) {
        SelectText(
            selected = false,
            text = stringResource(id = R.string.video_play_speed),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(Dimen.smallPadding),
            margin = 0.dp,
            textColor = MaterialTheme.colorScheme.primary
        )
        speedValueList.forEach {
            SelectText(
                selected = it == selectedSpeed,
                text = it.toString(),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(Dimen.smallPadding),
                margin = 0.dp
            ) {
                selectedSpeed = it
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
        MediaDownloadHelper(MyApplication.context).downloadVideo(
            url,
            fileName,
            lifecycleOwner,
            onDownloadFinished = {
                downloading = false
            },
            onDownloadFailure = {
                downloading = false
                ToastUtil.short(videoDownloadError)
            }
        )
    }
}

/**
 * 播放器
 */
@OptIn(UnstableApi::class)
@Composable
private fun MainPlayView(
    exoPlayer: ExoPlayer,
    ratio: Float,
    loading: Boolean,
    playError: Boolean
) {
    val context = LocalContext.current

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


/**
 * 获取文件名
 */
private fun getVideoFileName(url: String): String {
    return try {
        val type = when {
            url.contains(ImageRequestHelper.CARD_MOVIE) -> "card"
            url.contains(ImageRequestHelper.SKILL_MOVIE) -> "skill"
            else -> Constants.UNKNOWN
        } + "_"
        type + url.split('/').last().split('.')[0] + ".mp4"
    } catch (e: Exception) {
        System.currentTimeMillis().toString()
    }
}

/**
 * 播放进度
 */
private fun currentDurationFlow(player: ExoPlayer) = flow {
    val offset = 25L
    if (player.isPlaying) {
        while (player.currentPosition + offset <= player.duration) {
            Log.e("play position", "${player.currentPosition + offset} / ${player.duration}")
            delay(50L)
            emit(player.currentPosition + offset)
        }
        player.seekToDefaultPosition()
    }
}.conflate()
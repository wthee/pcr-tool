package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.VideoType
import cn.wthee.pcrtool.navigation.navigateUpSheet
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.coroutines.launch

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
                    VideoPlayer(url = it, ratio = ratio)
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
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun VideoPlayer(
    url: String,
    ratio: Float
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var loading by remember(url) {
        mutableStateOf(true)
    }
    val speedList = listOf(0.25f, 0.5f, 1f, 2f, 4f)
    var speed by remember(url) {
        mutableFloatStateOf(1f)
    }
    var speedExpend by remember(url) {
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
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        loading = !isPlaying
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
                        StyledPlayerView(context).apply {
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
            }
        }

        Row(modifier = Modifier.align(Alignment.End)) {
            //倍速列表
            PCRToolComposeTheme(shapes = MaterialTheme.shapes.copy(extraSmall = MaterialTheme.shapes.medium)) {
                DropdownMenu(
                    expanded = speedExpend,
                    onDismissRequest = {
                        speedExpend = false
                    }
                ) {
                    //返回
                    IconTextButton(
                        text = stringResource(id = R.string.video_play_speed),
                        icon = MainIconType.BACK,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        speedExpend = false
                    }
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
                                    text = it.toString(),
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
                text = speed.toString(),
                icon = MainIconType.VIDEO_SPEED,
                modifier = Modifier.padding(top = Dimen.smallPadding)
            ) {
                speedExpend = !speedExpend
            }

            //浏览器中查看
            IconTextButton(
                text = stringResource(id = R.string.open_browser),
                icon = MainIconType.BROWSER,
                modifier = Modifier.padding(start = Dimen.mediumPadding, top = Dimen.smallPadding)
            ) {
                BrowserUtil.open(url)
            }
        }
    }

}
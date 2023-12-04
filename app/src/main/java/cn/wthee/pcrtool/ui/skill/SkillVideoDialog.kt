package cn.wthee.pcrtool.ui.skill

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.theme.Dimen
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


/**
 * ub 技能动画预览
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillVideoDialog(
    unitId: Int
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    var loading by remember {
        mutableStateOf(true)
    }
    val speedList = listOf(0.1f, 0.25f, 0.5f, 1f, 2f, 4f)
    var speed by remember {
        mutableFloatStateOf(1f)
    }
    var speedExpend by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    //打开弹窗
    IconTextButton(icon = MainIconType.MOVIE, text = stringResource(R.string.ub_video)) {
        openDialog = true
    }

    //视频预览弹窗
    if (openDialog) {
        val url = ImageRequestHelper.getInstance().getSkillMovieUrl(unitId)
        val mediaSource = ProgressiveMediaSource.Factory(
            CacheDataSource.Factory()
                .setCache(MyApplication.simpleCache)
                .setUpstreamDataSourceFactory(
                    DefaultHttpDataSource.Factory()
                )
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        ).createMediaSource(MediaItem.fromUri(url))

        val exoPlayer = remember(context, mediaSource) {
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


        AlertDialog(
            onDismissRequest = {
                exoPlayer.release()
                openDialog = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .widthIn(max = Dimen.videoMaxWidth)
                .padding(Dimen.largePadding)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = {
                            StyledPlayerView(context).apply {
                                useController = false
                                player = exoPlayer
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ratio = 960f / 720)
                    )

                    if (loading) {
                        CircularProgressCompose()
                    }
                }

                //浏览器中查看
                MainCard(
                    fillMaxWidth = false,
                    modifier = Modifier
                        .padding(top = Dimen.mediumPadding, end = Dimen.smallPadding)
                        .align(Alignment.End)
                ) {
                    Row {
                        DropdownMenu(
                            expanded = speedExpend,
                            onDismissRequest = {}
                        ) {
                            speedList.forEach {
                                DropdownMenuItem(
                                    text = { CaptionText(text = it.toString()) },
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

                        IconTextButton(
                            text = "x$speed",
                            icon = MainIconType.CHANGE_DATA,
                        ) {
                            speedExpend = !speedExpend
                        }

                        IconTextButton(
                            text = stringResource(id = R.string.open_browser),
                            icon = MainIconType.BROWSER,
                        ) {
                            BrowserUtil.open(url)
                        }
                    }

                }
            }
        }
    }

}
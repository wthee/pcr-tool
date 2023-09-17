package cn.wthee.pcrtool.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SettingSwitchType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.HeaderText
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LinearProgressCompose
import cn.wthee.pcrtool.ui.components.MainButton
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.skill.ColorTextIndex
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.ui.tool.SettingCommonItem
import cn.wthee.pcrtool.ui.tool.SettingSwitchCompose
import cn.wthee.pcrtool.utils.ApkDownloadWorker
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.joinQQGroup
import cn.wthee.pcrtool.viewmodel.NoticeViewModel


/**
 * 顶部工具栏
 *
 */
@Composable
fun TopBarCompose(
    isEditMode: MutableState<Boolean>,
    noticeViewModel: NoticeViewModel = MainActivity.noticeViewModel
) {
    //应用更新通知
    val updateAppFlow = remember {
        noticeViewModel.updateApp
    }
    val updateApp by updateAppFlow.observeAsState(initial = AppNotice())
    //应用安装包下载状态
    val downloadState = MainActivity.navViewModel.apkDownloadProgress.observeAsState().value ?: -2
    //展开布局
    var isExpanded by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(updateApp) {
        if (updateApp.id == -1) {
            isExpanded = false
        }
    }

    //Toolbar
    if (downloadState <= -2) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    top = Dimen.largePadding,
                    start = Dimen.largePadding,
                    end = Dimen.largePadding
                )
                .fillMaxWidth()
        ) {
            HeaderText(
                text = stringResource(id = R.string.app_name)
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //应用更新
                when (updateApp.id) {
                    -1 -> {
                        //校验更新中
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(Dimen.fabIconSize)
                                .padding(Dimen.smallPadding),
                            color = MaterialTheme.colorScheme.onSurface,
                            strokeWidth = 3.dp
                        )
                    }

                    -2 -> {
                        //异常
                        MainIcon(
                            data = MainIconType.REQUEST_ERROR,
                            tint = colorRed,
                            size = Dimen.fabIconSize,
                            modifier = Modifier.padding(start = Dimen.smallPadding)
                        ) {
                            isExpanded = !isExpanded
                        }
                    }

                    else -> {
                        //提示
                        if (updateApp.id == 0 && !isExpanded) {
                            IconTextButton(
                                icon = MainIconType.APP_UPDATE,
                                text = stringResource(R.string.find_new_release, updateApp.title),
                                contentColor = colorGreen,
                                iconSize = Dimen.fabIconSize
                            ) {
                                isExpanded = !isExpanded
                            }
                        } else {
                            MainIcon(
                                data = if (isExpanded) MainIconType.CLOSE else MainIconType.NOTICE,
                                tint = MaterialTheme.colorScheme.onSurface,
                                size = Dimen.fabIconSize,
                                modifier = Modifier.padding(start = Dimen.smallPadding)
                            ) {
                                isExpanded = !isExpanded
                            }
                        }

                    }
                }
                Spacer(modifier = Modifier.width(Dimen.largePadding))
                //编辑
                MainIcon(
                    data = if (isEditMode.value) MainIconType.OK else MainIconType.EDIT_TOOL,
                    tint = MaterialTheme.colorScheme.onSurface,
                    size = Dimen.fabIconSize
                ) {
                    isEditMode.value = !isEditMode.value
                }
                Spacer(modifier = Modifier.width(Dimen.largePadding))
            }

        }
    }

    //更新卡片布局
    ExpandAnimation(visible = isExpanded || updateApp.id == -2 || downloadState > -2) {
        AppUpdateContent(appNotice = updateApp, downloadState)
    }
}

/**
 * 应用更新内容或异常提示
 */
@Composable
private fun AppUpdateContent(appNotice: AppNotice, downloadState: Int) {
    val downloading = downloadState > -2
    //下载、安装状态通知
    when (downloadState) {
        -3 -> {
            ToastUtil.long(stringResource(R.string.download_apk_error))
            MainActivity.navViewModel.apkDownloadProgress.postValue(-2)
        }

        -4 -> {
            ToastUtil.long(stringResource(R.string.install_apk_error))
            MainActivity.navViewModel.apkDownloadProgress.postValue(-2)
        }
    }


    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(defaultSpring())
    ) {
        if (downloading) {
            HeaderText(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .padding(
                        top = Dimen.largePadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
            )
            Spacer(modifier = Modifier.weight(1f))
            //下载进度
            if (downloadState > -1) {
                CaptionText(
                    text = "$downloadState%",
                    modifier = Modifier.padding(
                        top = Dimen.largePadding
                    )
                )
            }

        }

        MainCard(
            modifier = if (downloading) {
                Modifier
                    .padding(
                        top = Dimen.largePadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
            } else {
                Modifier
                    .padding(
                        top = Dimen.largePadding,
                        bottom = Dimen.mediumPadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
            },
            fillMaxWidth = !downloading
        ) {
            if (appNotice.id != -2) {
                //下载相关
                if (downloading) {
                    DownloadingContent(downloadState)
                } else {
                    UpdateContent(appNotice)
                }
            } else {
                //异常
                ErrorContent()
            }
        }
    }

}

/**
 * 下载进度
 */
@Composable
private fun DownloadingContent(
    downloadState: Int
) {
    when (downloadState) {
        -1, 100 -> {
            LinearProgressCompose(
                modifier = Modifier.fillMaxWidth(1 - RATIO_GOLDEN),
                color = colorGreen
            )
        }

        in 0..99 -> {
            LinearProgressCompose(
                modifier = Modifier.fillMaxWidth(1 - RATIO_GOLDEN),
                progress = downloadState / 100f,
                color = colorGreen
            )
        }
    }
}

/**
 * 更新内容
 */
@Composable
private fun UpdateContent(
    appNotice: AppNotice
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.smallPadding
        )
    ) {
        //更新内容
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            //版本
            Text(
                text = "v${appNotice.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.weight(1f))
            //反馈群
            IconTextButton(
                icon = MainIconType.SUPPORT,
                text = stringResource(id = R.string.qq_group),
            ) {
                joinQQGroup(context)
            }
        }

        //日期
        CaptionText(
            text = stringResource(
                id = R.string.release, appNotice.date.formatTime
            )
        )

        //内容
        ColorText(appNotice.message)

        //前往更新
        if (appNotice.id == 0) {
            Column(
                modifier = Modifier
                    .padding(top = Dimen.largePadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //github下载链接
                val githubReleaseUrl = stringResource(id = R.string.apk_url, appNotice.title)
                //从GitHub下载
                IconTextButton(
                    text = stringResource(id = R.string.download_apk_from_github),
                    contentColor = MaterialTheme.colorScheme.outline
                ) {
                    downloadApk(githubReleaseUrl, context)
                }

                MainButton(
                    text = stringResource(id = R.string.download_apk),
                    containerColor = colorGreen,
                    modifier = Modifier.fillMaxWidth(RATIO_GOLDEN)
                ) {
                    if (appNotice.url.contains("coolapk")) {
                        //从酷安下载
                        BrowserUtil.open(appNotice.url)
                    } else {
                        //直接从服务器下载
                        downloadApk(appNotice.url, context)
                    }

                }

            }
        }

    }

}

/**
 * 下载文件
 */
private fun downloadApk(
    url: String,
    context: Context
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        BrowserUtil.open(url)
    } else {
        //请求应用安装权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                ToastUtil.long(getString(R.string.request_install))
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data =
                    Uri.parse(
                        java.lang.String.format(
                            "package:%s",
                            context.packageName
                        )
                    )
                context.startActivity(intent)
                return
            }
        }

        //准备下载
        MainActivity.navViewModel.apkDownloadProgress.postValue(-1)
        //下载
        val data = Data.Builder()
            .putString(ApkDownloadWorker.KEY_URL, url)
            .build()
        val updateApkRequest =
            OneTimeWorkRequestBuilder<ApkDownloadWorker>()
                .setInputData(data)
                .build()
        WorkManager.getInstance(MyApplication.context).enqueueUniqueWork(
            Constants.DOWNLOAD_APK_WORK,
            ExistingWorkPolicy.KEEP,
            updateApkRequest
        )
    }
}

/**
 * 优化字体风格
 */
@Composable
private fun ColorText(message: String) {
    val mark0 = arrayListOf<ColorTextIndex>()
    message.forEachIndexed { index, c ->
        if (c == '[') {
            mark0.add(ColorTextIndex(start = index))
        }
        if (c == ']') {
            mark0[mark0.size - 1].end = index
        }
    }

    Text(
        text = buildAnnotatedString {
            message.forEachIndexed { index, char ->
                //替换括号及括号内字体颜色
                mark0.forEach {
                    if (index >= it.start && index <= it.end) {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(char)
                        }
                        return@forEachIndexed
                    }
                }
                //添加非括号标记的参数
                append(char)
            }
        },
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(top = Dimen.largePadding, bottom = Dimen.mediumPadding),
        style = MaterialTheme.typography.bodyLarge,
    )
}

/**
 * 异常内容
 */
@Composable
private fun ErrorContent() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainText(
            text = stringResource(id = R.string.title_api_request_error),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        //内容
        ColorText(stringResource(id = R.string.content_api_request_error))

        //网络异常设置
        SettingSwitchCompose(type = SettingSwitchType.USE_IP, showSummary = true)

        //加群反馈
        SettingCommonItem(iconType = MainIconType.SUPPORT,
            title = stringResource(id = R.string.qq_group),
            summary = stringResource(id = R.string.qq_group_summary),
            onClick = {
                joinQQGroup(context)
            }) {
            Subtitle2(
                text = stringResource(id = R.string.to_join_qq_group),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@CombinedPreviews
@Composable
private fun AppUpdateContentPreview() {
    PreviewLayout {
        AppUpdateContent(
            AppNotice(
                date = "2022-01-01 01:01:01",
                title = "3.2.1",
                message = "- [BUG] BUG\n- [测试] 测试",
                file_url = "123"
            ),
            22
        )
        AppUpdateContent(
            AppNotice(
                id = 0,
                date = "2022-01-01 01:01:01",
                title = "3.2.1",
                message = "- [BUG] BUG\n- [测试] 测试",
                file_url = "123"
            ),
            -2
        )
    }
}
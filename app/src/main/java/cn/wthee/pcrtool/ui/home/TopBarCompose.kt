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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SettingSwitchType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.SettingCommonItem
import cn.wthee.pcrtool.ui.SettingSwitchCompose
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.HeaderText
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LinearProgressCompose
import cn.wthee.pcrtool.ui.components.MainButton
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.SubButton
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
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.DOWNLOAD_APK_NAME
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.joinQQGroup
import cn.wthee.pcrtool.workers.FileDownloadWorker
import java.io.File


/**
 * 顶部工具栏
 *
 */
@Composable
fun TopBarCompose(
    isEditMode: Boolean,
    apkDownloadState: Int,
    updateApp: AppNotice,
    isExpanded: Boolean,
    changeEditMode: () -> Unit,
    updateApkDownloadState: (Int) -> Unit,
    updateAppNoticeLayoutState: (Boolean) -> Unit
) {

    //Toolbar
    if (apkDownloadState <= -2) {
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

            //异常时显示版本号
            if (updateApp.id == -2) {
                CaptionText(text = "v" + BuildConfig.VERSION_NAME)
            }

            //数据版本，测试用
            if (BuildConfig.DEBUG) {
                CaptionText(text = MainActivity.regionType.name)
            }

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
                            strokeWidth = Dimen.strokeWidth
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
                            updateAppNoticeLayoutState(!isExpanded)
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
                                updateAppNoticeLayoutState(true)
                            }
                        } else {
                            MainIcon(
                                data = if (isExpanded) MainIconType.CLOSE else MainIconType.NOTICE,
                                tint = MaterialTheme.colorScheme.onSurface,
                                size = Dimen.fabIconSize,
                                modifier = Modifier.padding(start = Dimen.smallPadding)
                            ) {
                                updateAppNoticeLayoutState(!isExpanded)
                            }
                        }

                    }
                }
                Spacer(modifier = Modifier.width(Dimen.largePadding))
                //编辑
                MainIcon(
                    data = if (isEditMode) MainIconType.OK else MainIconType.EDIT_TOOL,
                    tint = MaterialTheme.colorScheme.onSurface,
                    size = Dimen.fabIconSize
                ) {
                    changeEditMode()
                }
                Spacer(modifier = Modifier.width(Dimen.largePadding))
            }

        }
    }

    //更新卡片布局
    ExpandAnimation(visible = isExpanded || updateApp.id == -2 || apkDownloadState > -2) {
        AppUpdateContent(
            appNotice = updateApp,
            apkDownloadState = apkDownloadState,
            updateApkDownloadState = updateApkDownloadState
        )
    }
}

/**
 * 应用更新内容或异常提示
 */
@Composable
private fun AppUpdateContent(
    appNotice: AppNotice,
    apkDownloadState: Int,
    updateApkDownloadState: (Int) -> Unit
) {
    val downloading = apkDownloadState > -2
    //下载、安装状态通知
    when (apkDownloadState) {
        -3 -> {
            ToastUtil.long(stringResource(R.string.download_apk_error))
            updateApkDownloadState(-2)
        }

        -4 -> {
            ToastUtil.long(stringResource(R.string.install_apk_error))
            updateApkDownloadState(-2)
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
            if (apkDownloadState > -1) {
                CaptionText(
                    text = "$apkDownloadState%",
                    modifier = Modifier.padding(
                        top = Dimen.largePadding
                    )
                )
            }

        }

        MainCard(
            modifier = Modifier.padding(
                top = Dimen.largePadding,
                bottom = if (downloading) 0.dp else Dimen.mediumPadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding,
            ),
            fillMaxWidth = !downloading
        ) {
            if (appNotice.id != -2) {
                if (downloading) {
                    //下载相关
                    DownloadingContent(apkDownloadState)
                } else {
                    if (appNotice.id == -1) {
                        //加载中
                        CircularProgressCompose(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(Dimen.mediumPadding)
                        )
                    } else {
                        //正常展示更新内容
                        UpdateContent(appNotice, updateApkDownloadState)
                    }
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
    apkDownloadState: Int
) {
    when (apkDownloadState) {
        -1, 100 -> {
            LinearProgressCompose(
                modifier = Modifier.fillMaxWidth(1 - RATIO_GOLDEN),
                color = colorGreen
            )
        }

        in 0..99 -> {
            LinearProgressCompose(
                modifier = Modifier.fillMaxWidth(1 - RATIO_GOLDEN),
                progress = apkDownloadState / 100f,
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
    appNotice: AppNotice,
    updateApkDownloadState: (Int) -> Unit
) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

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
        ColorText(message = appNotice.message)

        //前往更新
        if (appNotice.id == 0) {
            //github下载链接
            val githubReleaseUrl = stringResource(id = R.string.apk_url, appNotice.title)

            Row(
                modifier = Modifier
                    .padding(
                        top = Dimen.largePadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                //从GitHub下载
                SubButton(
                    text = stringResource(id = R.string.download_apk_from_github),
                ) {
                    downloadApk(githubReleaseUrl, context, updateApkDownloadState, owner)
                }

                //从服务器下载
                MainButton(
                    text = stringResource(id = R.string.download_apk),
                    containerColor = colorGreen,
                ) {
                    downloadApk(appNotice.url, context, updateApkDownloadState, owner)
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
    context: Context,
    updateApkDownloadState: (Int) -> Unit,
    lifecycleOwner: LifecycleOwner
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
        updateApkDownloadState(-1)
        //下载
        val data = Data.Builder()
            .putString(FileDownloadWorker.KEY_URL, url)
            .putString(FileDownloadWorker.KEY_FILE_NAME, DOWNLOAD_APK_NAME)
            .build()
        val updateApkRequest =
            OneTimeWorkRequestBuilder<FileDownloadWorker>()
                .setInputData(data)
                .build()

        val workManager = WorkManager.getInstance(MyApplication.context)
        workManager.enqueueUniqueWork(
            Constants.DOWNLOAD_APK_WORK,
            ExistingWorkPolicy.KEEP,
            updateApkRequest
        )

        //监听下载进度
        workManager.getWorkInfoByIdLiveData(updateApkRequest.id)
            .observe(lifecycleOwner) { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            //下载成功，安装应用
                            val file =
                                FileUtil.getDownloadDir() + File.separator + DOWNLOAD_APK_NAME
                            openAPK(MyApplication.context, File(file))
                            updateApkDownloadState(-2)
                        }

                        WorkInfo.State.RUNNING -> {
                            val value = workInfo.progress.getInt(Constants.KEY_PROGRESS, -1)
                            updateApkDownloadState(value)
                        }

                        else -> Unit
                    }
                }
            }
    }
}

/**
 * 优化字体风格
 */
@Composable
private fun ColorText(modifier: Modifier = Modifier, message: String) {
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
        modifier = modifier.padding(top = Dimen.largePadding, bottom = Dimen.mediumPadding),
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
        ColorText(message = stringResource(id = R.string.content_api_request_error))

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


/**
 * 安装apk
 */
private fun openAPK(context: Context, apkFile: File) {
    val auth = BuildConfig.APPLICATION_ID + ".provider"
    val type = "application/vnd.android.package-archive"

    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setDataAndType(
        FileProvider.getUriForFile(
            context,
            auth,
            apkFile
        ),
        type
    )
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    context.startActivity(intent)
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
            apkDownloadState = 22,
            updateApkDownloadState = {}
        )
        AppUpdateContent(
            AppNotice(
                id = 0,
                date = "2022-01-01 01:01:01",
                title = "3.2.1",
                message = "- [BUG] BUG\n- [测试] 测试",
                file_url = "123"
            ),
            apkDownloadState = -2,
            updateApkDownloadState = {}
        )
    }
}
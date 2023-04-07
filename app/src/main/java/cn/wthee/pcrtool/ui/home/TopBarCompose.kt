package cn.wthee.pcrtool.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SettingSwitchType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.ColorTextIndex
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.ui.tool.SettingCommonItem
import cn.wthee.pcrtool.ui.tool.SettingSwitchCompose
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.joinQQGroup
import cn.wthee.pcrtool.viewmodel.NoticeViewModel


/**
 * 顶部工具栏
 *
 */
@Composable
fun TopBarCompose(
    isEditMode: MutableState<Boolean>,
    noticeViewModel: NoticeViewModel
) {
    val updateApp = noticeViewModel.updateApp.observeAsState().value ?: AppNotice()
    var isExpanded by remember {
        mutableStateOf(false)
    }

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
                    IconCompose(
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
                    val updateColor =
                        when (updateApp.id) {
                            0 -> colorGreen
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    val icon =
                        when (updateApp.id) {
                            0 -> MainIconType.APP_UPDATE
                            else -> MainIconType.NOTICE
                        }

                    IconCompose(
                        data = if (isExpanded) MainIconType.CLOSE else icon,
                        tint = if (isExpanded) MaterialTheme.colorScheme.onSurface else updateColor,
                        size = Dimen.fabIconSize,
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    ) {
                        isExpanded = !isExpanded
                    }
                }
            }
            Spacer(modifier = Modifier.width(Dimen.largePadding))
            //编辑
            IconCompose(
                data = if (isEditMode.value) MainIconType.OK else MainIconType.EDIT_TOOL,
                tint = MaterialTheme.colorScheme.onSurface,
                size = Dimen.fabIconSize
            ) {
                isEditMode.value = !isEditMode.value
            }
            Spacer(modifier = Modifier.width(Dimen.largePadding))
        }

    }

    ExpandAnimation(visible = isExpanded || updateApp.id == -2) {
        AppUpdateContent(appNotice = updateApp)
    }
}

/**
 * 应用更新内容或异常提示
 */
@Composable
private fun AppUpdateContent(appNotice: AppNotice) {

    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        if (appNotice.id != -2) {
            UpdateContent(appNotice)
        } else {
            ErrorContent()
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
        modifier = Modifier
            .padding(horizontal = Dimen.largePadding)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(top = Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //版本
            Text(
                text = "v${appNotice.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
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
                id = R.string.release,
                appNotice.date.formatTime
            )
        )

        //内容
        ColorText(appNotice.message)

        //前往更新
        if (appNotice.id == 0 || BuildConfig.DEBUG) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    VibrateUtil(context).single()
                    BrowserUtil.open(appNotice.url)
                }
            ) {
                MainContentText(
                    text = stringResource(id = R.string.to_update),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            // 下载 github 上发布的安装包
            val githubReleaseUrl = stringResource(id = R.string.apk_url, appNotice.title)
            IconTextButton(
                icon= MainIconType.DOWNLOAD,
                text = stringResource(id = R.string.download_apk_from_github),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ){
                BrowserUtil.open(githubReleaseUrl)
            }
        }

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
        SettingCommonItem(
            iconType = MainIconType.SUPPORT,
            title = stringResource(id = R.string.qq_group),
            summary = stringResource(id = R.string.qq_group_summary),
            onClick = {
                joinQQGroup(context)
            }
        ) {
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
                id = 0,
                date = "2022-01-01 01:01:01",
                title = "3.2.1",
                message = "- [BUG] BUGBUG\n- [测试] 测试",
                file_url = "123"
            )
        )
    }
}
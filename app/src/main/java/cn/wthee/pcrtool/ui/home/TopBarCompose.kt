package cn.wthee.pcrtool.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.CaptionText
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.MainCard
import cn.wthee.pcrtool.ui.common.MainContentText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.NoticeViewModel


/**
 * 顶部工具栏
 *
 */
@Composable
fun TopBarCompose(actions: NavActions, noticeViewModel: NoticeViewModel) {
    val updateApp = noticeViewModel.updateApp.observeAsState().value ?: AppNotice()
    var isExpanded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(null) {
        noticeViewModel.check()
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
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //应用更新
            if (updateApp.id == -1) {
                //校验更新中
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(Dimen.fabIconSize)
                        .padding(Dimen.smallPadding),
                    color = MaterialTheme.colorScheme.onSurface,
                    strokeWidth = 3.dp
                )
            } else {
                //有更新时显示
                FadeAnimation(updateApp.id == 0) {
                    val updateColor = colorResource(id = R.color.color_rank_21_23)
                    IconCompose(
                        data = if (isExpanded) MainIconType.CLOSE else MainIconType.APP_UPDATE,
                        tint = if (isExpanded) MaterialTheme.colorScheme.onSurface else updateColor,
                        size = Dimen.fabIconSize,
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    ) {
                        isExpanded = !isExpanded
                    }
                }
            }
            Spacer(modifier = Modifier.width(Dimen.largePadding))
            //设置
            IconCompose(
                data = MainIconType.SETTING,
                tint = MaterialTheme.colorScheme.onSurface,
                size = Dimen.fabIconSize
            ) {
                actions.toSetting()
            }
        }

    }

    ExpandAnimation(visible = isExpanded) {
        AppUpdateContent(appNotice = updateApp)
    }
}

//应用更新内容
@Composable
private fun AppUpdateContent(appNotice: AppNotice) {
    val context = LocalContext.current

    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
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
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f)
                )
                //反馈群
                Row(
                    modifier = Modifier
                        .clip(Shape.medium)
                        .clickable {
                            VibrateUtil(context).single()
                            joinQQGroup(context)
                        }
                        .padding(Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconCompose(data = MainIconType.SUPPORT, size = Dimen.smallIconSize)
                    CaptionText(
                        text = stringResource(id = R.string.qq_group),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                }
                //查看 github release 详情
                Row(
                    modifier = Modifier
                        .padding(start = Dimen.largePadding)
                        .clip(Shape.medium)
                        .clickable {
                            VibrateUtil(context).single()
                            openWebView(
                                context,
                                Constants.GITHUB_RELEASE_URL + appNotice.title
                            )
                        }
                        .padding(Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconCompose(data = MainIconType.GITHUB_RELEASE, size = Dimen.smallIconSize)
                    CaptionText(
                        text = stringResource(id = R.string.github),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                }
            }

            //日期
            CaptionText(text = "${appNotice.date.formatTime.substring(0, 10)} 发布")

            //内容
            MainContentText(
                text = appNotice.message,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = Dimen.largePadding, bottom = Dimen.mediumPadding)
            )

            //前往更新
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    VibrateUtil(context).single()
                    openWebView(
                        context,
                        appNotice.url
                    )
                }
            ) {
                MainContentText(
                    text = stringResource(id = R.string.to_update),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}


@Preview
@Composable
private fun AppUpdateContentPreview() {
    PreviewBox {
        AppUpdateContent(
            AppNotice(
                date = "2022-01-01 01:01:01",
                title = "3.2.1",
                message = "- 更新测试\n- BUGBUGBUG",
                file_url = "123"
            )
        )
    }
}
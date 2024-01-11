package cn.wthee.pcrtool.ui.home

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.enums.SettingSwitchType
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag
import cn.wthee.pcrtool.ui.SettingCommonItem
import cn.wthee.pcrtool.ui.SettingSwitchCompose
import cn.wthee.pcrtool.ui.components.AppResumeEffect
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.ExpandableFab
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.home.character.CharacterSection
import cn.wthee.pcrtool.ui.home.equip.EquipSection
import cn.wthee.pcrtool.ui.home.event.EventComingSoonSection
import cn.wthee.pcrtool.ui.home.event.EventInProgressSection
import cn.wthee.pcrtool.ui.home.news.NewsSection
import cn.wthee.pcrtool.ui.home.tool.ToolSection
import cn.wthee.pcrtool.ui.home.uniqueequip.UniqueEquipSection
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.ScaleBottomEndAnimation
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.utils.joinQQGroup
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * 首页纵览
 */
@Composable
fun Overview(
    actions: NavActions,
    overviewScreenViewModel: OverviewScreenViewModel = hiltViewModel()
) {
    val uiState by overviewScreenViewModel.uiState.collectAsStateWithLifecycle()

    //从桌面重新打开时，更新校验
    AppResumeEffect {
        overviewScreenViewModel.initCheck()
    }


    MainScaffold(
        mainFabIcon = if (uiState.showDropMenu || uiState.showChangeDb) MainIconType.CLOSE else MainIconType.SETTING,
        onMainFabClick = {
            overviewScreenViewModel.fabClick()
        },
        fabWithCustomPadding = {
            //数据切换功能
            ChangeDbCompose(
                dbError = uiState.dbError,
                dbVersion = uiState.dbVersion,
                showChangeDb = uiState.showChangeDb,
                downloadState = uiState.dbDownloadState,
                closeAllDialog = overviewScreenViewModel::closeAllDialog,
                updateDbDownloadState = overviewScreenViewModel::updateDbDownloadState,
                updateDbVersionText = overviewScreenViewModel::updateDbVersionText
            ) {
                overviewScreenViewModel.changeDbClick()
            }
        },
        secondLineFab = {
            //菜单
            SettingDropMenu(
                showDropMenu = uiState.showDropMenu,
                closeAllDialog = overviewScreenViewModel::closeAllDialog,
                actions = actions
            )
        },
        enableClickClose = uiState.showDropMenu || uiState.showChangeDb,
        onCloseClick = {
            overviewScreenViewModel.closeAllDialog()
        }
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TopBarCompose(
                isEditMode = uiState.isEditMode,
                appUpdateData = uiState.appUpdateData,
                apkDownloadState = uiState.apkDownloadState,
                isExpanded = uiState.isAppNoticeExpanded,
                updateApkDownloadState = overviewScreenViewModel::updateApkDownloadState,
                changeEditMode = overviewScreenViewModel::changeEditMode,
                updateExpanded = overviewScreenViewModel::updateExpanded,
            )
            if (!uiState.isEditMode) {
                uiState.orderData.intArrayList.forEach {
                    when (OverviewType.getByValue(it)) {
                        OverviewType.CHARACTER -> CharacterSection(
                            toCharacterList = actions.toCharacterList,
                            toCharacterDetail = actions.toCharacterDetail,
                            updateOrderData = overviewScreenViewModel::updateOrderData,
                            isEditMode = false,
                            orderStr = uiState.orderData
                        )

                        OverviewType.EQUIP -> EquipSection(
                            toEquipList = actions.toEquipList,
                            toEquipDetail = actions.toEquipDetail,
                            updateOrderData = overviewScreenViewModel::updateOrderData,
                            isEditMode = false,
                            orderStr = uiState.orderData
                        )

                        OverviewType.TOOL -> ToolSection(
                            updateOrderData = overviewScreenViewModel::updateOrderData,
                            actions = actions,
                            isEditMode = false,
                            orderStr = uiState.orderData
                        )

                        OverviewType.NEWS -> NewsSection(
                            updateOrderData = overviewScreenViewModel::updateOrderData,
                            toNews = actions.toNews,
                            isEditMode = false,
                            orderStr = uiState.orderData
                        )

                        OverviewType.IN_PROGRESS_EVENT -> EventInProgressSection(
                            eventExpandState = uiState.eventExpandState,
                            updateOrderData = overviewScreenViewModel::updateOrderData,
                            updateEventLayoutState = overviewScreenViewModel::updateEventLayoutState,
                            actions = actions,
                            isEditMode = false,
                            orderStr = uiState.orderData
                        )

                        OverviewType.COMING_SOON_EVENT -> EventComingSoonSection(
                            eventExpandState = uiState.eventExpandState,
                            updateOrderData = overviewScreenViewModel::updateOrderData,
                            updateEventLayoutState = overviewScreenViewModel::updateEventLayoutState,
                            actions = actions,
                            isEditMode = false,
                            orderStr = uiState.orderData
                        )

                        OverviewType.UNIQUE_EQUIP -> UniqueEquipSection(
                            toUniqueEquipList = actions.toUniqueEquipList,
                            toUniqueEquipDetail = actions.toUniqueEquipDetail,
                            updateOrderData = overviewScreenViewModel::updateOrderData,
                            isEditMode = false,
                            orderStr = uiState.orderData
                        )
                    }
                }
            } else {
                // 编辑模式显示全部
                Subtitle2(
                    text = stringResource(R.string.tip_click_to_add),
                    modifier = Modifier
                        .padding(vertical = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                //角色
                CharacterSection(
                    toCharacterList = actions.toCharacterList,
                    toCharacterDetail = actions.toCharacterDetail,
                    updateOrderData = overviewScreenViewModel::updateOrderData,
                    isEditMode = true,
                    orderStr = uiState.orderData
                )

                //装备
                EquipSection(
                    toEquipList = actions.toEquipList,
                    toEquipDetail = actions.toEquipDetail,
                    updateOrderData = overviewScreenViewModel::updateOrderData,
                    isEditMode = true,
                    orderStr = uiState.orderData
                )

                //专用装备
                UniqueEquipSection(
                    toUniqueEquipList = actions.toUniqueEquipList,
                    toUniqueEquipDetail = actions.toUniqueEquipDetail,
                    updateOrderData = overviewScreenViewModel::updateOrderData,
                    isEditMode = true,
                    orderStr = uiState.orderData
                )

                //更多功能
                ToolSection(
                    updateOrderData = overviewScreenViewModel::updateOrderData,
                    actions = actions,
                    isEditMode = true,
                    orderStr = uiState.orderData
                )

                //新闻
                NewsSection(
                    updateOrderData = overviewScreenViewModel::updateOrderData,
                    toNews = actions.toNews,
                    isEditMode = true,
                    orderStr = uiState.orderData
                )

                //进行中
                EventInProgressSection(
                    eventExpandState = uiState.eventExpandState,
                    updateOrderData = overviewScreenViewModel::updateOrderData,
                    updateEventLayoutState = overviewScreenViewModel::updateEventLayoutState,
                    actions = actions,
                    isEditMode = true,
                    orderStr = uiState.orderData,
                )

                //活动预告
                EventComingSoonSection(
                    eventExpandState = uiState.eventExpandState,
                    updateOrderData = overviewScreenViewModel::updateOrderData,
                    updateEventLayoutState = overviewScreenViewModel::updateEventLayoutState,
                    actions = actions,
                    isEditMode = true,
                    orderStr = uiState.orderData,
                )

            }

            CommonSpacer()

        }

    }
}


/**
 * 数据切换选择弹窗
 */
@Composable
private fun ChangeDbCompose(
    showChangeDb: Boolean,
    dbError: Boolean,
    dbVersion: DatabaseVersion?,
    downloadState: Int,
    updateDbDownloadState: (Int) -> Unit,
    closeAllDialog: () -> Unit,
    updateDbVersionText: (DatabaseVersion?) -> Unit,
    onClick: () -> Unit
) {

    //远程数据文件异常
    val remoteDbSizeError = downloadState == -3

    //颜色
    val tintColor = if (dbError || remoteDbSizeError) {
        colorRed
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(verticalAlignment = Alignment.Bottom) {
        //数据提示
        if (showChangeDb) {
            DbVersionOtherContent(
                dbError = dbError,
                remoteDbSizeError = remoteDbSizeError,
                dbVersion = dbVersion,
                color = tintColor,
                updateDbDownloadState = updateDbDownloadState,
                closeAllDialog = closeAllDialog,
                updateDbVersionText = updateDbVersionText
            )
        }

        //数据切换
        ExpandableFab(
            paddingValues = PaddingValues(
                start = Dimen.mediumPadding,
                end = Dimen.fabMarginEnd,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin
            ),
            expanded = showChangeDb,
            onClick = {
                if (downloadState <= -2) {
                    onClick()
                }
            },
            customFabContent = {
                //加载相关
                when (downloadState) {
                    -3 -> {
                        MainIcon(
                            data = MainIconType.REMOTE_DB_ERROR,
                            tint = tintColor,
                            size = Dimen.fabIconSize
                        )
                    }

                    -2 -> {
                        FadeAnimation(visible = dbError) {
                            MainIcon(
                                data = MainIconType.DB_ERROR,
                                tint = tintColor,
                                size = Dimen.fabIconSize
                            )
                        }
                        FadeAnimation(visible = !dbError) {
                            MainIcon(
                                data = MainIconType.CHANGE_DATA,
                                tint = tintColor,
                                size = Dimen.fabIconSize
                            )
                        }
                    }

                    in 1..99 -> {
                        CircularProgressCompose(progress = downloadState / 100f)
                    }

                    else -> {
                        CircularProgressCompose()
                    }
                }
            }
        ) {
            DbVersionSelectContent(tintColor)
        }
    }
}

/**
 * 版本选择列表
 */
@Composable
private fun DbVersionSelectContent(
    color: Color
) {
    val coroutineScope = rememberCoroutineScope()
    val region = MainActivity.regionType
    val menuTexts = arrayListOf(
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp),
    )

    Column(
        modifier = Modifier
            .width(Dimen.selectFabMinWidth)
            .padding(bottom = Dimen.smallPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        //版本
        for (i in 0..2) {
            val regionType = RegionType.getByValue(i + 2)
            //是否选中
            val selected = region.value == i + 2

            SelectText(
                selected = selected,
                text = menuTexts[i],
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen.smallPadding)
                    .padding(vertical = Dimen.mediumPadding),
                selectedColor = color
            ) {
                coroutineScope.launch {
                    //正常切换
                    DatabaseUpdater.changeDatabase(regionType)
                }
            }

        }
    }
}

/**
 * 数据切换其他内容
 * 日期、版本、更新内容
 */
@Composable
private fun DbVersionOtherContent(
    dbError: Boolean,
    remoteDbSizeError: Boolean,
    dbVersion: DatabaseVersion?,
    color: Color,
    updateDbDownloadState: (Int) -> Unit,
    closeAllDialog: () -> Unit,
    updateDbVersionText: (DatabaseVersion?) -> Unit
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.commonItemPadding,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            )
    ) {

        //数据更新内容
        DbVersionContentItem(
            title = stringResource(id = R.string.db_diff_content),
            content = if (dbVersion == null || dbVersion.desc == "") {
                stringResource(R.string.db_diff_content_none)
            } else {
                dbVersion.desc
            }
        )

        Spacer(modifier = Modifier.height(Dimen.commonItemPadding * 2))

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .widthIn(min = Dimen.dataChangeWidth + Dimen.iconSize)
        ) {

            //数据更新时间
            DbVersionContentItem(
                title = stringResource(id = R.string.db_diff_time),
                content = if (dbVersion != null && dbVersion.time != "") {
                    dbVersion.time.substring(5, 10).replace("-", "/")
                } else {
                    stringResource(id = R.string.unknown)
                },
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight()
            )

            Spacer(modifier = Modifier.width(Dimen.commonItemPadding))

            //数据版本号
            DbVersionContentItem(
                title = stringResource(id = R.string.db_diff_version),
                content = dbVersion?.truthVersion ?: stringResource(id = R.string.unknown),
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = Dimen.dataChangeWidth)
            )
        }

        Spacer(modifier = Modifier.height(Dimen.commonItemPadding * 2))

        //重新下载数据
        MainCard(
            modifier = Modifier.height(IntrinsicSize.Min),
            fillMaxWidth = false,
            elevation = Dimen.popupMenuElevation,
            onClick = {
                MainScope().launch {
                    //重新下载
                    if (remoteDbSizeError) {
                        joinQQGroup(context)
                    } else {
                        DatabaseUpdater.checkDBVersion(
                            fixDb = true,
                            updateDbDownloadState = updateDbDownloadState,
                            updateDbVersionText = updateDbVersionText,
                        )
                    }
                }
                closeAllDialog()
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.padding(Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainIcon(
                    data = MainIconType.DOWNLOAD,
                    size = Dimen.smallIconSize,
                    tint = color
                )

                Text(
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    text = if (remoteDbSizeError) {
                        stringResource(id = R.string.remote_data_file_error)
                    } else if (dbError) {
                        stringResource(id = R.string.data_file_error)
                    } else {
                        stringResource(id = R.string.data_file_error_desc)
                    },
                    color = color,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }
        }

    }

}

/**
 * 数据切换其他内容项
 */
@Composable
private fun DbVersionContentItem(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    color: Color = MaterialTheme.colorScheme.primary,
    fillMaxWidth: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    MainCard(
        modifier = modifier.height(IntrinsicSize.Min),
        fillMaxWidth = fillMaxWidth,
        elevation = Dimen.popupMenuElevation,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        MainText(
            text = title,
            modifier = Modifier.padding(
                start = Dimen.mediumPadding,
                end = Dimen.mediumPadding,
                top = Dimen.mediumPadding,
                bottom = Dimen.smallPadding,
            ),
            color = color,
            style = MaterialTheme.typography.bodyMedium,
        )
        CaptionText(
            text = content,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(
                    start = Dimen.mediumPadding,
                    end = Dimen.mediumPadding,
                    bottom = Dimen.mediumPadding
                )
        )
    }
}


/**
 * 标题、内容
 */
@Composable
fun Section(
    modifier: Modifier = Modifier,
    id: Int,
    @StringRes titleId: Int,
    iconType: MainIconType? = null,
    hintText: String = "",
    contentVisible: Boolean = true,
    isEditMode: Boolean,
    rightIconType: MainIconType? = null,
    orderStr: String,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current
    //是否已显示到首页
    val hasAdded = orderStr.intArrayList.contains(id) && isEditMode
    //首页排序
    val index = orderStr.intArrayList.indexOf(id)

    val rowModifier = Modifier
        .then(
            if (onClick == null) {
                Modifier
            } else {
                Modifier
                    .padding(horizontal = Dimen.mediumPadding)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(onClick = {
                        VibrateUtil(context).single()
                        if (contentVisible) {
                            onClick()
                        }
                    })
                    .background(
                        color = if (hasAdded) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        shape = MaterialTheme.shapes.medium
                    )
            }
        )
        .padding(Dimen.mediumPadding)


    Column(
        modifier = modifier.then(
            if (animOnFlag) {
                Modifier
                    .padding(top = Dimen.largePadding)
                    .animateContentSize(defaultSpring())
            } else {
                Modifier
                    .padding(top = Dimen.largePadding)
            }
        )
    ) {
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //首页序号，编辑时显示
            if (isEditMode && index != -1) {
                MainText(
                    text = "${index + 1}",
                    modifier = Modifier
                        .padding(horizontal = Dimen.mediumPadding),
                    textAlign = TextAlign.Start,
                    color = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
                )
            }
            if (iconType != null) {
                MainIcon(
                    data = iconType,
                    size = Dimen.fabIconSize,
                    tint = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
                )
            }
            MainText(
                text = stringResource(id = titleId),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Dimen.mediumPadding),
                textAlign = TextAlign.Start,
                color = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
            )
            //更多信息，编辑时隐藏
            if (!isEditMode) {
                Row(
                    modifier = Modifier
                        .padding(start = Dimen.smallPadding, end = Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onClick != null) {
                        if (hintText != "") {
                            Subtitle2(
                                text = hintText,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        MainIcon(
                            data = rightIconType ?: MainIconType.MORE,
                            size = Dimen.fabIconSize,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }
            }
        }

        if (!isEditMode) {
            content()
        }
    }

}


/**
 * 设置页面
 */
@Composable
private fun SettingDropMenu(
    showDropMenu: Boolean,
    closeAllDialog: () -> Unit,
    actions: NavActions
) {

    ScaleBottomEndAnimation(
        visible = showDropMenu,
    ) {
        MainCard(
            fillMaxWidth = false,
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .width(IntrinsicSize.Max)
                .padding(
                    end = Dimen.fabMargin,
                    bottom = Dimen.fabMarginLargeBottom
                ),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Spacer(modifier = Modifier.height(Dimen.mediumPadding))
            SettingSwitchCompose(
                modifier = Modifier.padding(horizontal = Dimen.smallPadding),
                type = SettingSwitchType.VIBRATE,
                showSummary = false,
                wrapWidth = true
            )
            SettingSwitchCompose(
                modifier = Modifier.padding(horizontal = Dimen.smallPadding),
                type = SettingSwitchType.ANIMATION,
                showSummary = false,
                wrapWidth = true
            )
            //- 动态色彩，仅 Android 12 及以上可用
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || BuildConfig.DEBUG) {
                SettingSwitchCompose(
                    modifier = Modifier.padding(horizontal = Dimen.smallPadding),
                    type = SettingSwitchType.DYNAMIC_COLOR,
                    showSummary = false,
                    wrapWidth = true
                )
            }
            Spacer(modifier = Modifier.height(Dimen.mediumPadding))
            SettingCommonItem(
                modifier = Modifier.padding(horizontal = Dimen.smallPadding),
                iconType = R.drawable.ic_launcher_foreground,
                iconSize = Dimen.mediumIconSize,
                title = "v" + BuildConfig.VERSION_NAME,
                summary = stringResource(id = R.string.app_name),
                titleColor = MaterialTheme.colorScheme.primary,
                summaryColor = MaterialTheme.colorScheme.onSurface,
                padding = Dimen.smallPadding,
                tintColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    closeAllDialog()
                    actions.toSetting()
                }
            ) {
                MainIcon(data = MainIconType.MORE, size = Dimen.fabIconSize)
            }
            Spacer(modifier = Modifier.height(Dimen.mediumPadding))
        }
    }
}


@CombinedPreviews
@Composable
private fun ChangeDbComposePreview() {
    PreviewLayout {
        ChangeDbCompose(
            showChangeDb = true,
            dbError = false,
            dbVersion = DatabaseVersion(
                truthVersion = "202302022223",
                hash = "",
                desc = stringResource(id = R.string.debug_name),
                time = "2020-02-02"
            ),
            downloadState = -2,
            updateDbDownloadState = {},
            closeAllDialog = {},
            updateDbVersionText = {}
        ) {

        }

    }
}

@CombinedPreviews
@Composable
private fun ChangeDbCompose2Preview() {
    PreviewLayout {
        ChangeDbCompose(
            showChangeDb = true,
            dbError = true,
            dbVersion = DatabaseVersion(
                truthVersion = "1002342",
                hash = "",
                desc = stringResource(id = R.string.debug_long_text),
                time = "2020-02-02"
            ),
            downloadState = -2,
            updateDbDownloadState = {},
            closeAllDialog = {},
            updateDbVersionText = {}
        ) {

        }
        ChangeDbCompose(
            showChangeDb = false,
            dbError = true,
            dbVersion = DatabaseVersion(
                truthVersion = "202302022223",
                hash = "",
                desc = stringResource(id = R.string.debug_name),
                time = "2020-02-02"
            ),
            downloadState = -2,
            updateDbDownloadState = {},
            closeAllDialog = {},
            updateDbVersionText = {}
        ) {

        }

    }
}
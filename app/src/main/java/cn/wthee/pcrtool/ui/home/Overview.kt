package cn.wthee.pcrtool.ui.home

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.navigation.NavViewModel
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.clickClose
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.home.module.CharacterSection
import cn.wthee.pcrtool.ui.home.module.ComingSoonEventSection
import cn.wthee.pcrtool.ui.home.module.EquipSection
import cn.wthee.pcrtool.ui.home.module.InProgressEventSection
import cn.wthee.pcrtool.ui.home.module.NewsSection
import cn.wthee.pcrtool.ui.home.module.ToolSection
import cn.wthee.pcrtool.ui.home.module.UniqueEquipSection
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private const val DEFAULT_ORDER = "0-1-6-2-3-4-5-"


/**
 * 首页纵览
 */
@Composable
fun Overview(
    actions: NavActions,
    scrollState: ScrollState,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    //初始化加载六星数据
    LaunchedEffect(null) {
        overviewViewModel.getR6Ids()
    }

    //日程点击展开状态
    val confirmState = remember {
        mutableIntStateOf(0)
    }

    //编辑模式
    val isEditMode = remember {
        mutableStateOf(false)
    }

    //自定义显示
    val overviewOrderData = remember {
        context.dataStoreMain.data.map {
            it[MainPreferencesKeys.SP_OVERVIEW_ORDER] ?: DEFAULT_ORDER
        }
    }.collectAsState(initial = DEFAULT_ORDER).value


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            TopBarCompose(isEditMode)
            if (!isEditMode.value) {
                overviewOrderData.intArrayList.forEach {
                    when (OverviewType.getByValue(it)) {
                        OverviewType.CHARACTER -> CharacterSection(
                            actions = actions,
                            isEditMode = false,
                            orderStr = overviewOrderData
                        )

                        OverviewType.EQUIP -> EquipSection(
                            actions = actions,
                            isEditMode = false,
                            orderStr = overviewOrderData
                        )

                        OverviewType.TOOL -> ToolSection(
                            actions = actions,
                            isEditMode = false,
                            orderStr = overviewOrderData
                        )

                        OverviewType.NEWS -> NewsSection(
                            actions = actions,
                            isEditMode = false,
                            orderStr = overviewOrderData
                        )

                        OverviewType.IN_PROGRESS_EVENT -> InProgressEventSection(
                            confirmState = confirmState,
                            actions = actions,
                            isEditMode = false,
                            orderStr = overviewOrderData
                        )

                        OverviewType.COMING_SOON_EVENT -> ComingSoonEventSection(
                            confirmState = confirmState,
                            actions = actions,
                            isEditMode = false,
                            orderStr = overviewOrderData
                        )

                        OverviewType.UNIQUE_EQUIP -> UniqueEquipSection(
                            actions = actions,
                            isEditMode = false,
                            orderStr = overviewOrderData
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
                    actions,
                    isEditMode = true,
                    orderStr = overviewOrderData
                )

                //装备
                EquipSection(
                    actions,
                    isEditMode = true,
                    orderStr = overviewOrderData
                )

                //专用装备
                UniqueEquipSection(
                    actions,
                    isEditMode = true,
                    orderStr = overviewOrderData
                )

                //更多功能
                ToolSection(
                    actions,
                    isEditMode = true,
                    orderStr = overviewOrderData
                )

                //新闻
                NewsSection(
                    actions,
                    isEditMode = true,
                    orderStr = overviewOrderData
                )

                //进行中
                InProgressEventSection(
                    confirmState = confirmState,
                    actions,
                    isEditMode = true,
                    orderStr = overviewOrderData
                )

                //活动预告
                ComingSoonEventSection(
                    confirmState = confirmState,
                    actions,
                    isEditMode = true,
                    orderStr = overviewOrderData
                )

            }

            CommonSpacer()

        }

        //数据切换功能
        ChangeDbCompose(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
        )
    }
}


/**
 * 数据切换选择弹窗
 */
@Composable
private fun ChangeDbCompose(
    modifier: Modifier,
    navViewModel: NavViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val context = LocalContext.current

    var openDialog by remember {
        mutableStateOf(false)
    }
    val downloadState = navViewModel.downloadProgress.observeAsState().value ?: -1
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    //切换数据关闭监听
    if (close) {
        openDialog = false
        navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
        navViewModel.fabCloseClick.postValue(false)
    }

    //展开边距修正
    val mFabModifier = if (openDialog) {
        modifier.padding(start = Dimen.textFabMargin, end = Dimen.textFabMargin)
    } else {
        modifier
    }
    //校验数据文件是否异常
    val dbError by navViewModel.dbError.observeAsState(initial = false)
    //颜色
    val tintColor = if (dbError) {
        colorRed
    } else {
        MaterialTheme.colorScheme.primary
    }


    Box(modifier = Modifier.clickClose(openDialog)) {
        Row(
            modifier = mFabModifier.height(IntrinsicSize.Max),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
            //数据提示
            DbVersionContent(openDialog, dbError, tintColor)

            //数据切换
            SmallFloatingActionButton(
                modifier = mFabModifier
                    .animateContentSize(defaultSpring())
                    .padding(
                        end = Dimen.fabMarginEnd,
                        start = Dimen.mediumPadding,
                        top = Dimen.fabMargin,
                        bottom = Dimen.fabMargin,
                    ),
                shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
                onClick = {
                    //非加载中可点击，加载中禁止点击
                    VibrateUtil(context).single()
                    if (downloadState == -2) {
                        if (!openDialog) {
                            navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                            openDialog = true
                        } else {
                            navViewModel.fabCloseClick.postValue(true)
                        }
                    }
                },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = if (openDialog) {
                        Dimen.popupMenuElevation
                    } else {
                        Dimen.fabElevation
                    }
                ),
            ) {
                if (openDialog) {
                    //选择
                    DbVersionList(tintColor)
                } else {
                    //加载相关
                    when (downloadState) {
                        -2 -> {
                            if (dbError) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = Dimen.largePadding)
                                ) {
                                    MainIcon(
                                        data = MainIconType.DB_ERROR,
                                        tint = tintColor,
                                        size = Dimen.fabIconSize
                                    )
                                    Text(
                                        text = stringResource(R.string.db_error),
                                        style = MaterialTheme.typography.titleSmall,
                                        textAlign = TextAlign.Center,
                                        color = colorRed,
                                        modifier = Modifier.padding(
                                            start = Dimen.mediumPadding,
                                            end = Dimen.largePadding
                                        )
                                    )
                                }
                            } else {
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
            }
        }
    }

}

/**
 * 版本选择列表
 */
@Composable
private fun DbVersionList(
    color: Color,
    navViewModel: NavViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val region = MainActivity.regionType
    val menuTexts = arrayListOf(
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp),
    )

    Column(
        modifier = Modifier
            .width(Dimen.homeDataChangeWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        //版本
        for (i in 0..2) {
            val regionType = RegionType.getByValue(i + 2)
            //是否选中
            val selected = region.value == i + 2

            val mModifier = if (selected) {
                Modifier
                    .fillMaxWidth()
            } else {
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        VibrateUtil(context).single()
                        coroutineScope.launch {
                            //正常切换
                            DatabaseUpdater.changeDatabase(regionType)
                            navViewModel.fabCloseClick.postValue(true)
                        }
                    } .padding(vertical = Dimen.mediumPadding)
            }

            SelectText(
                selected = selected,
                text = menuTexts[i],
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = mModifier
                    .padding(horizontal = Dimen.smallPadding),
                selectedColor = color
            )

        }
    }
}

/**
 * 数据切换其他内容
 */
@Composable
private fun DbVersionContent(
    openDialog: Boolean,
    dbError: Boolean,
    color: Color,
    navViewModel: NavViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val coroutineScope = rememberCoroutineScope()
    val dbVersion = navViewModel.dbVersion.observeAsState().value


    if (openDialog) {
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
            //重新数据下载
            MainCard(
                modifier = Modifier.height(IntrinsicSize.Min),
                fillMaxWidth = false,
                elevation = Dimen.popupMenuElevation,
                onClick = {
                    coroutineScope.launch {
                        //重新下载
                        DatabaseUpdater.checkDBVersion(fixDb = true)
                        navViewModel.fabCloseClick.postValue(true)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainIcon(
                        data = MainIconType.SYNC,
                        size = Dimen.smallIconSize,
                        tint = color
                    )

                    Text(
                        text = (if (dbError) {
                            stringResource(id = R.string.data_file_error)
                        } else {
                            stringResource(id = R.string.none)
                        }) + stringResource(id = R.string.data_file_error_desc),
                        color = color,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimen.commonItemPadding * 2))

            //数据更新内容
            DbVersionContentItem(
                title = stringResource(id = R.string.db_diff_content),
                content = if (dbVersion == null || dbVersion.desc == "") {
                    stringResource(R.string.db_diff_content_none)
                } else {
                    dbVersion.desc
                },
            )

            Spacer(modifier = Modifier.height(Dimen.commonItemPadding * 2))

            Row(
                modifier = Modifier.widthIn(min = Dimen.dataChangeWidth + Dimen.iconSize)
            ) {
                //数据版本
                DbVersionContentItem(
                    title = stringResource(id = R.string.db_diff_version),
                    content = dbVersion?.truthVersion ?: "",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(Dimen.commonItemPadding))

                //数据更新时间
                DbVersionContentItem(
                    title = stringResource(id = R.string.db_diff_time),
                    content = if (dbVersion != null && dbVersion.time != "") {
                        dbVersion.time.substring(5, 10).replace("-", "/")
                    } else {
                        stringResource(id = R.string.unknown)
                    },
                    modifier = Modifier.width(60.dp)
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
        modifier = modifier
            .height(IntrinsicSize.Min),
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
                .weight(1f)
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

    val modifier = if (onClick == null) {
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
                color = if (hasAdded) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
    }


    Column(
        modifier = if (animOnFlag) {
            Modifier
                .padding(top = Dimen.largePadding)
                .animateContentSize(defaultSpring())
        } else {
            Modifier
                .padding(top = Dimen.largePadding)
        }
    ) {
        Row(
            modifier = modifier
                .padding(Dimen.mediumPadding),
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
                        .fillMaxHeight()
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


@CombinedPreviews
@Composable
private fun DbVersionContentItemPreview() {
    PreviewLayout {
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .width(Dimen.dataChangeWidth)
        ) {
            DbVersionContentItem(
                title = stringResource(id = R.string.data_file_error),
                content = stringResource(id = R.string.data_file_error_desc),
                color = colorRed
            )
        }
    }

}


@CombinedPreviews
@Composable
private fun DbVersionListPreview() {
    PreviewLayout {
        DbVersionList(MaterialTheme.colorScheme.primary)
    }
}
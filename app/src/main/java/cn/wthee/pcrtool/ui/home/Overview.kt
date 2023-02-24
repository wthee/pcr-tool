package cn.wthee.pcrtool.ui.home

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.home.module.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.settingSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//权限
val permissions = arrayOf(
    Manifest.permission.READ_CALENDAR,
    Manifest.permission.WRITE_CALENDAR,
)

/**
 * 首页纵览
 */
@Composable
fun Overview(
    actions: NavActions,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    noticeViewModel: NoticeViewModel = hiltViewModel()
) {
    LaunchedEffect(null) {
        overviewViewModel.getR6Ids()
        noticeViewModel.check()
    }

    val coroutineScope = rememberCoroutineScope()
    val openDialog = navViewModel.openChangeDataDialog.observeAsState().value ?: false

    val downloadState = navViewModel.downloadProgress.observeAsState().value ?: -1
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    //切换数据关闭监听
    if (close) {
        navViewModel.openChangeDataDialog.postValue(false)
        navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
        navViewModel.fabCloseClick.postValue(false)
    }

    //添加日历确认弹窗
    val confirmState = remember {
        mutableStateOf(0)
    }

    //编辑模式
    val isEditMode = remember {
        mutableStateOf(false)
    }

    val sp = mainSP()

    //自定义显示
    val localData = sp.getString(Constants.SP_OVERVIEW_ORDER, "0-1-2-3-4-5") ?: ""
    var overviewOrderData = navViewModel.overviewOrderData.observeAsState().value
    if (overviewOrderData == null || overviewOrderData.isEmpty()) {
        overviewOrderData = localData
        navViewModel.overviewOrderData.postValue(overviewOrderData)
    }


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = rememberLazyListState()) {
            item {
                TopBarCompose(isEditMode, noticeViewModel)
            }
            if (!isEditMode.value) {
                overviewOrderData.intArrayList.forEach {
                    item {
                        when (OverviewType.getByValue(it)) {
                            OverviewType.CHARACTER -> CharacterSection(
                                actions = actions,
                                isEditMode = false
                            )
                            OverviewType.EQUIP -> EquipSection(
                                actions = actions,
                                isEditMode = false
                            )
                            OverviewType.TOOL -> ToolSection(
                                actions = actions,
                                isEditMode = false
                            )
                            OverviewType.NEWS -> NewsSection(
                                actions = actions,
                                isEditMode = false
                            )
                            OverviewType.IN_PROGRESS_EVENT -> InProgressEventSection(
                                confirmState,
                                actions = actions, isEditMode = false
                            )
                            OverviewType.COMING_SOON_EVENT -> ComingSoonEventSection(
                                confirmState,
                                actions = actions, isEditMode = false
                            )
                        }
                    }
                }
            } else {
                // 编辑模式显示全部
                item {
                    Subtitle2(
                        text = stringResource(R.string.tip_click_to_add),
                        modifier = Modifier
                            .padding(vertical = Dimen.mediumPadding)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                //角色
                item {
                    CharacterSection(actions, isEditMode = true)
                }

                //装备
                item {
                    EquipSection(actions, isEditMode = true)
                }

                //更多功能
                item {
                    ToolSection(actions, isEditMode = true)
                }

                //新闻
                item {
                    NewsSection(actions, isEditMode = true)
                }

                //进行中
                item {
                    InProgressEventSection(confirmState, actions, isEditMode = true)
                }

                //活动预告
                item {
                    ComingSoonEventSection(confirmState, actions, isEditMode = true)
                }
            }

            item {
                CommonSpacer()
            }
        }

        //数据切换功能
        ChangeDbCompose(
            openDialog,
            downloadState,
            coroutineScope,
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
    openDialog: Boolean,
    downloadState: Int,
    coroutineScope: CoroutineScope,
    modifier: Modifier,
    viewModel: NoticeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val region = MainActivity.regionType
    val menuTexts = arrayListOf(
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp),
    )

    //展开边距修正
    val mFabModifier = if (openDialog) {
        modifier.padding(start = Dimen.textfabMargin, end = Dimen.textfabMargin)
    } else {
        modifier
    }
    //校验数据文件是否异常
    val dbError = FileUtil.dbSizeError(region)
    //颜色
    val tintColor = if (dbError) {
        colorRed
    } else {
        MaterialTheme.colorScheme.primary
    }
    //数据库版本
    val sp = settingSP()
    val localVersion = sp.getString(
        when (region) {
            RegionType.CN -> Constants.SP_DATABASE_VERSION_CN
            RegionType.TW -> Constants.SP_DATABASE_VERSION_TW
            RegionType.JP -> Constants.SP_DATABASE_VERSION_JP
        },
        ""
    )
    val dbVersionCode = if (localVersion != null) {
        localVersion.split("/")[0]
    } else {
        ""
    }
    val updateDb = viewModel.updateDb.observeAsState().value ?: ""
    LaunchedEffect(MainActivity.regionType) {
        viewModel.getDbDiff()
    }


    Row(
        modifier = mFabModifier
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.fabMarginEnd,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            ),
        verticalAlignment = Alignment.Bottom
    ) {

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1f)
        ) {
            //数据异常时显示
            if (openDialog && dbError) {
                MainCard(
                    fillMaxWidth = false,
                    modifier = Modifier.padding(vertical = Dimen.mediumPadding)
                ) {
                    IconTextButton(
                        icon = MainIconType.SYNC,
                        text = stringResource(id = R.string.data_file_error),
                        contentColor = colorRed,
                        iconSize = Dimen.fabIconSize,
                        textStyle = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                    ) {
                        VibrateUtil(context).single()
                        navViewModel.openChangeDataDialog.postValue(false)
                        navViewModel.fabCloseClick.postValue(true)
                        coroutineScope.launch {
                            //数据库文件异常时，重新下载
                            DatabaseUpdater.checkDBVersion(fixDb = true)
                        }
                    }
                }
            }

            if (openDialog) {
                //数据版本相关
                MainCard(fillMaxWidth = false) {
                    IconTextButton(
                        icon = MainIconType.DATA_SOURCE,
                        text = stringResource(
                            id = R.string.db_diff,
                            dbVersionCode,
                            if(updateDb != ""){
                                "\n$updateDb"
                            }else{
                                updateDb
                            }
                        ),
                        iconSize = Dimen.fabIconSize,
                        textStyle = MaterialTheme.typography.titleSmall,
                        maxLines = 4,
                        modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                    )
                }
            }
        }


        //数据切换
        SmallFloatingActionButton(
            modifier = Modifier
                .padding(start = Dimen.mediumPadding),
            shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
            onClick = {
                //非加载中可点击，加载中禁止点击
                VibrateUtil(context).single()
                if (downloadState == -2) {
                    if (!openDialog) {
                        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                        navViewModel.openChangeDataDialog.postValue(true)
                    } else {
                        navViewModel.fabCloseClick.postValue(true)
                    }
                }
            },
        ) {
            if (openDialog) {
                Column(
                    modifier = Modifier
                        .width(Dimen.dataChangeWidth),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                                    navViewModel.openChangeDataDialog.postValue(false)
                                    navViewModel.fabCloseClick.postValue(true)
                                    coroutineScope.launch {
                                        //正常切换
                                        DatabaseUpdater.changeDatabase(regionType)
                                    }
                                }
                        }

                        SelectText(
                            selected = selected,
                            text = menuTexts[i],
                            textStyle = MaterialTheme.typography.titleLarge,
                            modifier = mModifier
                                .padding(Dimen.mediumPadding),
                            selectedColor = tintColor
                        )

                    }
                }
            } else {
                if (downloadState == -2) {
                    IconCompose(
                        data = MainIconType.CHANGE_DATA,
                        tint = tintColor,
                        size = Dimen.fabIconSize
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressCompose()
                        //显示下载进度
                        if (downloadState in 1..99) {
                            Text(
                                text = downloadState.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

    }

}


/**
 * 标题、内容
 */
@Composable
fun Section(
    id: Int,
    @StringRes titleId: Int,
    iconType: MainIconType,
    hintText: String = "",
    contentVisible: Boolean = true,
    isEditMode: Boolean,
    rightIconType: MainIconType? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val orderStr = navViewModel.overviewOrderData.observeAsState().value ?: ""
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
            IconCompose(
                data = iconType,
                size = Dimen.fabIconSize,
                tint = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
            )
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
                        IconCompose(
                            data = rightIconType ?: MainIconType.MORE,
                            size = Dimen.fabIconSize,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }
            }
        }

        if (contentVisible && !isEditMode) {
            Column {
                content()
            }
        }
    }

}


/**
 * 编辑排序
 */
fun editOverviewMenuOrder(id: Int) {
    val sp = mainSP()
    val orderStr = sp.getString(Constants.SP_OVERVIEW_ORDER, "") ?: ""
    val idStr = "$id-"
    val hasAdded = orderStr.intArrayList.contains(id)

    //新增或移除
    val edited = if (!hasAdded) {
        orderStr + idStr
    } else {
        orderStr.replace(idStr, "")
    }
    sp.edit {
        putString(Constants.SP_OVERVIEW_ORDER, edited)
        //更新
        navViewModel.overviewOrderData.postValue(edited)
    }
}

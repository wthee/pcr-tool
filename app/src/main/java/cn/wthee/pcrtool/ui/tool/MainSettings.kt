package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.database.getDatabaseType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.FileUtil.convertFileSize
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import kotlinx.coroutines.launch

/**
 * 设置页面
 */
@ExperimentalAnimationApi
@Composable
fun MainSettings() {
    val context = LocalContext.current
    val type = getDatabaseType()
    val sp = mainSP()
    val scope = rememberCoroutineScope()
    val painter = rememberCoilPainter(request = R.mipmap.ic_launcher_foreground)

    val visibility = remember {
        mutableStateOf(false)
    }
    visibility.value = painter.loadState is ImageLoadState.Success

    SlideAnimation(visible = visibility.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            val expanded = remember {
                mutableStateOf(false)
            }
            val sizeChange =
                animateDpAsState(targetValue = if (expanded.value) 120.dp else 64.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = Dimen.largePadding)
                    .fillMaxWidth()
                    .clickable {
                        expanded.value = !expanded.value
                    }) {
                AnimatedVisibility(visible = expanded.value) {
                    Text(
                        text = stringResource(id = R.string.app_name) + " " + BuildConfig.VERSION_NAME,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(top = Dimen.smallPadding)
                    )
                }
                AnimatedVisibility(visible = expanded.value) {
                    //- 查看项目地址
                    val projectUrl = stringResource(id = R.string.project_url)
                    val project = stringResource(id = R.string.app_sourcce)
                    Subtitle2(
                        text = projectUrl,
                        modifier = Modifier.clickable {
                            openWebView(context, projectUrl, project)
                        })
                }
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(sizeChange.value)
                )
                AnimatedVisibility(visible = !expanded.value) {
                    Subtitle1(
                        text = BuildConfig.VERSION_NAME,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
            //数据库版本
            val localVersion = sp.getString(
                if (type == 1) Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
                ""
            )
            val dbVersionGroup = stringResource(
                id = R.string.data_version, if (localVersion != null) {
                    localVersion.split("/")[0]
                } else {
                    ""
                }
            )

            //数据更新
            MainText(
                text = dbVersionGroup,
                modifier = Modifier.padding(bottom = Dimen.largePadding)
            )
            //- 强制更新
            SettingItem(
                MainIconType.DB_DOWNLOAD,
                stringResource(id = R.string.redownload_db),
                stringResource(id = R.string.redownload_db_summary)
            ) {
                scope.launch {
                    DatabaseUpdater.checkDBVersion(0, force = true)
                }
            }
            //- 历史数据
            val oldFileSize = remember {
                mutableStateOf(FileUtil.getOldDatabaseSize())
            }
            val deleteTip = stringResource(id = R.string.clean_success)
            if (oldFileSize.value > 0) {
                SettingItem(
                    MainIconType.DELETE,
                    stringResource(id = R.string.clean_database),
                    FileUtil.getOldDatabaseSize().convertFileSize()
                ) {
                    FileUtil.deleteOldDatabase()
                    ToastUtil.short(deleteTip)
                    oldFileSize.value = 0
                }
            }
            //其它设置
            LineCompose()
            MainText(
                text = stringResource(id = R.string.other_settings),
                modifier = Modifier.padding(
                    top = Dimen.largePadding,
                    bottom = Dimen.largePadding
                )
            )
            //- 振动开关
            val vibrateOn = sp.getBoolean(Constants.SP_VIBRATE_STATE, true)
            val vibrateState = remember {
                mutableStateOf(vibrateOn)
            }
            MainActivity.vibrateOn = vibrateState.value
            val vibrateSummary =
                stringResource(id = if (vibrateState.value) R.string.vibrate_on else R.string.vibrate_off)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        VibrateUtil(context).single()
                        vibrateState.value = !vibrateState.value
                        sp.edit {
                            putBoolean(Constants.SP_VIBRATE_STATE, vibrateState.value)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(Dimen.mediuPadding))
                IconCompose(
                    data = MainIconType.VIBRATE.icon,
                    size = Dimen.settingIconSize
                )
                Column(
                    modifier = Modifier
                        .padding(Dimen.largePadding)
                        .weight(1f)
                ) {
                    TitleText(text = stringResource(id = R.string.vibrate))
                    SummaryText(text = vibrateSummary)
                }
                Switch(checked = vibrateState.value, onCheckedChange = {
                    vibrateState.value = it
                    sp.edit().putBoolean(Constants.SP_VIBRATE_STATE, it).apply()
                    VibrateUtil(context).single()
                })
                Spacer(modifier = Modifier.width(Dimen.mediuPadding))
            }
            //- 动画效果
            val animOn = sp.getBoolean(Constants.SP_ANIM_STATE, true)
            val animState = remember {
                mutableStateOf(animOn)
            }
            MainActivity.animOn = animState.value
            val animSummary =
                stringResource(id = if (animState.value) R.string.animation_on else R.string.animation_off)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        animState.value = !animState.value
                        sp.edit {
                            putBoolean(Constants.SP_ANIM_STATE, animState.value)
                        }
                        VibrateUtil(context).single()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(Dimen.mediuPadding))
                IconCompose(
                    data = MainIconType.ANIMATION.icon,
                    size = Dimen.settingIconSize
                )
                Column(
                    modifier = Modifier
                        .padding(Dimen.largePadding)
                        .weight(1f)
                ) {
                    TitleText(text = stringResource(id = R.string.animation))
                    SummaryText(text = animSummary)
                }
                Switch(checked = animState.value, onCheckedChange = {
                    animState.value = it
                    sp.edit().putBoolean(Constants.SP_ANIM_STATE, it).apply()
                    VibrateUtil(context).single()
                })
                Spacer(modifier = Modifier.width(Dimen.mediuPadding))
            }
            LineCompose()
            //感谢友链
            MainText(
                text = stringResource(id = R.string.thanks),
                modifier = Modifier.padding(
                    top = Dimen.largePadding,
                    bottom = Dimen.largePadding
                )
            )
            //- 干炸里脊资源
            val dataFromUrl = stringResource(id = R.string.data_from_url)
            SettingItem(
                MainIconType.DATA_SOURCE,
                stringResource(id = R.string.data_from),
                stringResource(id = R.string.data_from_hint),
            ) {
                openWebView(context, dataFromUrl)
            }
            //- 静流笔记
            val shizuruUrl = stringResource(id = R.string.shizuru_note_url)
            SettingItem(
                MainIconType.NOTE,
                stringResource(id = R.string.shizuru_note),
                stringResource(id = R.string.shizuru_note_tip),
            ) {
                openWebView(context, shizuruUrl)
            }
            //- 竞技场
            val pcrdfansUrl = stringResource(id = R.string.pcrdfans_url)
            SettingItem(
                MainIconType.PVP_SEARCH,
                stringResource(id = R.string.pcrdfans),
                stringResource(id = R.string.pcrdfans_tip),
            ) {
                openWebView(context, pcrdfansUrl)
            }
            //- 排行
            val appMediaUrl = stringResource(id = R.string.leader_source_url)
            SettingItem(
                MainIconType.LEADER,
                stringResource(id = R.string.leader_source),
                stringResource(id = R.string.leader_tip),
            ) {
                openWebView(context, appMediaUrl)
            }
            CommonSpacer()
        }
    }

}

@ExperimentalAnimationApi
@Composable
private fun SettingItem(
    iconType: MainIconType,
    title: String,
    summary: String,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick.vibrate {
                VibrateUtil(context).single()
            })
    ) {
        Spacer(modifier = Modifier.width(Dimen.mediuPadding))
        IconCompose(
            data = iconType.icon,
            size = Dimen.settingIconSize
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(Dimen.largePadding)
        ) {
            TitleText(text = title)
            SummaryText(text = summary)
        }
        Spacer(modifier = Modifier.width(Dimen.mediuPadding))
    }
}

@Composable
private fun TitleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    )
}

@Composable
private fun SummaryText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1,
        color = colorResource(id = R.color.gray),
        modifier = Modifier.padding(top = Dimen.mediuPadding)
    )
}
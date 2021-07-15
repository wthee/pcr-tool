package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import cn.wthee.pcrtool.database.getDatabaseType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.openWebView
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

/**
 * 设置页面
 */
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun MainSettings() {
    val context = LocalContext.current
    val type = getDatabaseType()
    val sp = mainSP()
    val visibility = remember {
        mutableStateOf(false)
    }
    val painter = rememberImagePainter(data = R.mipmap.ic_launcher_foreground, builder = {
        listener(onSuccess = { _, _ ->
            visibility.value = true
        })
    })

    SideEffect {
        //自动删除历史数据
        val oldFileSize = FileUtil.getOldDatabaseSize()
        if (oldFileSize > 0) {
            try {
                FileUtil.deleteOldDatabase()
            } catch (e: Exception) {

            }
        }
    }

    //数据库版本
    val typeName = stringResource(
        id = if (type == 1) {
            R.string.db_cn
        } else {
            R.string.db_jp
        }
    )
    val localVersion = sp.getString(
        if (type == 1) Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
        ""
    )
    val dbVersionGroup = if (localVersion != null) {
        localVersion.split("/")[0]
    } else {
        ""
    }

    SlideAnimation(visible = visibility.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = Dimen.largePadding)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.app_name) + " " + BuildConfig.VERSION_NAME,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(top = Dimen.smallPadding)
                )
                //- 查看项目地址
                val projectUrl = stringResource(id = R.string.project_url)
                val project = stringResource(id = R.string.app_sourcce)
                Subtitle2(
                    text = projectUrl,
                    modifier = Modifier.clickable {
                        openWebView(context, projectUrl, project)
                    })
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
                Subtitle2(
                    text = "${typeName}：${dbVersionGroup}",
                    color = MaterialTheme.colors.primary
                )
            }
            //其它设置
            LineCompose()
            MainText(
                text = stringResource(id = R.string.app_setting),
                modifier = Modifier.padding(Dimen.largePadding)
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
                Spacer(modifier = Modifier.width(Dimen.largePadding))
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
                Spacer(modifier = Modifier.width(Dimen.largePadding))
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
                Spacer(modifier = Modifier.width(Dimen.largePadding))
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
                Spacer(modifier = Modifier.width(Dimen.largePadding))
            }
            LineCompose()
            //感谢友链
            MainText(
                text = stringResource(id = R.string.thanks),
                modifier = Modifier.padding(Dimen.largePadding)
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

@ExperimentalCoilApi
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
            .clickable(onClick = {
                VibrateUtil(context).single()
                onClick.invoke()
            })
    ) {
        Spacer(modifier = Modifier.width(Dimen.largePadding))
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
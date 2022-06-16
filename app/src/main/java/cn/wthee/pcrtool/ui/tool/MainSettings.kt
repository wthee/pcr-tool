package cn.wthee.pcrtool.ui.tool

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag
import cn.wthee.pcrtool.ui.MainActivity.Companion.dynamicColorOnFlag
import cn.wthee.pcrtool.ui.MainActivity.Companion.vibrateOnFlag
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.settingSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.switchColors
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.openWebView

/**
 * 设置页面
 */
@Composable
fun MainSettings() {
    val context = LocalContext.current
    val sp = settingSP(context)
    val region = MainActivity.regionType

    SideEffect {
        //自动删除历史数据
        FileUtil.deleteOldDatabase(context)
    }

    //数据库版本
    val typeName = stringResource(
        id = when (region) {
            2 -> R.string.db_cn
            3 -> R.string.db_tw
            else -> R.string.db_jp
        }
    )
    val localVersion = sp.getString(
        when (region) {
            2 -> Constants.SP_DATABASE_VERSION_CN
            3 -> Constants.SP_DATABASE_VERSION_TW
            else -> Constants.SP_DATABASE_VERSION_JP
        },
        ""
    )
    val dbVersionGroup = if (localVersion != null) {
        localVersion.split("/")[0]
    } else {
        ""
    }
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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
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
            ImageCompose(
                data = R.mipmap.ic_launcher_round,
                ratio = 1f,
                modifier = Modifier
                    .size(100.dp)
                    .padding(Dimen.largePadding)
            )
            Subtitle2(
                text = "${typeName}：${dbVersionGroup}",
                color = MaterialTheme.colorScheme.primary
            )
        }
        //其它设置
        LineCompose()
        MainText(
            text = stringResource(id = R.string.app_setting),
            modifier = Modifier.padding(Dimen.largePadding)
        )
        //- 振动开关
        VibrateSetting(sp, context)
        //- 动画效果
        AnimSetting(sp, context)
        //- 动态色彩，仅 Android 12 及以上可用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || BuildConfig.DEBUG) {
            ColorSetting(sp, context)
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

//动态色彩
@Composable
private fun ColorSetting(sp: SharedPreferences, context: Context) {
    val dynamicColorOn = sp.getBoolean(Constants.SP_COLOR_STATE, true)
    val dynamicColorState = remember {
        mutableStateOf(dynamicColorOn)
    }
    dynamicColorOnFlag = dynamicColorState.value
    val dynamicColorSummary =
        stringResource(id = if (dynamicColorState.value) R.string.color_on else R.string.color_off)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                dynamicColorState.value = !dynamicColorState.value
                sp.edit {
                    putBoolean(Constants.SP_COLOR_STATE, dynamicColorState.value)
                }
                VibrateUtil(context).single()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(Dimen.largePadding))
        IconCompose(
            data = MainIconType.COLOR,
            size = Dimen.settingIconSize
        )
        Column(
            modifier = Modifier
                .padding(Dimen.largePadding)
                .weight(1f)
        ) {
            TitleText(text = stringResource(id = R.string.dynamic_color))
            SummaryText(text = dynamicColorSummary)
        }
        Switch(
            checked = dynamicColorState.value,
            colors = switchColors(),
            onCheckedChange = {
                dynamicColorState.value = it
                sp.edit().putBoolean(Constants.SP_COLOR_STATE, it).apply()
                VibrateUtil(context).single()
                MainActivity.handler.sendEmptyMessage(1)
            })
        Spacer(modifier = Modifier.width(Dimen.largePadding))
    }
}

//动画效果
@Composable
private fun AnimSetting(sp: SharedPreferences, context: Context) {
    val animOn = sp.getBoolean(Constants.SP_ANIM_STATE, true)
    val animState = remember {
        mutableStateOf(animOn)
    }
    animOnFlag = animState.value
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
            data = MainIconType.ANIMATION,
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
        Switch(checked = animState.value, colors = switchColors(), onCheckedChange = {
            animState.value = it
            sp.edit().putBoolean(Constants.SP_ANIM_STATE, it).apply()
            VibrateUtil(context).single()
        })
        Spacer(modifier = Modifier.width(Dimen.largePadding))
    }
}

//振动反馈
@Composable
private fun VibrateSetting(
    sp: SharedPreferences,
    context: Context
) {
    val vibrateOn = sp.getBoolean(Constants.SP_VIBRATE_STATE, true)
    val vibrateState = remember {
        mutableStateOf(vibrateOn)
    }
    vibrateOnFlag = vibrateState.value
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
            data = MainIconType.VIBRATE,
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
        Switch(checked = vibrateState.value, colors = switchColors(), onCheckedChange = {
            vibrateState.value = it
            sp.edit().putBoolean(Constants.SP_VIBRATE_STATE, it).apply()
            VibrateUtil(context).single()
        })
        Spacer(modifier = Modifier.width(Dimen.largePadding))
    }
}

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
            data = iconType,
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
        Spacer(modifier = Modifier.width(Dimen.mediumPadding))
    }
}

@Composable
private fun TitleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    )
}

@Composable
private fun SummaryText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = colorResource(id = R.color.gray),
        modifier = Modifier.padding(top = Dimen.mediumPadding)
    )
}

@Preview
@Composable
private fun MainSettingsPreview() {
    PreviewBox(1) {
        MainSettings()
    }
}

@Preview
@Composable
private fun MainSettingsDarkPreview() {
    PreviewBox(2) {
        MainSettings()
    }
}
package cn.wthee.pcrtool.ui.common

import android.Manifest
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.AllPicsViewModel

//权限
val permissions = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
)

//缓存
val loadedPicMap = hashMapOf<String, Drawable?>()


/**
 * 角色所有卡面/剧情故事图片
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllCardList(
    id: Int,
    allPicsType: AllPicsType,
    picsViewModel: AllPicsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    //角色卡面
    val basicUrls = if (allPicsType == AllPicsType.CHARACTER) {
        picsViewModel.getUniCardList(id).collectAsState(initial = arrayListOf()).value
    } else {
        arrayListOf()
    }
    //剧情活动
    val flow = remember(id, allPicsType.type) {
        picsViewModel.getStoryList(id, allPicsType.type)
    }
    val responseData = flow.collectAsState(initial = null).value

    val checkedPicUrl = remember {
        mutableStateOf("")
    }

    LaunchedEffect(MainActivity.navSheetState.currentValue) {
        if (allPicsType == AllPicsType.STORY && MainActivity.navSheetState.isVisible) {
            MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        }
    }

    val hasStory = responseData?.data?.isNotEmpty() == true


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            //角色
            if (allPicsType == AllPicsType.CHARACTER) {
                CharacterPicList(basicUrls, checkedPicUrl)
            }
            //剧情
            StoryPicList(hasStory, responseData, checkedPicUrl)
            CommonSpacer()
        }
    }

    //下载确认
    if (checkedPicUrl.value != "") {
        AlertDialog(
            title = {
                MainText(text = stringResource(R.string.ask_save_image))
            },
            modifier = Modifier.padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding),
            onDismissRequest = {
                checkedPicUrl.value = ""
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
            confirmButton = {
                //确认下载
                MainButton(text = stringResource(R.string.save_image)) {
                    loadedPicMap[checkedPicUrl.value]?.let {
                        FileSaveHelper(context).saveBitmap(
                            bitmap = (it as BitmapDrawable).bitmap,
                            displayName = "${getFileName(checkedPicUrl.value)}.jpg"
                        )
                        checkedPicUrl.value = ""
                    }
                }
            },
            dismissButton = {
                //取消
                SubButton(
                    text = stringResource(id = R.string.cancel)
                ) {
                    checkedPicUrl.value = ""
                }
            })
    }
}

/**
 * 剧情图片
 */
@Composable
private fun StoryPicList(
    hasStory: Boolean,
    responseData: ResponseData<ArrayList<String>>?,
    checkedPicUrl: MutableState<String>
) {
    Row(
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding
            )
            .fillMaxWidth(),
    ) {
        MainTitleText(
            text = stringResource(id = R.string.story)
        )
        Spacer(modifier = Modifier.weight(1f))
        if (hasStory) {
            MainText(text = responseData!!.data!!.size.toString())
        }
    }
    CommonResponseBox(responseData) { data ->
        if (data.isNotEmpty()) {
            CardGridList(
                checkedPicUrl = checkedPicUrl,
                urls = data
            )
        } else {
            CenterTipText(text = stringResource(id = R.string.no_story_info))
        }
    }

}

/**
 * 角色图片
 */
@Composable
private fun CharacterPicList(
    basicUrls: java.util.ArrayList<String>,
    checkedPicUrl: MutableState<String>
) {
    Row(
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTitleText(text = stringResource(id = R.string.basic))
        Spacer(modifier = Modifier.weight(1f))
        MainText(text = basicUrls.size.toString())
    }
    CardGridList(
        checkedPicUrl = checkedPicUrl,
        urls = basicUrls
    )
}

/**
 * 图片网格列表
 */
@Composable
private fun CardGridList(
    checkedPicUrl: MutableState<String>,
    urls: ArrayList<String>
) {
    val context = LocalContext.current
    val unLoadToast = stringResource(id = R.string.wait_pic_load)

    VerticalGrid(spanCount = getItemWidth().spanCount) {
        urls.forEach { picUrl ->
            MainCard(
                modifier = Modifier
                    .padding(horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding),
                onClick = {
                    //下载
                    val loaded = loadedPicMap[picUrl] != null
                    if (loaded) {
                        //权限校验
                        checkPermissions(context, permissions) {
                            checkedPicUrl.value = picUrl
                        }
                    } else {
                        ToastUtil.short(unLoadToast)
                    }
                }
            ) {
                //图片
                SubImageCompose(
                    data = picUrl
                ) {
                    loadedPicMap[picUrl] = it.drawable
                }
            }
        }
    }
}

/**
 * 获取文件名
 */
private fun getFileName(url: String): String {
    return try {
        val type = when {
            url.contains("story") -> "story"
            url.contains("actual_profile") -> "unit_actual"
            else -> "unit"
        } + "_"
        type + url.split('/').last().split('.')[0]
    } catch (e: Exception) {
        System.currentTimeMillis().toString()
    }
}


@CombinedPreviews
@Composable
private fun CharacterPicListPreview() {
    val checkedPicUrl = remember {
        mutableStateOf("")
    }
    PreviewLayout {
        CharacterPicList(arrayListOf("1"), checkedPicUrl)
    }
}
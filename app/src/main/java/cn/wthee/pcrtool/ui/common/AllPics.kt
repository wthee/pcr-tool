package cn.wthee.pcrtool.ui.common

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
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.AllPicsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch


/**
 * 角色所有卡面/剧情故事图片
 * type 0: 角色 1：剧情活动
 */
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun AllPics(id: Int, type: Int, picsViewModel: AllPicsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    //角色卡面
    val basicUrls = if (type == 0) {
        picsViewModel.getUniCardList(id).collectAsState(initial = arrayListOf()).value
    } else {
        arrayListOf()
    }
    //剧情活动
    val storyUrls = picsViewModel.getStoryList(id, type).collectAsState(initial = null).value

    val loadedPicMap = hashMapOf<String, Drawable?>()
    val checkedPicUrl = remember {
        mutableStateOf("")
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (type == 0) {
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
                    MainTitleText(text = "基本")
                    Spacer(modifier = Modifier.weight(1f))
                    MainText(text = basicUrls.size.toString())
                }
                PicGridList(
                    checkedPicUrl = checkedPicUrl,
                    urls = basicUrls,
                    loadedPicMap = loadedPicMap
                )
            }
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
                    text = "剧情"
                )
                Spacer(modifier = Modifier.weight(1f))
                if (storyUrls == null) {
                    CircularProgressIndicator()
                } else {
                    MainText(text = storyUrls.size.toString())
                }
            }
            if (storyUrls != null) {
                if (storyUrls.isEmpty()) {
                    MainText(
                        text = "暂无剧情信息",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(
                                Dimen.largePadding
                            )
                    )
                } else {
                    PicGridList(
                        checkedPicUrl = checkedPicUrl,
                        urls = storyUrls,
                        loadedPicMap = loadedPicMap
                    )
                }
            }
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
            shape = Shape.medium,
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
                    text = stringResource(id = R.string.cancel),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    checkedPicUrl.value = ""
                }
            })
    }
}


@Composable
private fun PicGridList(
    checkedPicUrl: MutableState<String>,
    urls: ArrayList<String>,
    loadedPicMap: MutableMap<String, Drawable?>
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val unLoadToast = stringResource(id = R.string.wait_pic_load)
    val spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px

    VerticalGrid(spanCount = spanCount) {
        urls.forEach { picUrl ->
            val request = coil.request.ImageRequest.Builder(context)
                .data(picUrl)
                .build()
            coroutineScope.launch {
                val image = coil.Coil.imageLoader(context).execute(request).drawable
                loadedPicMap[picUrl] = image
            }
            MainCard(
                modifier = Modifier
                    .padding(Dimen.largePadding),
                onClick = {
                    //下载
                    val loaded = loadedPicMap[picUrl] != null
                    if (loaded) {
                        //权限校验
                        checkPermissions(context) {
                            checkedPicUrl.value = picUrl
                        }
                    } else {
                        ToastUtil.short(unLoadToast)
                    }
                }
            ) {
                //图片
                StoryImageCompose(
                    data = picUrl,
                    loadingId = R.drawable.load,
                    errorId = R.drawable.error
                )
            }
        }
    }
}

/**
 * 获取文件名
 */
private fun getFileName(url: String): String {
    return try {
        url.split('/').last().split('.')[0]
    } catch (e: Exception) {
        System.currentTimeMillis().toString()
    }
}
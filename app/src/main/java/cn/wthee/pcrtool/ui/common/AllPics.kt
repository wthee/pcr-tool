package cn.wthee.pcrtool.ui.common

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
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
 * //TODO 优化获取已加载图片的方法
 */
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun AllPics(id: Int, picsViewModel: AllPicsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val picUrls = picsViewModel.getStoryList(id).collectAsState(initial = null).value
    val loaded = arrayListOf<Boolean>()
    val drawables = arrayListOf<Drawable?>()
    picUrls?.forEach { _ ->
        loaded.add(false)
        drawables.add(null)
    }
    val coroutineScope = rememberCoroutineScope()

    val unLoadToast = stringResource(id = R.string.wait_pic_load)
    val clickedIndex = remember {
        mutableStateOf(-1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (picUrls == null) {
            VerticalGrid(spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px) {
                for (i in 0..5) {
                    Card(
                        modifier = Modifier
                            .padding(Dimen.largePadding)
                            .fillMaxWidth()
                    ) {
                        ImageCompose(
                            data = R.drawable.load,
                            ratio = RATIO,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            if (picUrls.isNotEmpty()) {
                VerticalGrid(spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px) {
                    picUrls.forEachIndexed { index, _ ->
                        val request = coil.request.ImageRequest.Builder(context)
                            .data(picUrls[index])
                            .build()
                        coroutineScope.launch {
                            val image = coil.Coil.imageLoader(context).execute(request).drawable
                            drawables[index] = image
                        }
                        Card(
                            modifier = Modifier
                                .padding(Dimen.largePadding)
                                .fillMaxWidth(),
                            onClick = {
                                VibrateUtil(context).single()
                                //下载
                                if (loaded[index]) {
                                    //权限校验
                                    checkPermissions(context) {
                                        clickedIndex.value = index
                                    }
                                } else {
                                    ToastUtil.short(unLoadToast)
                                }
                            },
                            shape = Shape.medium,
                        ) {
                            //图片
                            ImageCompose(
                                data = picUrls[index],
                                ratio = -1f,
                                loadingId = R.drawable.load,
                                errorId = R.drawable.error,
                            ) {
                                loaded[index] = true
                            }
                        }
                    }
                    CommonSpacer()
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    MainTitleText(text = "暂无信息")
                }
            }

        }

    }

    //下载确认
    if (clickedIndex.value != -1) {
        val index = clickedIndex.value
        AlertDialog(
            title = {
                MainText(text = stringResource(R.string.ask_save_image))
            },
            modifier = Modifier.padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding),
            onDismissRequest = {
                clickedIndex.value = -1
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = Shape.medium,
            confirmButton = {
                //确认下载
                MainButton(text = stringResource(R.string.save_image)) {
                    drawables[index]?.let {
                        FileSaveHelper(context).saveBitmap(
                            bitmap = (it as BitmapDrawable).bitmap,
                            displayName = "${id}_${index}.jpg"
                        )
                        clickedIndex.value = -1
                    }
                }
            },
            dismissButton = {
                //取消
                SubButton(
                    text = stringResource(id = R.string.cancel),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    clickedIndex.value = -1
                }
            })
    }
}

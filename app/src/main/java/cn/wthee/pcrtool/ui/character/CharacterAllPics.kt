package cn.wthee.pcrtool.ui.character

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.utils.*
import coil.Coil
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch


/**
 * 角色卡面图片
 * fixme 优化图片下载方法
 */
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun CharacterAllPics(unitId: Int) {
    val context = LocalContext.current
    val picUrls = CharacterIdUtil.getAllPicUrl(unitId, MainActivity.r6Ids.contains(unitId))
    val loaded = arrayListOf<Boolean>()
    val drawables = arrayListOf<Drawable?>()
    picUrls.forEach { _ ->
        loaded.add(false)
        drawables.add(null)
    }
    val coroutineScope = rememberCoroutineScope()

    val unLoadToast = stringResource(id = R.string.wait_pic_load)
    val clickedIndex = remember {
        mutableStateOf(-1)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(picUrls) { index, _ ->
            val request = ImageRequest.Builder(context)
                .data(picUrls[index])
                .build()
            coroutineScope.launch {
                val image = Coil.imageLoader(context).execute(request).drawable
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
                ImageCompose(data = picUrls[index], ratio = RATIO) {
                    loaded[index] = true
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
            buttons = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.padding(top = Dimen.largePadding * 2))
                    Row(
                        modifier = Modifier
                            .padding(Dimen.largePadding)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        //取消
                        SubButton(text = stringResource(id = R.string.cancel)) {
                            clickedIndex.value = -1
                        }
                        //确认下载
                        MainButton(text = stringResource(R.string.save_image)) {
                            drawables[index]?.let {
                                FileSaveHelper(context).saveBitmap(
                                    bitmap = (it as BitmapDrawable).bitmap,
                                    displayName = "${unitId}_${index}.jpg"
                                )
                                clickedIndex.value = -1
                                VibrateUtil(context).done()
                            }
                        }
                    }
                }
            })
    }
}

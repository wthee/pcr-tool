package cn.wthee.pcrtool.ui.character

import android.Manifest
import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.requestPermissions
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.PagerIndicator
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.ImageDownloadHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.hasPermissions
import coil.Coil
import coil.request.ImageRequest
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


/**
 * 角色图片
 *
 */
@ExperimentalPagerApi
@Composable
fun CharacterAllPicture(unitId: Int) {
    //角色所有图片链接
    val picUrls = CharacterIdUtil.getAllPicUrl(unitId, MainActivity.r6Ids.contains(unitId))
    val loaded = arrayListOf<Boolean>()
    val drawables = arrayListOf<Drawable?>()
    picUrls.forEach { _ ->
        loaded.add(false)
        drawables.add(null)
    }

    val constraintsScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = picUrls.size)
    //权限
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.align(Alignment.Center)
        ) { pagerIndex ->
            val request = ImageRequest.Builder(context)
                .data(picUrls[pagerIndex])
                .target(
                    onStart = { placeholder ->
                        // Handle the placeholder drawable.
                    },
                    onSuccess = { result ->
                        // Handle the successful result.
                    },
                    onError = { error ->
                        // Handle the error drawable.
                    }
                )
                .build()
            constraintsScope.launch {
                val image = Coil.imageLoader(context).execute(request).drawable
                drawables[pagerIndex] = image
            }
            val painter = rememberCoilPainter(request = picUrls[pagerIndex])
            Image(
                painter = when (painter.loadState) {
                    is ImageLoadState.Success -> {
                        loaded[pagerIndex] = true
                        painter
                    }
                    is ImageLoadState.Error -> rememberCoilPainter(request = R.drawable.error)
                    else -> rememberCoilPainter(request = R.drawable.load)
                },
                contentDescription = null,
            )
        }
        //指示器
        PagerIndicator(pagerState, modifier = Modifier.align(Alignment.BottomCenter))

        val unLoadToast = stringResource(id = R.string.wait_pic_load)
        ExtendedFabCompose(
            iconType = MainIconType.DWONLOAD,
            text = stringResource(id = R.string.download_pic),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            constraintsScope.launch {
                val index = pagerState.currentPage
                if (loaded[index]) {
                    //权限校验
                    if (!hasPermissions(context, permissions)) {
                        requestPermissions(context as Activity, permissions, 1)
                    } else {
                        //fixme 保存时卡顿
                        drawables[index]?.let {
                            ImageDownloadHelper(context).save(
                                (it as BitmapDrawable).bitmap,
                                "${unitId}_${index}.jpg"
                            )
                        }
                    }
                } else {
                    ToastUtil.short(unLoadToast)
                }
            }
        }
    }
}


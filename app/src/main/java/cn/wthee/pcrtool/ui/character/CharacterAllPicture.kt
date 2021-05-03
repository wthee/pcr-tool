package cn.wthee.pcrtool.ui.character

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.ImageDownloadHelper
import cn.wthee.pcrtool.utils.ToastUtil
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * 角色图片
 *
 * fixme 保存图片
 */
@ExperimentalPagerApi
@Composable
fun CharacterAllPicture(unitId: Int) {
    val picUrls = CharacterIdUtil.getAllPicUrl(unitId, MainActivity.r6Ids.contains(unitId))
    val loaded = arrayListOf<Boolean>()
    val bitmaps = arrayListOf<Bitmap?>()
    picUrls.forEach { _ ->
        loaded.add(false)
        bitmaps.add(null)
    }
    val constraintsScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = picUrls.size)


    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.align(Alignment.Center)
        ) { pagerIndex ->
            Box {
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
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colors.primary,
            inactiveColor = colorResource(id = R.color.alpha_primary),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimen.sheetMarginBottom + Dimen.largePadding)
        )
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
                    //fixme 权限校验
                    bitmaps[index]?.let {
                        ImageDownloadHelper(context).save(
                            bitmaps[pagerState.currentPage]!!,
                            "${unitId}_$index"
                        )
                    }
                } else {
                    ToastUtil.short(unLoadToast)
                }
            }
        }
    }
}

